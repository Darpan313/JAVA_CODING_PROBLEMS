# File Search Engine — Understanding the Assignment

> Read this top to bottom once. It explains *what* we're building, *why* each design
> decision exists, and *what order* to build it in. No prior knowledge of design
> patterns assumed. A glossary of unfamiliar terms is at the bottom.

---

## Part 1 — What are we actually building?

We're building the engine behind a command like this:

```
find ~/Downloads -name "*.png" -size +1M
```

You give it a **starting folder**. It walks through that folder and every subfolder
inside it, looks at each file, and decides "does this file match what the user asked
for?" If yes, it goes in the results list.

That's it. That's the whole feature.

A concrete run might look like:

```
Search: start at ~/Downloads
        find files that are PNG images
        AND bigger than 1 MB
        AND whose name starts with "screenshot"

Result: 4 files found
        ~/Downloads/screenshot_jan.png
        ~/Downloads/old/screenshot_2.png
        ~/Downloads/old/archive/screenshot_final.png
        ~/Downloads/work/screenshot_bug.png

        142 directories searched
        3,891 files examined
        took 0.42 seconds
```

Simple to describe. The assignment is about building it in a way that's **easy to extend**
and **doesn't waste time on disk operations**.

---

## Part 2 — What is the assignment really testing?

The problem statement is written in dense jargon. Here's the translation:

| What it says | What it means |
|---|---|
| "Extensible filter pipeline" | I should be able to add a brand-new kind of filter (say, "files modified last Tuesday") by writing **one new class**, and touching **nothing else**. |
| "Combining criteria with AND, OR, NOT" | Filters must snap together like LEGO. `(PNG OR JPEG) AND bigger-than-1MB AND NOT in-trash-folder` should be expressible. |
| "Strategy Pattern for traversal" | Depth-first and breadth-first search must be **swappable at runtime** — pick one with a config flag, without `if/else` scattered through the code. |
| "Specification / Composite Pattern for filters" | The LEGO-snapping above has a standard name. See Part 4. |
| "Short-circuiting: cheap filters before expensive ones" | Checking a filename is nearly free. Reading bytes off the disk is ~10,000x slower. So check names first — most files get rejected there and you never touch the disk. |
| "Memory efficiency / avoid loading the whole tree" | Don't build a giant list of all 3 million files and *then* filter it. Process files one at a time as you find them. |
| "Result collection & stats" | Return not just the matches, but how many folders you walked, how long it took, how much memory you used. |
| "SOLID principles" | Mostly a restatement of the above: small classes, each doing one thing, that plug together via interfaces. |

**The real test:** almost anyone can write a working file search in 50 lines. The assignment
is checking whether you can *structure* it so it survives new requirements without being
rewritten — and whether you understand *why disk access is the thing to optimize*.

---

## Part 3 — The mental model: three moving parts

Every design decision below serves one of three components. Hold this picture in your head:

```
                    ┌──────────────────────┐
                    │    SearchEngine      │   ← the coordinator (thin!)
                    │  "for each file the  │
                    │   walker gives me,   │
                    │   ask the filter"    │
                    └──────────┬───────────┘
                               │
              ┌────────────────┴────────────────┐
              │                                 │
   ┌──────────▼──────────┐         ┌────────────▼───────────┐
   │  TRAVERSAL          │         │  FILTERS               │
   │  "how do I walk     │         │  "does this one file   │
   │   the folder tree?" │         │   match?"              │
   │                     │         │                        │
   │  • DepthFirst       │         │  • Extension           │
   │  • BreadthFirst     │         │  • Name / glob         │
   │  • Parallel         │         │  • Size                │
   │                     │         │  • MagicBytes          │
   │  Hands out files    │         │  • AND / OR / NOT      │
   │  one at a time  ────┼────────▶│    (combine the above) │
   └─────────────────────┘         └────────────────────────┘
                    passes a FileEntry
```

- **Traversal** knows *how to walk*. It knows nothing about filters.
- **Filters** know *how to judge one file*. They know nothing about walking.
- **Engine** connects them and counts stats. It knows almost nothing.

