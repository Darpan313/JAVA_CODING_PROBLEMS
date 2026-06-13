# Large Dataset Patterns — Interview Prep (Larus Technology)

## Learning Goals
Master three production-grade patterns that distinguish engineers who've worked with real data from those who haven't:
1. **Chunked/Batched Processing** — process large data in bounded windows
2. **External Sort** — sort datasets larger than RAM
3. **Streaming vs Buffering** — pipeline data without materializing it

---

## Concept Map (Read Before Coding)

```
Large Dataset Problem
├── Fits in memory?
│   ├── YES → Java Stream / Collections (standard)
│   └── NO  →
│       ├── Can process row-by-row?  → Streaming / forEach pipeline
│       ├── Need random access?      → Chunked processing (10K windows)
│       └── Need sorted order?       → External sort (chunk → merge)
└── Output size?
    ├── Small   → collect to List is fine
    └── Large   → write to file / stream out / paginate
```

---

## Pattern 1 — Chunked / Batched Processing

### Concept
Instead of loading all rows at once (`SELECT * FROM table` → 10M rows in RAM), you process a fixed window at a time. Think of it as sliding a bucket down a river rather than draining the whole river first.

**Key questions to ask in an interview:**
- What's the chunk size? (tune for memory vs throughput, typically 1K–50K)
- Is order important within a chunk? Across chunks?
- Are operations within a chunk stateful (running totals, dedup)?

---

### Task 1.1 — Basic Chunk Iterator

**Goal:** Build a generic `ChunkIterator<T>` that wraps any `List<T>` and exposes chunks of size N.

**Starter interface to implement:**
```java
public interface ChunkIterator<T> {
    boolean hasNext();
    List<T> nextChunk();
}
```

**Approaches:**
| Approach | How | Trade-off |
|---|---|---|
| A — Index-based | Track `offset`, slice with `subList` | Simple, zero-copy (subList is a view) |
| B — Iterator wrapper | Wrap `Iterator<T>`, drain N elements | Works on any `Iterable`, not just `List` |
| C — Java Stream partitioning | `IntStream.range` + `Collectors.groupingBy` | Idiomatic but materializes all groups upfront |

**Implement Approach A first, then B.** Notice that `subList` returns a view — mutating the chunk mutates the original. This is a common bug.

**Test cases to write:**
- Exact multiple: 10 items, chunk size 5 → 2 full chunks
- Non-multiple: 11 items, chunk size 5 → 2 full + 1 partial
- Empty input
- Chunk size larger than input

---

### Task 1.2 — Batched DB Simulation

**Goal:** Simulate processing a 100K-row "database table" 10K rows at a time, computing aggregates per chunk and a running global total.

**Setup:** Generate a `List<Integer>` of 100K random ints (use `new Random(42)` for reproducibility).

**What to implement:**
```
for each chunk of 10_000:
    chunkSum    = sum of chunk
    chunkAvg    = average of chunk
    chunkMax    = max of chunk
    runningTotal += chunkSum
    print chunk stats
print final runningTotal
```

**Approaches:**
| Approach | How |
|---|---|
| A — Manual loop with `subList` | Classic, easy to debug |
| B — Java Stream with `Collectors.collectingAndThen` | Functional style |
| C — Guava `Iterables.partition` | One-liner if Guava is available |

**Key insight:** Notice that `runningTotal` is stateful across chunks. This is the fundamental challenge of chunked processing — some aggregates (sum, count) are chunk-composable; others (median, percentile) are not. Be ready to explain this distinction.

---

### Task 1.3 — Chunked File Writer (Bonus)

**Goal:** Write 1M rows to a CSV file in chunks of 10K, then read it back in the same chunk size and verify row count.

**Why this matters:** In real systems, the "source" is often a DB cursor and the "sink" is a file or message queue. This forces you to decouple producer and consumer.

**Implement:**
```java
void writeInChunks(List<Row> data, int chunkSize, Path output)
void readInChunks(Path file, int chunkSize, Consumer<List<String>> handler)
```

---

## Pattern 2 — External Sort

### Concept
When your dataset is larger than available heap, you can't do `Collections.sort(list)`. External sort solves this with two phases:

```
Phase 1 — Sort chunks:
  Read chunk of N rows → sort in memory → write sorted chunk to temp file
  Repeat until all rows consumed → you now have K sorted temp files

Phase 2 — K-way merge:
  Open all K temp files simultaneously
  Use a min-heap (PriorityQueue) of size K
  Each heap entry = (current value, file index)
  Pop min → write to output → load next from that file
  → Output is globally sorted, RAM usage = O(K)
```

**Key questions to ask:**
- What's chunk size? (drives K = total_rows / chunk_size)
- What's the sort key?
- Can temp files be deleted mid-merge (streaming) or do you need them all?

---

### Task 2.1 — Understand the Merge Step

**Before writing sort code, implement K-way merge in isolation.**

**Input:** K pre-sorted `List<Integer>` arrays (simulate sorted temp files).
**Output:** One merged sorted `List<Integer>`.

**Approaches:**
| Approach | How | Complexity |
|---|---|---|
| A — Naive | Concat all, sort | O(N log N) — defeats the purpose |
| B — PriorityQueue | Min-heap of (value, sourceIndex), always pop min | O(N log K) — correct external merge |
| C — Recursive merge pairs | Merge lists pairwise like merge sort | O(N log K) but higher constant |

**Implement Approach B.** This is the core of external sort and often asked standalone.

```java
List<Integer> kWayMerge(List<List<Integer>> sortedChunks)
```

**Test:** K=4, each chunk has 5 sorted ints. Output must be globally sorted.

---

### Task 2.2 — Full External Sort (File-based)

**Goal:** Sort a 500K-row file of integers that "doesn't fit in memory" (simulate by capping chunk size to 10K).

**Phase 1 implementation:**
```java
List<Path> sortChunks(Path inputFile, int chunkSize) {
    // read chunkSize lines
    // sort them
    // write to temp file
    // return list of temp file paths
}
```

**Phase 2 implementation:**
```java
void mergeChunks(List<Path> sortedChunks, Path outputFile) {
    // open BufferedReader for each chunk
    // PriorityQueue<int[], comparator> where int[] = {value, fileIndex}
    // poll min, write to output, load next from that file
}
```

**Helper class you'll need:**
```java
record ChunkEntry(int value, int chunkIndex) implements Comparable<ChunkEntry> {}
```

**Verification:** Output file first line ≤ last line, and `wc -l output.txt == wc -l input.txt`.

---

### Task 2.3 — Multi-key External Sort (Interview Level)

**Goal:** Sort a CSV with columns `[userId, timestamp, amount]` by `(userId ASC, timestamp DESC)`.

**What changes:** Your `Comparator` becomes compound. The merge heap must use the same comparator.

**Key learning:** External sort is comparator-agnostic. The sort key is pluggable — only the chunk-sort and heap-comparison need to change. This is worth articulating clearly in interviews.

---

## Pattern 3 — Streaming vs Buffering

### Concept
```java
// BUFFERING — loads all 10M rows into heap before doing anything
List<Row> rows = getRows();          // OOM waiting to happen
rows.stream().filter(...).toList();  // toList() materializes everything

// STREAMING — processes one element at a time, O(1) memory
try (Stream<Row> rows = getRowStream()) {
    rows.filter(...)
        .map(...)
        .forEach(outputWriter::write); // never held in memory
}
```

**Critical rule:** `toList()`, `collect()`, `sorted()`, `distinct()` are *terminal* or *stateful* — they break the stream and buffer. `filter`, `map`, `flatMap`, `forEach` are *lazy* — they don't buffer.

---

### Task 3.1 — Identify the Bottleneck

**Read the following code snippets and identify which ones cause full materialization:**

```java
// Snippet A
stream.filter(r -> r.age > 18).collect(Collectors.toList());

// Snippet B
stream.filter(r -> r.age > 18).forEach(System.out::println);

// Snippet C
stream.sorted(Comparator.comparing(Row::name)).forEach(System.out::println);

// Snippet D
stream.limit(100).forEach(System.out::println);

// Snippet E
stream.distinct().forEach(System.out::println);
```

**Write your answers before looking up:** Which snippets are OOM risks on 10M rows? Why?

---

### Task 3.2 — File Streaming Pipeline

**Goal:** Read a 1M-line CSV file, filter rows where `amount > 1000`, transform to a summary string, write to output file — all without loading the file into memory.

