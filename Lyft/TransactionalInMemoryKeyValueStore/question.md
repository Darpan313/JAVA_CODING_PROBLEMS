# Requirements
Implement an in-memory key-value store.
Support normal database operations through a prescribed API set; recent prompts mention five APIs and required output formatting.
Support transactions with at least:
begin transaction
rollback
commit
Handle nested or repeated transaction states according to the prompt's rules.
Produce output in the exact requested format, not just return internal values.
Expect 90 minutes total, with a meaningful amount of time spent on reading the prompt and handling input/output.
# Notes
The common implementation pattern is a base dictionary plus a stack of transaction overlays or undo logs.
A strong undo-log version writes directly to the visible store and records the original value for each key only the first time that key is changed in the current transaction. Rollback restores those originals; commit clears the whole transaction stack.
Edge cases matter: rollback with no active transaction, commit with no active transaction, setting a key to the same value, deleting/unsetting keys if present in the prompt, and reading a key shadowed by an active transaction.
Copying the whole store on every BEGIN is easy to reason about but can be expensive; overlay maps make BEGIN cheap but require careful read lookup through the stack.
Candidates repeatedly mention that the round is time-tight. Do not over-engineer persistence or concurrency unless asked.
One intern candidate finished the first approach in about an hour and was asked for a second approach as a follow-up.
# Preparation
Implement a transaction stack with overlay maps and write tests for nested begin -> set -> begin -> set -> rollback -> commit flows.
Implement an undo-log variant where each transaction records first-touch originals, then compare memory cost, read cost, and commit/rollback complexity.
Practice exact command parsing and output formatting from stdin/file-like input.
Drill delete/unset semantics separately: distinguish a missing key from a key explicitly written to None/tombstone.