Those three staying ignorant of each other is the entire point. It's what lets you add a
new filter without touching traversal, and a new traversal without touching filters.

---

## Part 4 — The five design decisions, and why

### Decision 1: Filters receive a `FileEntry`, not a `Path`

**This is the most important decision in the whole design.** Everything else is
comparatively routine.

Here's the naive version. Each filter takes a file path and figures things out itself:

```java
// ❌ THE TRAP
class SizeFilter {
    boolean matches(Path p) {
        return Files.size(p) > 1_000_000;   // ← asks the OS about this file
    }
}
class MagicBytesFilter {
    boolean matches(Path p) {
        var bytes = readFirst8Bytes(p);      // ← opens the file
        ...
    }
}
```

Looks clean. Now count the disk operations for **one single file**, with a filter chain of
`size > 1MB AND is-PNG AND is-not-corrupt`:

1. The traversal already asked the OS "is this a file or a folder?" → **1 operation**
2. `SizeFilter` asks the OS how big it is → **1 operation**
3. `MagicBytesFilter` opens the file, reads 8 bytes → **1 operation**
4. `NotCorruptFilter` opens the file *again*, reads 8 bytes *again* → **1 operation**

**Four trips to the operating system**, and three of them were avoidable. On a tree with
500,000 files, that's 1.5 million wasted operations, and these are *slow* — thousands of
times slower than anything happening in your CPU.

Why avoidable? Because:
- The traversal **already got the size for free.** When the OS tells you "this is a file,"
  it hands back a whole bundle of info — size, timestamps, permissions — in the same
  response. Java calls this bundle `BasicFileAttributes`. Throwing it away and re-asking
  is pure waste.
- Filters 3 and 4 both wanted the *same first 8 bytes.* They should share one read.

**The fix.** Instead of passing around a bare `Path`, we pass a small object that carries
the path *plus* everything we already learned about it, and that remembers anything
expensive it's forced to look up:

```java
// ✅ THE FIX (sketch — you'll write the real one)
class FileEntry {
    private final Path path;
    private final BasicFileAttributes attrs;  // ← handed in by traversal, already paid for
    private byte[] cachedHeader;              // ← filled in on first request only

    long size()          { return attrs.size(); }          // free — no OS call
    String extension()   { return ...; }                    // free — pure string work
    byte[] header(int n) {
        if (cachedHeader == null) {
            cachedHeader = readFromDisk();                   // happens at most ONCE
        }
        return cachedHeader;
    }
}
```

Now the same three-filter chain costs: **1 operation** (traversal's, which we needed
anyway) **+ 1 read** (shared by both byte-checking filters) = 2 total, down from 4. And
the size check became literally free.

Notice what just happened: **a performance requirement was solved by choosing the right
data type, not by writing clever algorithms.** That's the insight the assignment is fishing
for. If you say one thing on a whiteboard, say this.

---

### Decision 2: Filters are objects that snap together (Specification Pattern)

We need `(PNG OR JPEG) AND bigger-than-1MB AND NOT in-trash`. How?

The naive approach is a config object with a field per option:

```java
// ❌ Doesn't scale, and can't express OR/NOT at all
class SearchConfig {
    String extension;
    long minSize;
    boolean checkMagicBytes;
    // ...20 more fields, and no way to say "OR"
}
```

Every new feature means a new field, plus a new `if` inside the search loop. And there's no
way to express "this OR that."

The pattern that solves this: **make every filter implement the same tiny interface.**

```java
interface SearchCriterion {
    boolean matches(FileEntry entry);
}
```

That's the whole contract: *given a file, yes or no.* Now `ExtensionCriterion`,
`SizeCriterion`, `NameCriterion`, and `MagicBytesCriterion` each implement it.

**Here's the trick that makes it powerful.** AND, OR, and NOT are *also* just filters that
implement the same interface — they simply hold other filters inside them:

```java
class AndCriterion implements SearchCriterion {
    private final List<SearchCriterion> children;

    public boolean matches(FileEntry e) {
        for (var child : children) {
            if (!child.matches(e)) return false;   // one failure = whole thing fails
        }
        return true;
    }
}
```

`AndCriterion` doesn't know or care whether its children are simple extension checks or
themselves giant nested OR trees. Because everything is a `SearchCriterion`, it all
composes:

```
                    AND
                     │
        ┌────────────┼────────────┐
        │            │            │
       OR        size>1MB        NOT
        │                         │
    ┌───┴───┐                 in-trash
   PNG    JPEG
```

That whole tree is *one* `SearchCriterion` from the engine's point of view. The engine calls
`.matches(file)` on it and gets back a single boolean. The engine has no idea there's a tree
in there.

**This is why the design is "extensible."** Want to filter by "modified in the last 7 days"?
Write one class implementing `SearchCriterion`. Done. No other file changes. That property —
*add behavior by adding code, never by editing existing code* — is what "Open/Closed
Principle" means, and it's the O in SOLID.

**A trade-off to be ready to discuss.** Java already has `Predicate<T>`, a built-in interface
with exactly this shape, and it comes with `.and()`, `.or()`, `.negate()` for free. So why
write our own? Because we need one extra thing on the interface — see Decision 3 — plus a
human-readable description for reports. Being able to say *"I considered `Predicate` and here's
specifically why I didn't use it"* is worth more in an interview than the code itself. Never
look like you didn't know the standard option existed.

---

### Decision 3: Sort filters cheapest-first

Two *different* things both get called "short-circuiting." Interviewers notice when
candidates blur them, so keep them separate in your head.

**(a) Bail out on first failure.** In `AndCriterion`, the moment one child returns `false`,
return `false` — don't check the rest. Nearly free to implement, and shown in the code above.

**(b) Control the *order* children are checked in.** This one is the real optimization, and
it's less obvious.

Consider `is-PNG-bytes AND extension-is-.png`, run against 100,000 files where only 500 are
actually `.png`:

- **Bad order** (expensive first): read bytes off disk for all 100,000 files → then check
  extension. **100,000 disk reads.**
- **Good order** (cheap first): check extension for all 100,000 (free, it's just string
  comparison) → 99,500 fail instantly and exit → only 500 survivors get their bytes read.
  **500 disk reads.**

Identical results. **200x less disk work.** Purely from ordering.

So we tag each filter with how expensive it is, by adding a second method to the interface:

```java
interface SearchCriterion {
    boolean matches(FileEntry entry);
    Cost cost();                          // ← FREE, CHEAP, or EXPENSIVE
}
```

- `FREE` — pure string work on the path. Extension, name, glob.
- `CHEAP` — reads a field off the attributes we already have. Size, timestamp.
- `EXPENSIVE` — actually touches the disk. Magic bytes, content search.

Then `AndCriterion` **sorts its children by cost in its constructor** — once, when the query
is built, not once per file. Sorting 3 items per search: irrelevant. Sorting 3 items per file
across a million files: a real bug.

Two subtle points that are excellent interview material:

1. **This reordering is only *legal* because filters are pure** — meaning `matches()` just
   answers a question and changes nothing else. If a filter secretly wrote to a log or
   incremented a counter, shuffling the order would change program behavior. So "filters must
   be side-effect-free" becomes a documented rule of the interface, not an accident. Write it
   in the Javadoc.

2. **Cheap-first is correct for `OR` too**, for the mirror-image reason. `OR` stops at the
   first `true`, so you want the cheap possibility tested first. Same sort, both directions.

---

### Decision 4: Traversal is swappable (Strategy Pattern)

The assignment wants both depth-first and breadth-first walking, chosen by config.

Given this tree:

```
root
├── a
│   └── a1
├── b
│   └── b1
└── c
```

- **Depth-first (DFS)** dives as deep as it can before backing up: `a → a1 → b → b1 → c`.
  It's like exploring a maze by always taking the first corridor and only backtracking at
  dead ends.