```java
void streamingPipeline(Path input, Path output) throws IOException {
    try (
        BufferedReader reader = Files.newBufferedReader(input);
        BufferedWriter writer = Files.newBufferedWriter(output);
        Stream<String> lines = reader.lines()   // lazy — reads one line at a time
    ) {
        lines.skip(1)                            // skip header
             .map(line -> line.split(","))
             .filter(cols -> Double.parseDouble(cols[2]) > 1000)
             .map(cols -> cols[0] + "," + cols[2])
             .forEach(row -> {
                 try { writer.write(row + "\n"); }
                 catch (IOException e) { throw new UncheckedIOException(e); }
             });
    }
}
```

**Implement this, then measure:** Use `Runtime.getRuntime().totalMemory()` before and after. Compare with a version that does `Files.readAllLines(input)` first.

---

### Task 3.3 — Streaming Aggregation Without Collecting

**Goal:** Compute `count`, `sum`, and `max` over a 10M-element stream **without ever calling `.toList()`**.

**Approaches:**
| Approach | How | Notes |
|---|---|---|
| A — `reduce` | Fold with accumulator | Verbose but explicit |
| B — `collect(Collectors.summarizingDouble)` | Built-in stats collector | One pass, no materialization |
| C — `peek` + mutable accumulators | Side-effect accumulation | Works but not thread-safe |

**Implement Approach B**, understand why it doesn't buffer, and explain why `sorted()` before `count()` would be a problem.

---

### Task 3.4 — Backpressure Simulation (Bonus)

**Goal:** Simulate a producer that generates rows faster than the consumer can process them. Use a `BlockingQueue<List<Row>>` as the buffer between producer and consumer threads.

```
Producer thread: generates chunks of 1000 rows, puts on queue (blocks if queue full)
Consumer thread: takes chunk, processes, writes to output (blocks if queue empty)
Queue capacity: 5 chunks (bounded — this is the backpressure mechanism)
```

**Why this matters:** In real pipelines (Kafka, reactive streams), backpressure prevents fast producers from overwhelming slow consumers. Interviewers at data-heavy companies ask this.

---

## Putting It Together — Capstone Task

**Build a mini ETL pipeline** that:
1. Reads a 500K-row CSV (generate it programmatically)
2. Filters rows meeting a condition (streaming — no toList)
3. Sorts the filtered output (external sort — can't sort a stream with unknown size)
4. Writes final sorted output in chunks of 10K

**This combines all three patterns.** Draw the data flow diagram first, then implement.

```
CSV File
  → StreamingReader (Pattern 3)
  → ChunkedSorter (Pattern 2 Phase 1)
  → KWayMerge (Pattern 2 Phase 2)
  → ChunkedWriter (Pattern 1)
  → Output File
```

---

## Interview Talking Points

Have crisp answers ready for these:

1. **"How would you process a 10GB CSV file in a 512MB heap?"**
   → Streaming read + chunked processing or external sort depending on whether you need ordering.

2. **"Why is `.sorted()` dangerous on a large stream?"**
   → It's a stateful intermediate operation — must buffer the entire stream before emitting any element.

3. **"What's the time complexity of external sort?"**
   → O(N log N) sort phase + O(N log K) merge phase where K = number of chunks. Total O(N log N).

4. **"What chunk size would you choose?"**
   → Depends on row size and available heap. Rule of thumb: `chunkSize = (heapMB * 0.5) / rowSizeBytes`. Leave headroom for GC.

5. **"How would you parallelize chunked processing?"**
   → `ExecutorService` with fixed thread pool, submit one chunk per task, use `Future<ChunkResult>` to collect, aggregate at the end. Be careful with stateful aggregates.

---

## Suggested Order

| Day | Tasks |
|---|---|
| Day 1 | Task 1.1, 1.2 — get comfortable with chunking |
| Day 2 | Task 2.1 — nail the k-way merge in isolation |
| Day 3 | Task 2.2 — full external sort end-to-end |
| Day 4 | Task 3.1, 3.2 — streaming pipeline |
| Day 5 | Task 3.3, 3.4 — aggregation + backpressure |
| Day 6 | Capstone — connect everything |
| Day 7 | Dry-run all interview talking points out loud |

---

## Resources to Read (Optional but High Value)
- Java `Stream` javadoc — specifically the section on stateful vs stateless intermediate operations
- `PriorityQueue` javadoc — understand natural ordering vs `Comparator`
- `BufferedReader.lines()` — how it lazily reads
- `Files.newBufferedWriter` with `StandardOpenOption.APPEND`