- **Breadth-first (BFS)** finishes each level before descending: `a → b → c → a1 → b1`.
  Like ripples spreading out from a stone.

The naive implementation puts a flag in the walking function:

```java
// ❌ Every new traversal order means editing this method
void walk(Path dir, boolean useBFS) {
    if (useBFS) { ...20 lines... } else { ...20 lines... }
}
```

**Strategy Pattern** instead says: put each algorithm in its own class behind a shared
interface, and let the caller pick the object.

```java
interface TraversalStrategy {
    CloseableIterator<FileEntry> iterate(Path root, TraversalConfig cfg, SearchStats stats);
}
```

Then `DepthFirstTraversal`, `BreadthFirstTraversal`, and later `ParallelTraversal` each
implement it. The engine holds a `TraversalStrategy` and calls `iterate()` — it never learns
which one it got. Adding a new walking order = adding one class.

The mechanics are pleasingly similar. Both keep a collection of folders still to visit; the
*only* difference is which end you pull from:

- **DFS** uses a **stack** (LIFO — last in, first out)
- **BFS** uses a **queue** (FIFO — first in, first out)

That's genuinely it. Same loop, different container discipline. Java's `ArrayDeque` does both.

**Two things to get right here:**

**Don't write DFS recursively.** The textbook DFS calls itself, and it's tempting. Two
problems: a very deeply nested folder tree overflows the call stack and crashes; and — more
fundamentally — a recursive method can't hand out results one at a time as it goes (Java has
no `yield`). Use an explicit `ArrayDeque` as a stack. Same algorithm, written as a loop.

**Know the memory cost of each.** DFS holds roughly one folder's worth of entries per level
of depth — small. BFS holds an entire *level* at once, and the widest level of a real
filesystem can be enormous. That's a genuine trade-off, and the reason DFS is the sane
default. Say it out loud rather than hoping nobody asks.

---

### Decision 5: Hand out files one at a time (laziness)

The requirement: *"avoid loading entire directory trees into memory simultaneously."*

The tempting Java one-liner violates it:

```java
// ❌ Builds a list of every file in the tree, THEN filters
List<Path> all = Files.walk(root).collect(toList());
```

On a large tree that's millions of `Path` objects in memory at once, and you paid to collect
every one before rejecting 99% of them.

Instead, traversal returns an **iterator** — an object you ask "got another one?" and it
produces exactly one file, doing only the work needed for that one. Memory holds one file at
a time, not millions.

**The bonus this buys you: early exit.** If the user says "just give me the first 10 matches,"
an iterator can genuinely *stop walking* once it hits 10. A design that collects everything
first has already done all the work before it notices it only needed 10.

**The cost this design has:** when you open a folder to list its contents, the OS gives you a
handle, and you must close it or you leak a limited system resource. Normally you'd wrap that
in `try-with-resources`. But with a lazy iterator, the folder stays open *after* the method
that opened it has returned — because the iterator is still using it. So the iterator itself
has to be closeable, and whoever consumes it must close it when done. That's why the interface
above says `CloseableIterator` rather than plain `Iterator`.

Being able to explain that consequence — "I chose laziness, and here's the resource-management
cost that came with it" — is exactly the kind of trade-off awareness being tested.

---

## Part 5 — Class map

About 18 files. Grouped by the three components from Part 3:

**The data object**
| File | Job |
|---|---|
| `FileEntry.java` | A file + its already-known metadata + memoized header bytes. The star of the show. |
| `Cost.java` | `enum { FREE, CHEAP, EXPENSIVE }` |

**Filters**
| File | Job |
|---|---|
| `SearchCriterion.java` | The interface: `matches()` + `cost()` |
| `AndCriterion.java` | All children must match; sorts children cheap-first |
| `OrCriterion.java` | Any child matches; also sorts cheap-first |
| `NotCriterion.java` | Inverts one child |
| `ExtensionCriterion.java` | `.png`, `.csv` — FREE |
| `NameCriterion.java` | Glob (`log_*.txt`) or regex — FREE |
| `SizeCriterion.java` | An operator + a threshold. One class, not four. — CHEAP |
| `MagicBytesCriterion.java` | Compares file's first bytes to a signature — EXPENSIVE |
| `Criteria.java` | Static shortcuts so queries read nicely: `Criteria.ext(".png").and(...)` |

**Traversal**
| File | Job |
|---|---|
| `TraversalStrategy.java` | The interface |
| `AbstractTraversal.java` | Shared logic: depth tracking, error handling, symlink policy, stat counting |
| `DepthFirstTraversal.java` | Stack-based |
| `BreadthFirstTraversal.java` | Queue-based |
| `ParallelTraversal.java` | Multi-threaded (last layer) |
| `CloseableIterator.java` | `Iterator` + `Closeable` |
| `TraversalOrder.java` | `enum { DFS, BFS, PARALLEL }` |

**Engine and plumbing**
| File | Job |
|---|---|
| `SearchRequest.java` | All the inputs, built with a builder |
| `SearchResult.java` | Matches + stats |
| `SearchStats.java` | Running counters |
| `SearchEngine.java` | Wires it together. **Should be ~15 lines.** |
| `Application.java` | Demo with a generated test tree |

**On `SearchEngine` being tiny:** that's the proof the design worked. All the real logic
lives in small focused classes; the engine just connects them. If your engine swells into
100 lines of branching, that's a signal your abstractions are wrong — treat it as a design
smell detector, not a coding chore.

---

## Part 6 — Build order

Build in this order. Each layer is testable before the next exists.

| # | Build | Why here | Done when |
|---|---|---|---|
| 1 | `FileEntry`, `Cost` | Everything else takes a `FileEntry` as input | Header bytes are read once and cached; a 3-byte file asked for 8 bytes doesn't crash |
| 2 | `SearchCriterion` + And/Or/Not | The composition machinery | You can build a nested tree by hand and test it against a fake `FileEntry` — **no real files needed** |
| 3 | The four concrete filters | Fills in the leaves of the tree | Patterns compiled once in the constructor, not per file |
| 4 | `TraversalStrategy` + DFS + BFS | First time you touch the real filesystem | DFS and BFS visit the same tree in demonstrably different orders |
| 5 | Request / Result / Stats | Inputs and outputs | Builder has sensible defaults |
| 6 | `SearchEngine` + `Application` | It's alive | End-to-end search prints matches and stats |
| 7 | `ParallelTraversal` | Advanced; discussed more than coded | Memory still bounded under load |

**Layers 1–3 need zero filesystem access.** You can build and fully test the entire filter
system against hand-constructed fake `FileEntry` objects. Do those first — they're the heart
of the assignment and the fastest to iterate on. Layer 4 is where the fiddly I/O begins.

---

## Part 7 — Things that break, and what to do

Real filesystems are hostile. Handling these separates working code from toy code, and they're
cheap credit because most candidates skip them.

**Folders you can't read.** Some directories will refuse you (`AccessDeniedException`). Catch
it **per folder**, count it in stats, and keep going. One locked folder must never abort a
scan of a million files.

**Symlink loops.** A symlink is a folder that's really a pointer somewhere else. Someone can
create `/tmp/a` pointing at `/tmp` — now walking it recurses forever. Simplest safe answer:
**don't follow symlinks by default.** If you do follow them, keep a set of already-visited
real locations and skip repeats. Mentioning this unprompted is a strong signal.

**Ambiguity in `maxDepth`.** Is the root folder depth 0 or depth 1? Does `maxDepth = 2` mean
"go two levels down" or "visit two levels total"? There's no universally right answer — but
picking one, writing it in a comment, and being consistent *is* the right answer. Leaving it
vague is a free criticism you can avoid.

**Reporting memory honestly.** The obvious approach — `Runtime.totalMemory() - freeMemory()`
before and after — is unreliable. Java's garbage collector can run at any moment, so your
"after" reading can be *smaller* than "before," giving negative memory usage. The trustworthy
route is `ManagementFactory.getMemoryPoolMXBeans()`: reset the peak before your run, read it
after. Report the number *and* say why the naive version is untrustworthy. Printing a bogus
figure with confidence is worse than printing a caveated one.

---

## Part 8 — Layer 7: parallel traversal

Not needed for a working solution — it's the "senior signal" extension. Reading folders is
I/O-bound (mostly *waiting* on the disk, not computing), so multiple threads can wait
simultaneously and finish sooner.

**The interesting part is that it breaks Decision 5.** A lazy iterator is *pull*-based —
the consumer asks, one item at a time, on one thread. Parallel traversal is *push*-based —
several threads producing whenever they finish.

The bridge is a **bounded blocking queue**: worker threads push found files into a
fixed-size queue; the iterator on the consumer side pops from it. Two nice properties:
- The consumer still looks like a plain `Iterator`, so **nothing else in the design changes.**
- Because the queue is *bounded*, when it fills up the producers block until the consumer
  catches up. That's **backpressure**, and it's what preserves the memory guarantee.

Also worth knowing: **thread count is not `numberOfCPUs` here.** That heuristic is for
CPU-bound work. This is I/O-bound, so the right number depends on the *storage hardware* —
on a fast SSD more threads help; on a spinning hard drive they can make things *worse*
because the read head thrashes seeking between locations. So: a configurable knob with a
conservative default, not a constant. Saying that sentence demonstrates you understand
*why* the usual rule exists rather than just repeating it.

---

## Glossary

| Term | Meaning |
|---|---|
| **syscall** | A request to the operating system (read a file, list a folder). Vastly slower than ordinary code — the thing we're minimizing. |
| **`stat`** | The specific syscall that asks "tell me about this file" — type, size, timestamps. Returns them all together, which is why hoarding the result matters. |
| **`BasicFileAttributes`** | Java's object holding one `stat` result. |
| **memoize** | Compute once, cache, return the cache forever after. What `header()` does. |
| **lazy** | Do work only when the answer is actually demanded, one piece at a time. Opposite of collecting everything up front. |
| **glob** | Shell-style wildcard pattern: `*.png`, `log_2026_*`. Simpler than regex. |
| **magic bytes** | The first few bytes of a file, which identify its real type regardless of filename. Every PNG starts `89 50 4E 47`. Renaming a `.png` to `.txt` doesn't change them. |
| **short-circuit** | Stop evaluating a boolean chain once the outcome is decided. |
| **pure function** | Returns an answer and changes nothing else. Required for filters, so reordering is safe. |
| **LIFO / FIFO** | Last-In-First-Out (a stack, → DFS) / First-In-First-Out (a queue, → BFS). |
| **I/O-bound** | The program spends its time *waiting* on disk or network rather than computing. Changes how threading helps. |
| **backpressure** | A slow consumer forcing fast producers to pause, so queues can't grow without bound. |
| **SOLID** | Five OOD guidelines. The one doing real work here is **O**pen/Closed: extend by adding classes, not editing existing ones. |

---

## Where we are

Everything above is background. The plan is to build **Layer 1 (`FileEntry` + `Cost`)**
first, then check in before moving on.

Specifically, `FileEntry` should:
- Take a `Path` **and** a `BasicFileAttributes` in its constructor. If you catch yourself
  calling `Files.readAttributes()` *inside* `FileEntry`, stop — that recreates the exact
  redundant-syscall problem this class exists to prevent.
- Give free access to name, extension, size, and modified time.
- Have a `header(int n)` that reads from disk **at most once** and caches the result. Read a
  reasonable minimum (say 512 bytes) even when asked for fewer, so later requests for more
  are still cache hits.
- Not blow up on a 3-byte file when asked for 8 bytes — return the 3 bytes you have.

One question to decide deliberately rather than by accident: if the first call caches 512
bytes and a later call wants 1024, do you re-read, or serve what you have? Either is
defensible. Choosing without noticing there was a choice is not.
