# Design a Donation / Charity System — Complete Interview Prep

> Target: 60-minute system design interview (Lyft).
> Goal of this doc: give you everything to know about this system, a battle-tested
> structure to drive the hour, the deep dives that win the interview, and a full
> 60-minute mock conversation you can rehearse from.

---

## 0. How to think about this problem (read this first)

The single most important framing insight:

> **This is a correctness-and-consistency problem, not a raw-throughput problem.**

A donation system moves **money**. The hard parts are NOT "how do I handle a million
QPS." The hard parts are:

1. **Exactly-once charging** — never double-charge a donor, never silently lose a donation.
2. **Integrating with an external, asynchronous, fallible payment provider** (Stripe/Braintree/Adyen).
3. **Idempotency** end-to-end (client retries, network failures, duplicate webhooks).
4. **A reliable state machine** for the donation lifecycle.
5. **Accurate aggregates** that recipients can trust.
6. **Auditability & compliance** (PCI DSS, financial ledger, reconciliation).

If you spend the hour talking about sharding and caching and ignore idempotency,
webhooks, reconciliation, and the ledger, you will fail this interview. Lead with
correctness. That is the signal a payments-adjacent company wants.

The volume is modest (donations are far rarer than ride requests or ad impressions),
so don't over-index on scale. Mention it, size it, move on, and spend your time on
money-correctness.

---

## 1. Time budget for the 60 minutes

| Phase | Time | What you do |
|---|---|---|
| Intro / understand the problem | 0–3 min | Restate, list actors |
| Requirements (functional + non-functional) | 3–10 min | Scope, pin down "money correctness" as the headline NFR |
| Capacity / back-of-envelope | 10–15 min | Size it, conclude "correctness > throughput" |
| API design | 15–22 min | A few key endpoints, focus on the donate call + idempotency |
| Data model | 22–30 min | Entities, the ledger, state column |
| High-level architecture | 30–38 min | Services + datastores + the payment flow end-to-end |
| Deep dives | 38–56 min | Idempotency, webhooks, recurring, aggregates, reconciliation |
| Wrap-up / trade-offs / follow-ups | 56–60 min | Summarize, name what you'd do with more time |

Drive the interview. Announce the structure up front ("I'll clarify requirements,
size it, sketch APIs and data model, draw the architecture, then deep-dive on the
payment flow and idempotency, which is where the real complexity lives"). Interviewers
reward candidates who lead.

---

## 2. Requirements

### 2.1 Functional requirements (in scope)

1. A **donor** can make a **one-time donation** to a **campaign** (or recipient).
2. A donor can set up a **recurring donation** (e.g., monthly).
3. Donations are charged via a **third-party payment provider**.
4. The system tracks **donation state transitions**: `created → authorized → succeeded → failed → refunded → canceled`.
5. **Recipients** can view **individual donation details** and **aggregates** (daily/monthly totals, success counts).
6. **Refunds** (donor-requested or admin-initiated).
7. **Receipts / notifications** to donors (email confirmation, tax receipt).
8. Donor can **view their donation history** and **cancel a recurring donation**.

### 2.2 Out of scope (state these to bound the problem)

- Campaign CRUD / fundraiser onboarding & KYC (assume it exists).
- Payouts/disbursement to recipients' bank accounts (mention the ledger enables it, but don't design the banking rails).
- Tax compliance logic per jurisdiction, fraud-ML modeling (mention hooks, don't build).
- The donor-facing UI.

### 2.3 Non-functional requirements (this is where you score points)

| NFR | Why it matters here | Target |
|---|---|---|
| **Correctness / exactly-once** | Money. No double charge, no lost donation. | **Hard requirement** |
| **Durability** | A recorded donation must never be lost. | No data loss; replicated, backed up |
| **Consistency** | The ledger and donation state must be authoritative & strongly consistent. Aggregates can be eventually consistent. | Strong for ledger/state, eventual for aggregates |
| **Idempotency** | Retries/webhooks must not duplicate effects. | End-to-end |
| **Availability** | Donors should be able to donate, especially during disaster-relief spikes. | High (e.g., 99.95%) for the donate path |
| **Latency** | Accept the donation fast; final confirmation can be async. | p99 < ~500 ms to accept; confirm async |
| **Auditability** | Every cent traceable; reconciliation with provider. | Immutable audit log + ledger |
| **Security / compliance** | PCI DSS; never store raw card data; PII protection. | PCI SAQ-A via tokenization, encryption |
| **Scalability** | Handle viral / GivingTuesday spikes. | Elastic; partition-ready |

**The headline NFR to say out loud:** *"Above everything, this system must be correct
about money — exactly-once charging and a tamper-evident record of every transaction.
I'll optimize for consistency and auditability over latency or throughput."*

---

## 3. Capacity estimation (keep it short — ~4 min)

Make reasonable assumptions and state them.

**Assumptions**
- 50M registered donors, 1M campaigns.
- Normal volume: ~5M donations/day.
- Peak (GivingTuesday / disaster relief): 10–20× normal, bursty.

**Write throughput**
- Avg: 5M / 86,400 s ≈ **~58 donations/sec**.
- Peak: ~20× ≈ **~1,200 writes/sec** (and very bursty within minutes).

> Takeaway: ~1–2K writes/sec peak is **modest**. A well-tuned relational DB (Postgres/MySQL)
> handles this comfortably. We do **not** need exotic scale-out for writes. **This validates
> "correctness over throughput."**

**Read throughput**
- Recipient dashboards + donor history + aggregate views. Reads dominate writes, maybe 10–50×.
- Aggregates can be cached / pre-computed, so the read DB isn't hammered with `GROUP BY`s.

**Storage**
- Donation row ≈ 1 KB. 5M/day → ~5 GB/day → ~1.8 TB/year. Add payments, ledger (2× entries),
  events → call it ~5–10 TB/year. Years of data fit on a partitioned relational store + cold archival.

**Bandwidth** — negligible; payloads are tiny JSON.

**Conclusion to verbalize:** *"Throughput is low for a company this size. The engineering
difficulty is concentrated in correctness, the external payment integration, and idempotency —
so that's where I'll spend the design."*

---

## 4. API design

REST/HTTPS, JSON. Authenticated (OAuth2/JWT). All money in **minor units (cents) as integers**
+ ISO-4217 `currency` — never floats for money.

### 4.1 Create a donation (the most important endpoint)

```
POST /v1/donations
Authorization: Bearer <token>
Idempotency-Key: 7c3f...e9   (client-generated UUID, REQUIRED)

{
  "campaignId": "camp_123",
  "amountMinor": 5000,            // $50.00
  "currency": "USD",
  "paymentMethodToken": "pm_abc", // tokenized at provider; raw card never hits us
  "recurring": { "interval": "monthly" } | null,
  "donorMessage": "Keep it up!",
  "anonymous": false
}

201 Created
{
  "donationId": "don_456",
  "status": "processing",
  "clientSecret": "pi_..._secret_...",  // for 3DS/SCA confirmation on the client, if needed
  "subscriptionId": "sub_789"           // present iff recurring
}
```

Key points to call out:
- **`Idempotency-Key` header is mandatory.** Same key + same payload ⇒ same donation returned,
  never a second charge. This is the crux of payment safety.
- The endpoint **accepts** the donation and returns quickly. Final `succeeded` arrives **async**
  via webhook (the bank/3DS may take seconds). So the synchronous status is usually `processing`.
- `clientSecret` lets the client complete Strong Customer Authentication (3DS) when the provider
  requires it.

### 4.2 Read endpoints

```
GET  /v1/donations/{donationId}                       -> donation detail + current state
GET  /v1/donors/{donorId}/donations?cursor=&limit=    -> donor history (paginated)
GET  /v1/campaigns/{campaignId}/donations?cursor=&limit=&status=  -> recipient view (authz!)
GET  /v1/campaigns/{campaignId}/aggregates?granularity=daily|monthly&from=&to=
       -> { buckets: [{date, totalMinor, successCount, failedCount, refundedMinor}], ... }
```

### 4.3 Mutations

```
POST   /v1/donations/{donationId}/refund       { amountMinor?, reason }   (admin/recipient, authz)
DELETE /v1/subscriptions/{subscriptionId}        -> cancel recurring (idempotent)
PATCH  /v1/subscriptions/{subscriptionId}        -> change amount / payment method
```

### 4.4 Webhook ingress (provider → us)

```
POST /v1/webhooks/payments
Stripe-Signature: t=...,v1=...     // HMAC; we verify before trusting anything
{ "id": "evt_...", "type": "payment_intent.succeeded", "data": {...} }
-> 200 quickly (ack), process asynchronously & idempotently
```

> Pagination: use **cursor/keyset pagination** (e.g., `created_at, id`), not OFFSET — stable
> under inserts and efficient for deep pages on a recipient with millions of donations.

---

## 5. Data model

Use a **relational database** (Postgres) as the system of record. Money + relationships +
transactions + strong consistency ⇒ relational is the right default. NoSQL can serve the
read/aggregate side.

### 5.1 Core entities

```
donors(
  id PK, name, email, created_at
)

recipients(
  id PK, name, payout_account_id, kyc_status, created_at
)

campaigns(
  id PK, recipient_id FK, title, description,
  goal_amount_minor, currency, status,         -- active|paused|closed
  starts_at, ends_at, created_at
)

donations(                                      -- the donation "order"/intent
  id PK,
  donor_id FK,
  campaign_id FK,
  amount_minor BIGINT, currency,
  status TEXT,                                  -- state machine (see §6)
  is_recurring BOOL,
  subscription_id FK NULL,
  payment_id FK NULL,
  idempotency_key TEXT UNIQUE,                  -- <<< prevents duplicate donations
  donor_message, anonymous BOOL,
  created_at, updated_at,
  INDEX (campaign_id, created_at),
  INDEX (donor_id, created_at)
)

payments(                                       -- one charge attempt at the provider
  id PK,
  donation_id FK,
  provider TEXT,                                -- 'stripe'
  provider_payment_intent_id TEXT UNIQUE,
  status TEXT,                                  -- mirrors provider
  amount_minor BIGINT, currency,
  failure_code, failure_message,
  created_at, updated_at
)

subscriptions(                                  -- recurring donation schedule
  id PK,
  donor_id FK, campaign_id FK,
  amount_minor, currency, interval,             -- 'monthly'
  status TEXT,                                  -- active|past_due|canceled|paused
  payment_method_token TEXT,
  provider_subscription_id TEXT NULL,           -- if using provider-managed subs
  next_charge_at TIMESTAMP,                      -- if self-managed
  created_at, updated_at,
  INDEX (status, next_charge_at)                -- scheduler scans due rows
)

webhook_events(                                 -- inbound idempotency / audit
  id PK,
  provider_event_id TEXT UNIQUE,                -- dedupe duplicate deliveries
  type TEXT,
  payload JSONB,
  status TEXT,                                  -- received|processed|failed
  received_at, processed_at
)

outbox(                                         -- transactional outbox (see §9)
  id PK, aggregate_id, type, payload JSONB,
  created_at, published_at NULL
)

ledger_entries(                                 -- double-entry, append-only, immutable
  id PK,
  donation_id FK,
  txn_id,                                       -- groups the balanced pair
  account TEXT,                                 -- e.g. 'donor_clearing', 'campaign_payable'
  direction TEXT,                               -- 'debit' | 'credit'
  amount_minor BIGINT, currency,
  created_at
)

campaign_aggregates(                            -- precomputed rollups (read model)
  campaign_id, bucket_date, granularity,        -- PK (campaign_id, granularity, bucket_date)
  total_minor BIGINT, success_count INT,
  failed_count INT, refunded_minor BIGINT,
  updated_at
)
```

### 5.2 Why separate `donations` and `payments`?

- A **donation** is the *intent / business object*. A **payment** is a *charge attempt at the
  provider*. One donation can have multiple payment attempts (first card declined, retry with
  another). Recurring donations create *many* donations over time but share a subscription.
- This separation keeps the business record clean and lets you model retries and provider state
  without polluting the donation row.

### 5.3 The ledger (say this — it's a senior signal)

> *"For money correctness I'd keep an append-only, double-entry ledger. Every successful donation
> writes a balanced pair of entries (debit donor-clearing, credit campaign-payable). Refunds write
> reversing entries. The ledger — not a mutable balance column — is the source of truth for
> payouts and for reconciliation against the provider. It's immutable and auditable."*

### 5.4 Money representation

- Store as **integer minor units** (`BIGINT` cents) + `currency`. Never floats. Avoids rounding bugs.

---

## 6. The donation state machine (deep, expected)

States and **allowed** transitions. Make this explicit — interviewers love a clean FSM and will
probe illegal transitions.

```
                     submit to provider
   CREATED ───────────────────────────────▶ PROCESSING
      │                                          │
      │ (validation fail)                        │  provider result
      ▼                                          ├──────────────▶ AUTHORIZED ──capture──▶ SUCCEEDED
   FAILED                                        │                    │                      │
                                                 ├──────────────▶ SUCCEEDED                  │ refund
                                                 │                                            ▼
                                                 └──────────────▶ FAILED                  REFUNDED
                                                                                       (or PARTIALLY_REFUNDED)
   AUTHORIZED ──void/expire──▶ CANCELED
```

| State | Meaning | Terminal? |
|---|---|---|
| `CREATED` | Donation recorded; not yet sent to provider | no |
| `PROCESSING` | Submitted to provider; awaiting result (incl. 3DS) | no |
| `AUTHORIZED` | Funds held (auth) but not captured | no |
| `SUCCEEDED` | Captured; money moved | no (refundable) |
| `FAILED` | Declined / errored | yes |
| `CANCELED` | Auth voided / not captured / user canceled before capture | yes |
| `REFUNDED` / `PARTIALLY_REFUNDED` | Money returned | terminal-ish |

**Authorize vs. capture:** For most donations you do an immediate **auth+capture** (charge now).
The separate `AUTHORIZED` state matters when you want to (a) verify the card, (b) delay capture,
or (c) hold during fraud review. Mention you'd typically capture immediately for donations.

**State transition rules to enforce in code:**
- Transitions are **guarded** — only legal edges allowed (a DB-level or service-level check).
- State only moves **forward** (monotonic). A late/duplicate webhook can't move `SUCCEEDED` back to `PROCESSING`.
- Every transition is recorded (status + `updated_at`, ideally an audit row) for the audit trail.
- Idempotent: applying the same terminal event twice is a no-op.

---

## 7. High-level architecture

```
                   ┌─────────────┐
   Donor App ─────▶│ API Gateway │──── authN/Z, rate limit, TLS
   Recipient App   │  + LB       │
                   └──────┬──────┘
                          │
        ┌─────────────────┼───────────────────────────────────────┐
        ▼                 ▼                  ▼                      ▼
 ┌─────────────┐  ┌───────────────┐  ┌──────────────┐    ┌──────────────────┐
 │  Donation   │  │  Subscription │  │  Aggregation │    │   Notification   │
 │  Service    │  │  /Recurring   │  │   /Read API  │    │     Service      │
 │ (FSM, idem) │  │   Service     │  │              │    │ (email receipts) │
 └──────┬──────┘  └───────┬───────┘  └──────┬───────┘    └────────▲─────────┘
        │                 │                 │                     │
        │  ┌──────────────▼─────────────┐   │                     │
        │  │  Scheduler (due charges)   │   │                     │
        │  └──────────────┬─────────────┘   │                     │
        ▼                 ▼                 │                     │
 ┌──────────────────────────────┐          │                     │
 │   Payment Service / Adapter   │──────────┼──▶ Third-Party Payment Provider
 │  (provider calls + webhooks)  │◀─ webhooks│       (Stripe/Braintree/Adyen)
 └──────────────┬───────────────┘          │
                │ writes (txn + outbox)     │
                ▼                           │
        ┌───────────────┐   outbox relay    │     ┌─────────────────────┐
        │  Postgres     │──────────────────▶│────▶│  Kafka / event bus  │
        │ (system of    │                   │     └──────────┬──────────┘
        │  record:      │                   │                │ consumers
        │  donations,   │                   ▼                ▼
        │  payments,    │           ┌──────────────┐  ┌──────────────────┐
        │  ledger,      │           │   Redis      │  │ Aggregation worker│
        │  subs, outbox)│           │ (idempotency │  │ (updates rollups) │
        └───────────────┘           │  cache,      │  └────────┬─────────┘
                ▲                    │  counters)   │           ▼
                │ reconciliation     └──────────────┘   ┌──────────────────┐
        ┌───────┴────────┐                              │ campaign_aggregates│
        │ Reconciliation │                              │  + OLAP/warehouse  │
        │  job (polls    │                              └──────────────────┘
        │  provider)     │
        └────────────────┘
```

### Components

- **API Gateway / LB** — TLS termination, authN/Z, rate limiting (protect the donate path), routing.
- **Donation Service** — owns the donation FSM, idempotency, creates the payment via Payment Service,
  writes donation + outbox event in one DB transaction.
- **Payment Service / Adapter** — the *only* thing that talks to the provider; abstracts provider
  specifics (so you can multi-source / swap providers); also the **webhook receiver**.
- **Subscription/Recurring Service + Scheduler** — manages recurring donations and triggers due charges.
- **Aggregation Service / workers** — consume `succeeded/refunded/failed` events, update precomputed
  rollups; serve the recipient dashboard reads.
- **Notification Service** — receipts, failed-payment dunning emails.
- **Reconciliation job** — periodically reconciles our records against the provider (catch missed webhooks).
- **Datastores**:
  - **Postgres** — system of record (donations, payments, subscriptions, ledger, outbox, webhook_events).
  - **Kafka/SQS** — async event backbone (outbox → bus → consumers).
  - **Redis** — idempotency-key cache, real-time aggregate counters, rate limiting.
  - **OLAP/Warehouse** (BigQuery/Snowflake/ClickHouse) — heavy historical analytics for recipients.

---

## 8. End-to-end donation flow (walk this slowly in the interview)

**One-time donation, happy path:**

1. **Client tokenizes the card** with the provider's SDK (Stripe Elements/Stripe.js). The raw PAN
   goes *directly* to the provider; our servers receive only a **payment method token** (`pm_...`).
   → keeps us out of PCI scope (SAQ-A).
2. Client `POST /v1/donations` with an **`Idempotency-Key`** + the token.
3. **Donation Service**:
   a. Look up the idempotency key. If seen → return the existing donation (no new charge).
   b. In **one DB transaction**: insert `donations` row (`CREATED`) with the unique idempotency key,
      and insert an **outbox** record. Commit.
4. Donation Service asks **Payment Service** to create a **PaymentIntent** at the provider, passing
   an idempotency key to the provider too (end-to-end idempotency). Donation → `PROCESSING`.
5. If the provider needs **3DS/SCA**, return `clientSecret`; the client completes authentication.
6. The provider processes asynchronously and **sends a webhook** (`payment_intent.succeeded` /
   `.payment_failed`).
7. **Webhook handler**: verify signature → dedupe via `provider_event_id` → enqueue → ack `200` fast.
8. A consumer applies the event **idempotently**: transition donation `PROCESSING → SUCCEEDED`,
   write the **ledger** entries, emit a `donation.succeeded` event (via outbox).
9. Downstream consumers: **Aggregation** updates rollups/counters; **Notification** emails the receipt.
10. If no webhook arrives within an SLA, the **reconciliation job** polls the provider and resolves
    the donation (succeeded/failed). This is the safety net.

> The pattern to name: **accept synchronously, confirm asynchronously, reconcile as a backstop.**

---

## 9. Deep dive A — Idempotency & exactly-once (the #1 topic)

There are **three** dangerous duplication points; address each:

### 9.1 Client → our API (the donate request)
- Client generates a **UUID idempotency key** per donation attempt and sends it in the header.
- We store it with a **UNIQUE constraint** on `donations.idempotency_key`.
- Logic:
  - Begin txn → try insert with the key.
  - On unique-violation → the donation already exists → fetch and **return the same response**.
  - Cache `key → donationId/response` in **Redis** for fast short-circuit, but the DB unique
    constraint is the authority (Redis is an optimization, not the guarantee).
- Result: retries (timeouts, double-clicks, mobile reconnects) never create a second donation.

### 9.2 Our API → payment provider (the charge)
- Pass an **idempotency key to the provider** on the create-charge call (Stripe supports this).
- If our request to Stripe times out and we retry, Stripe returns the *same* charge instead of a
  second one. This closes the "did the charge go through?" gap.

### 9.3 Provider → us (webhooks, delivered ≥ once, possibly out of order)
- **Dedupe** on `provider_event_id` (UNIQUE). Seen before → ack `200`, do nothing.
- **Idempotent apply** via the **monotonic state machine**: a duplicate `succeeded` after we're
  already `SUCCEEDED` is a no-op; an out-of-order stale event can't regress the state.
- Aggregate increments must be idempotent too (tie the increment to the event id; see §12).

### 9.4 Internal events (the dual-write problem)
- Writing to the DB **and** publishing to Kafka is two systems → they can diverge (crash between).
- Solution: **Transactional Outbox.** Write the domain change **and** an `outbox` row in the **same
  DB transaction**. A relay/CDC (e.g., Debezium) reads the outbox and publishes to Kafka **at-least-once**.
  Consumers are idempotent. This guarantees we never "succeed a donation but lose the event."

> Summary line: *"Idempotency keys protect the client→API and API→provider hops; a unique
> event-id + a monotonic state machine protect the webhook hop; and a transactional outbox
> protects the DB→event-bus hop. Together that's end-to-end exactly-once **effects** on top of
> at-least-once **delivery**."*

---

## 10. Deep dive B — Webhooks & reconciliation

**Why webhooks?** The charge result is asynchronous (bank latency, 3DS). We can't block the donor's
request waiting. The provider calls us back.

**Robust webhook handling:**
1. **Verify the signature** (HMAC with the provider's signing secret) before trusting the body.
2. **Persist raw event** to `webhook_events` (audit + replay) and **dedupe** on `provider_event_id`.
3. **Ack fast** (`200`) and process **asynchronously** via a queue — don't do heavy work inline,
   or the provider may time out and retry, amplifying load.
4. **Process idempotently** and respect the **monotonic FSM** (handle out-of-order delivery).
5. **DLQ** for poison events; alert on repeated failures.

**Reconciliation (the backstop everyone forgets):**
- Webhooks **can be missed** (our outage, provider hiccup, misconfig).
- A scheduled **reconciliation job** finds donations stuck in `PROCESSING` past an SLA (say > 1 hour)
  and **polls the provider** (`GET payment_intent`) for the truth, then resolves them.
- Periodically, a **full reconciliation** compares our ledger/settlements against the provider's
  payout/settlement reports to catch discrepancies (amounts, fees, refunds).
- This is what makes the system **trustworthy** — it self-heals from missed signals.

---

## 11. Deep dive C — Recurring donations

Two approaches — present both and pick, with trade-offs.

### Option A — Provider-managed subscriptions (e.g., Stripe Billing)
- We create a subscription object at the provider; **the provider charges on schedule** and sends
  `invoice.payment_succeeded` / `invoice.payment_failed` webhooks. We just record the resulting donations.
- **Pros:** offloads scheduling, retries (dunning), card-expiry handling, SCA/off-session mandates,
  proration. Much less to build/operate.
- **Cons:** vendor lock-in; less control over custom schedules; harder to support multiple providers.

### Option B — Self-managed scheduler
- Store `subscriptions(status, next_charge_at, payment_method_token)`.
- A **scheduler** (cron + a worker, or a distributed scheduler) periodically queries
  `WHERE status='active' AND next_charge_at <= now()` (indexed), and for each due subscription:
  - Create a new **donation** (with an idempotency key derived from `subscription_id + period`, so a
    re-run of the scheduler can't double-charge the same period).
  - Charge the saved payment method **off-session** via Payment Service.
  - On success: advance `next_charge_at` by the interval.
- **Pros:** full control (custom intervals, multi-provider, custom dunning), no lock-in.
- **Cons:** you must build scheduling reliability, retries/dunning, card-expiry handling, SCA mandates.

### Failure handling for recurring (dunning)
- Failed charge → **retry schedule** (e.g., day 1, 3, 5, 7) → on continued failure mark
  `subscription.status = past_due` then `canceled`; notify the donor to update their card.
- **Idempotency for the period** is the key safety property: the `(subscription_id, billing_period)`
  pair guarantees one charge per period even if the scheduler runs twice or restarts mid-batch.

**Scheduler reliability:** make the due-scan **idempotent + locked** (e.g., `SELECT ... FOR UPDATE
SKIP LOCKED`, or claim rows with a lease) so multiple scheduler instances don't process the same
subscription. Recommend **Option A** for v1 (less to get wrong), keep the abstraction so you can
move to **B** for control/multi-provider later.

---

## 12. Deep dive D — Aggregates (recipient dashboards)

Requirement: recipients view **daily/monthly totals, success counts** (and refunds, failed counts).
Read-heavy, want near-real-time-ish, must be **trustworthy**.

**Spectrum of options:**

| Approach | How | Pros | Cons |
|---|---|---|---|
| 1. On-the-fly SQL | `SELECT sum, count ... GROUP BY date` on read | Always exact, zero extra infra | Slow/expensive for big campaigns; hammers the write DB |
| 2. Incremental counters | On `succeeded` event, atomically `UPDATE campaign_aggregates` (or Redis `INCR`) | Fast reads, near-real-time | Must be idempotent; hot partition on viral campaigns |
| 3. Batch rollups | Periodic job aggregates donations → rollup table / warehouse | Cheap, great for history | Eventually consistent (lag) |
| 4. Hybrid (Lambda) | Real-time counters for "today" + nightly batch for historical/correction | Fast *and* eventually exact | More moving parts |

**Recommended: Hybrid.**
- **Real-time:** the aggregation consumer reads `donation.succeeded/refunded` events and increments
  `campaign_aggregates(campaign_id, granularity, bucket_date)`. Increments are **idempotent** by
  recording processed event-ids (or upserting from the source of truth) so a redelivered event
  doesn't double-count.
- **Batch:** a nightly job recomputes rollups from the donations/ledger **source of truth** and
  corrects any drift. Heavy historical/monthly queries are served from the **warehouse**.
- The **donations table / ledger is the source of truth**; aggregates are a **derived cache that can
  always be rebuilt**. State this — it means a bug in the counter can't corrupt the real data.

**Hot-partition mitigation (viral campaign):**
- A single campaign during a disaster could get a flood of donations → one aggregate row becomes a
  write hotspot. Mitigate with **sharded counters**: maintain N sub-counters
  `(campaign_id, bucket_date, shard)` and **sum on read**; or batch increments in the consumer
  (aggregate per micro-batch, then one update). Redis `INCRBY` on a sharded key also works for the
  live "today" number.

---

## 13. Deep dive E — Refunds

- `POST /v1/donations/{id}/refund` (authz: admin/recipient/automated policy). Full or partial.
- Idempotent (idempotency key on the refund too).
- Calls the provider's refund API; the donation moves `SUCCEEDED → REFUNDED` (or `PARTIALLY_REFUNDED`)
  **on the refund webhook**, not optimistically.
- **Ledger:** write **reversing** entries (don't mutate or delete the original) — preserves the audit trail.
- Aggregates: decrement `total_minor`, bump `refunded_minor` (idempotently).

---

## 14. Security, privacy & compliance (say a few crisp lines)

- **PCI DSS:** *never* touch raw card data. Use the provider's hosted fields/SDK → tokenization →
  we store only tokens. This keeps us in **SAQ-A** (smallest PCI scope). This is a huge de-risking
  decision — say it explicitly.
- **Encryption:** TLS 1.2+ in transit; encrypt PII at rest (KMS-managed keys).
- **AuthN/Z:** OAuth2/JWT; **recipients can only read their own campaigns' data** (object-level authz).
- **Webhook security:** verify HMAC signatures; reject unsigned/old timestamps (replay protection).
- **Idempotency + ledger** double as fraud/abuse and audit controls.
- **Fraud:** velocity checks, provider risk engine (e.g., Stripe Radar) hook; flag/hold suspicious donations.
- **Audit log:** immutable record of state transitions and money movement.
- **Compliance/data:** GDPR/CCPA (data export/erasure with legal-hold exceptions for financial records),
  configurable retention.

---

## 15. Reliability, scaling & failure modes

- **DB:** Postgres primary + sync replica (no data loss on failover) + async read replicas for reads.
  **Partition** large tables (e.g., `donations` by `created_at` range or `campaign_id` hash) when needed —
  but note current write volume doesn't demand sharding yet.
- **Stateless services** behind a load balancer; autoscale the donate path for spikes.
- **Queue (Kafka/SQS)** decouples spikes — webhook ingestion and aggregation absorb bursts; consumers
  scale horizontally; **DLQ** for poison messages.
- **Idempotent consumers everywhere** (at-least-once delivery is the norm).
- **Rate limiting** on the API gateway; **graceful degradation**: if aggregation lags, donations still
  succeed (aggregates are eventually consistent, the money path isn't blocked).
- **Provider outage:** the provider is a hard dependency for charging. If it's down, we can still
  **accept and queue** donations as `CREATED`/`PROCESSING` and submit when it recovers (with backoff),
  or fail fast and ask the donor to retry. A **circuit breaker** around the provider prevents cascading
  failures. Consider a **secondary provider** for failover (the adapter abstraction enables this).
- **Backups + point-in-time recovery**; regular **restore drills**.

### Key failure scenarios (interviewers love these "what if")
| Scenario | Handling |
|---|---|
| Client retries after timeout | Idempotency key → same donation, no double charge |
| We charged but crashed before recording | Provider idempotency key + reconciliation reconciles it |
| Webhook never arrives | Reconciliation job polls provider and resolves |
| Duplicate / out-of-order webhook | Event-id dedupe + monotonic FSM |
| DB write ok, event publish fails | Transactional outbox guarantees eventual publish |
| Viral campaign hot aggregate | Sharded counters / batched increments |
| Recurring scheduler runs twice | `(subscription_id, period)` idempotency → one charge |
| Provider down | Circuit breaker, queue & retry, optional secondary provider |

---

## 16. Trade-offs summary (have these ready)

- **Relational (Postgres) over NoSQL for the system of record** — money needs transactions, strong
  consistency, a ledger, and relational integrity. Volume is low enough that we don't need to give
  these up. NoSQL/warehouse serves the analytics/aggregate read side.
- **Async confirmation over synchronous charge** — payments are inherently async (3DS, banks). Accept
  fast, confirm via webhook, reconcile as backstop. Trade a little UX immediacy for correctness & resilience.
- **Provider-managed recurring (v1) over self-managed** — less to build/operate and fewer ways to
  double-charge; accept vendor lock-in, keep the adapter so we can migrate later.
- **Eventually-consistent aggregates over strongly-consistent ones** — recipients tolerate seconds of
  lag; keeping the money path off the aggregate write path improves availability and avoids hotspots.
- **Tokenization / SAQ-A over storing cards** — massively reduces PCI scope and breach blast radius;
  cost is a hard dependency on the provider's SDK.
- **Exactly-once *effects* on at-least-once *delivery*** — we don't chase true exactly-once delivery
  (impossible in distributed systems); we make every effect idempotent instead.

---

## 17. If they give you more time / follow-ups to anticipate

- **Multi-currency & FX** — store original currency; settle/convert at payout; record FX rate in the ledger.
- **Multiple payment providers / routing** — adapter pattern; route by region/cost/availability; failover.
- **Payouts/disbursement** to recipients — driven off the ledger; batch transfers; hold periods; fees.
- **Matching donations / corporate match / fees** — extra ledger accounts and rules.
- **Tax receipts** — generate annual statements from the immutable record.
- **Fraud/AML** — velocity rules, risk scoring, manual review queue, holds.
- **Analytics** — CDC → warehouse → recipient BI dashboards.
- **Tipping the platform / cover-the-fees** — donor opts to add platform fee; another ledger split.

---

## 18. The 30-second pitch (memorize this opener/closer)

> *"A donation system is a money-movement system, so I'm optimizing for correctness and
> auditability over throughput — the volume here is modest. The core is a donation state machine
> backed by a relational system of record and an append-only double-entry ledger. Charging goes
> through a third-party provider, so the three hard problems are end-to-end idempotency (so we
> never double-charge), asynchronous confirmation via verified webhooks with a reconciliation
> backstop (so we never lose or strand a donation), and a transactional outbox so our DB and event
> bus never diverge. Recurring donations sit on a subscription model, and recipient aggregates are
> a rebuildable, eventually-consistent read model derived from the ledger."*

---
---

# 19. FULL 60-MINUTE MOCK INTERVIEW

> Two interviewers: **I1** (drives requirements/architecture) and **I2** (jumps in with deep,
> adversarial follow-ups). **C** = you, the candidate. Times are approximate. Read this aloud a
> couple of times; internalize the *moves*, not the exact words.

---

### [0:00 – 0:03] Framing

**I1:** Today I'd like you to design a donation system. Donors give to campaigns, you support
one-time and recurring donations, payment goes through a third-party provider, you track the
donation lifecycle, and recipients can see donation details and aggregates. Where would you start?

**C:** Great. Let me restate to make sure I've got it, then I'll drive the session. The actors are
**donors**, **recipients/campaigns**, and an external **payment provider**. The flows are: make a
one-time donation, set up a recurring donation, charge via the provider, track state transitions,
and let recipients view individual donations plus aggregates like daily and monthly totals.

Before I design anything, one framing point I want to put on the table: **this is a money-movement
system.** So unlike, say, a feed or a ride-matcher, the dominant challenge isn't raw throughput —
it's **correctness**: never double-charging a donor, never losing or stranding a donation, and
keeping an auditable record. I'll let that drive my priorities.

My plan for the hour: clarify requirements, do a quick capacity estimate, sketch the key APIs and
data model, draw the architecture and walk the end-to-end payment flow, then deep-dive on
idempotency, webhooks, recurring donations, and aggregates. Sound good?

**I1:** Perfect, go ahead.

---

### [0:03 – 0:10] Requirements

**C:** Let me confirm the functional scope. In scope: (1) one-time donations to a campaign,
(2) recurring monthly donations, (3) charging via a third-party provider, (4) the donation state
machine, (5) recipient views of detail + aggregates. I'll also assume we want (6) refunds and
(7) email receipts, since they're core to a real system. Out of scope unless you want them:
campaign creation/KYC onboarding, the actual bank payouts to recipients, and tax-jurisdiction logic —
I'll mention hooks but not design them. Is that the right scope?

**I1:** Yes. Don't worry about payouts. Keep refunds in.

**C:** Good. Now the non-functional requirements, which for this system are the real story:

- **Correctness / exactly-once charging** — the headline. No double charges, no lost donations.
- **Durability and strong consistency** for the donation state and the money ledger.
- **Idempotency** end-to-end, because clients retry and providers redeliver webhooks.
- **Availability** on the donate path — especially during disaster-relief or GivingTuesday spikes.
- **Auditability** — every cent traceable, reconcilable against the provider.
- **Security/compliance** — PCI DSS; we will *never* store raw card data.
- Aggregates can be **eventually consistent** — recipients tolerate a few seconds of lag.

**I2:** You keep saying "exactly-once." Be precise — exactly-once delivery is famously impossible
in distributed systems.

**C:** Right, and that's an important distinction. I don't mean exactly-once *delivery* — I mean
exactly-once *effects*. Delivery will be at-least-once; messages and requests get retried. What I
guarantee is that the *effect* — a charge, a state transition, an aggregate increment — happens
once, by making each of those operations **idempotent**. I'll show concretely how at three layers:
client→API, API→provider, and provider→us. That's the safety property that matters for money.

**I2:** Good. Continue.

---

### [0:10 – 0:15] Capacity

**C:** Quick sizing so we right-size the design. Assume 50M donors, 1M campaigns, ~5M donations/day
normal. That's about **58 writes/sec average**. Peak during a viral event, say 20×, is roughly
**1,200 writes/sec**, bursty.

The conclusion I want to draw: **that's a modest write rate.** A well-tuned Postgres handles a couple
thousand writes/sec comfortably. So we do **not** need exotic write sharding for v1. Reads — recipient
dashboards, donor history, aggregates — dominate, maybe 10–50× writes, but those we serve from
replicas and precomputed rollups.

Storage: ~1 KB per donation, plus payments and double ledger entries — call it a few KB per donation —
so single-digit TB/year. Easily handled with partitioning and archival.

So I'll explicitly **trade scale-out complexity away** and spend the design budget on correctness and
the payment integration. Anybody object to that prioritization?

**I1:** No, that's the right read. Move on.

---

### [0:15 – 0:22] APIs

**C:** Let me start with the most important endpoint — creating a donation:

```
POST /v1/donations
Idempotency-Key: <client-generated UUID>     // REQUIRED
{ campaignId, amountMinor, currency, paymentMethodToken,
  recurring: {interval:"monthly"} | null, donorMessage, anonymous }
-> 201 { donationId, status:"processing", clientSecret?, subscriptionId? }
```

Three things I want to highlight. First, **money is integer minor units plus a currency** — never a
float. Second, the **`Idempotency-Key` header is mandatory** — that's how we make the donate call safe
under retries. Third, the response status is usually **`processing`**, not `succeeded` — because the
real result comes back asynchronously from the provider. The optional `clientSecret` lets the client
complete 3DS/Strong Customer Authentication when required.

Reads use **cursor/keyset pagination**:
```
GET /v1/donations/{id}
GET /v1/donors/{id}/donations?cursor=&limit=
GET /v1/campaigns/{id}/donations?cursor=&limit=&status=
GET /v1/campaigns/{id}/aggregates?granularity=daily|monthly&from=&to=
```
Mutations: `POST /v1/donations/{id}/refund`, `DELETE /v1/subscriptions/{id}`. And one ingress
endpoint for the provider: `POST /v1/webhooks/payments`.

**I2:** Why cursor pagination and not just `page` and `offset`?

**C:** Because a popular campaign can have millions of donations and new ones arrive constantly.
OFFSET pagination gets slower the deeper you page — it scans and discards rows — and it skips or
duplicates rows when inserts happen between page loads. Keyset pagination on `(created_at, id)` is
**O(log n)** to seek and **stable** under concurrent inserts. For an append-heavy table read by
recipients, that's the right call.

**I2:** Why is the idempotency key generated by the *client* rather than the server?

**C:** Because the failure we're defending against is the client **not knowing whether its request
succeeded** — a timeout or a dropped connection. If the server generated the key, the client's retry
would get a *new* key and we'd create a second donation and a second charge. By having the client mint
one stable key per donation *attempt* and reuse it on retry, the server can recognize "this is the same
attempt" and return the original result. The client owns the identity of the attempt because the client
is the one that retries.

---

### [0:22 – 0:30] Data model

**C:** Relational system of record — Postgres. Money plus transactions plus a ledger plus relational
integrity, at this modest volume, means I keep ACID rather than give it up. Core tables:

- `donors`, `recipients`, `campaigns` — straightforward.
- `donations` — the business object: `donor_id, campaign_id, amount_minor, currency, status,
  is_recurring, subscription_id?, payment_id?, idempotency_key UNIQUE, ...`. That unique constraint
  is load-bearing for idempotency.
- `payments` — one **charge attempt** at the provider: `donation_id, provider_payment_intent_id UNIQUE,
  status, failure_code, ...`.
- `subscriptions` — recurring schedule: `amount, interval, status, next_charge_at, payment_method_token,
  provider_subscription_id?`.
- `webhook_events` — `provider_event_id UNIQUE`, payload, status — for dedupe and audit.
- `outbox` — for the transactional outbox pattern, I'll explain shortly.
- `ledger_entries` — append-only **double-entry** ledger.
- `campaign_aggregates` — precomputed rollups for the dashboard.

**I2:** Why split `donations` and `payments`? Feels like overmodeling.

**C:** They're different lifecycles. A **donation** is the donor's intent — a stable business object.
A **payment** is a single charge attempt at the provider, which can fail and be retried with a
different card, and which carries provider-specific state. One donation may have several payment
attempts; and a recurring donation produces *many* donations over months but shares one subscription.
Keeping payment-attempt churn out of the donation row keeps the business record clean and the audit
trail clear. It's not overmodeling — it's separating "what the donor wants" from "how the money was moved."

**I2:** And the double-entry ledger — is that not overkill for charity donations?

**C:** I'd argue it's exactly what protects us. A mutable "balance" column is easy to corrupt and hard
to audit. With an append-only double-entry ledger, every successful donation writes a **balanced pair** —
debit a donor-clearing account, credit the campaign-payable account — and a refund writes **reversing**
entries rather than mutating history. That gives me three things for free: an immutable audit trail,
a basis for reconciliation against the provider's settlement reports, and the source of truth for
eventual payouts. For anything touching money, the ledger is the senior-engineer default, and it's cheap.

---

### [0:30 – 0:36] State machine + architecture sketch

**C:** Let me define the donation state machine, because the whole system is organized around it:

```
CREATED → PROCESSING → {AUTHORIZED → SUCCEEDED | SUCCEEDED | FAILED}
SUCCEEDED → REFUNDED / PARTIALLY_REFUNDED
AUTHORIZED → CANCELED
CREATED → FAILED (validation)
```

Two rules I enforce: transitions are **guarded** (only legal edges) and the state is **monotonic** —
it only moves forward. That second rule is what makes us robust to **out-of-order and duplicate**
webhooks: a stale event can't drag `SUCCEEDED` back to `PROCESSING`.

For donations I'd normally do an **immediate auth+capture** — a single charge. The separate
`AUTHORIZED` state is there for the case where we want to verify a card or hold during fraud review,
but the common path is straight to `SUCCEEDED`.

Now the architecture: [draws] an API gateway for auth/rate-limiting; a **Donation Service** that owns
the FSM and idempotency; a **Payment Service** that's the only component talking to the provider and
also receives webhooks; a **Subscription Service + scheduler** for recurring; an **Aggregation
Service**; and a **Notification Service**. The system of record is **Postgres**. Async work flows over
**Kafka**. **Redis** holds the idempotency cache and live counters. A **warehouse** serves heavy
historical analytics. And critically, a **reconciliation job** polls the provider as a backstop.

---

### [0:36 – 0:40] End-to-end flow

**C:** Let me walk one donation end to end, because the subtlety is all in the choreography:

1. The client tokenizes the card **directly with the provider's SDK** — the raw card number never
   touches our servers. We only ever see a token. That keeps us in **PCI SAQ-A**, the smallest scope.
2. Client calls `POST /v1/donations` with the idempotency key and the token.
3. Donation Service checks the key; if new, it inserts the `donations` row as `CREATED` **and** an
   `outbox` row **in one DB transaction**, then commits.
4. It asks the Payment Service to create a PaymentIntent at the provider, **passing an idempotency
   key to the provider too**. Donation → `PROCESSING`.
5. If 3DS is needed, we hand back a `clientSecret`; the client completes authentication.
6. The provider finishes asynchronously and **sends us a webhook**.
7. We **verify the signature**, dedupe on the event id, ack `200` fast, and process async.
8. The consumer transitions `PROCESSING → SUCCEEDED`, writes the **ledger** entries, and emits a
   `donation.succeeded` event via the outbox.
9. Aggregation updates the rollups; Notification emails the receipt.
10. If no webhook arrives within the SLA, **reconciliation** polls the provider and resolves it.

The pattern in one line: **accept synchronously, confirm asynchronously, reconcile as a backstop.**

---

### [0:40 – 0:46] Deep dive: idempotency

**I2:** Let's drill into the scariest part. Walk me through exactly how you guarantee a donor is never
charged twice. Be concrete.

**C:** There are **three** places a duplicate can happen, and I close each one.

**One — client to our API.** The client sends a UUID idempotency key. The `donations` table has a
**UNIQUE constraint** on it. In a transaction I try to insert; on a unique violation I know this attempt
already exists, so I fetch the existing donation and **return the identical response**. I also cache
`key → result` in Redis to short-circuit fast, but the **DB unique constraint is the source of truth** —
Redis is only an optimization, so a cache miss or flush is still safe.

**Two — our API to the provider.** When we create the charge, we pass an **idempotency key to the
provider** as well. So if our call to Stripe times out and we don't know if it landed, our retry returns
the *same* charge — Stripe dedupes it. This closes the terrifying "did the money actually move?" gap.

**Three — the provider's webhooks to us.** Webhooks are delivered at-least-once and can arrive out of
order. I dedupe on the **unique `provider_event_id`**, and I apply events through the **monotonic state
machine**, so a duplicate `succeeded` is a no-op and a stale event can't regress state.

**I2:** Suppose you successfully charged the card at the provider, but your service crashed before
writing anything to your database. Now you have a charge and no record. What happens?

**C:** That's the classic gap, and the **reconciliation job** is what saves us. Two layers:
First, because I sent an idempotency key to the provider, when the client retries — or when my service
comes back and retries — I don't create a second charge; I get the same one back. Second, reconciliation
periodically asks the provider "show me recent charges" and compares against our records. It finds the
orphaned charge, locates the matching donation by the idempotency key we passed, and reconciles the
record — creating or completing the donation and writing the ledger entries. So even a crash at the
worst moment converges to a correct, single-charge state. The provider's record plus our idempotency
key together let us always reconstruct the truth.

**I2:** And what about your own internal step — you update the DB and you publish a Kafka event. What if
the DB commit succeeds but the Kafka publish fails?

**C:** That's the **dual-write problem**, and I deliberately avoid doing two independent writes. I use a
**transactional outbox**: in the *same* DB transaction that updates the donation, I insert a row into an
`outbox` table. A separate relay — Debezium/CDC tailing the WAL, or a poller — reads the outbox and
publishes to Kafka **at-least-once**, marking rows published. So either the transaction commits and the
event is guaranteed to eventually publish, or it rolls back and neither happens. The DB and the event
bus can't diverge. Consumers are idempotent, so the at-least-once publish is safe.

**I2:** Nicely done.

---

### [0:46 – 0:50] Deep dive: webhooks & reconciliation

**I1:** You lean a lot on webhooks. What if a webhook just never shows up?

**C:** I treat webhooks as **best-effort, not guaranteed** — which is why reconciliation isn't optional,
it's a core component. Concretely: any donation sitting in `PROCESSING` past an SLA — say an hour — is
picked up by a scheduled job that **polls the provider** (`GET payment_intent`) for the authoritative
status and resolves it to `SUCCEEDED` or `FAILED`. Separately, a periodic **full reconciliation**
compares our ledger against the provider's settlement/payout reports to catch any drift in amounts,
fees, or refunds. Webhooks make us *fast*; reconciliation makes us *correct*. I never trust the system
to a signal that can be silently dropped.

On the inbound side, the webhook handler **verifies the HMAC signature** before trusting anything,
persists the raw event for audit, **dedupes** on the event id, **acks 200 immediately**, and does the
real work asynchronously off a queue — so a slow handler doesn't cause the provider to time out and
retry, which would just amplify load. Poison events go to a DLQ with alerting.

**I1:** Why ack before processing? Isn't that risky — what if processing then fails?

**C:** It's safe *because* we persisted the raw event durably before acking. The ack just says "I've got
it, you can stop retrying." The actual processing happens from our durable queue with its own retries and
DLQ. If we instead did heavy processing inline before acking, the provider would frequently time out and
redeliver, and we'd be doing duplicate work under load right when we're slowest. Decoupling receipt from
processing is the resilient pattern — as long as receipt is durable, which it is.

---

### [0:50 – 0:54] Deep dive: recurring donations

**I1:** Let's talk recurring monthly donations. How do you build that without ever double-charging?

**C:** Two approaches. **Option A: provider-managed subscriptions** — Stripe Billing charges on schedule
and sends us `invoice.payment_succeeded/failed` webhooks; we just record the resulting donations. It
offloads scheduling, retries, dunning, card-expiry, and off-session SCA mandates. **Option B:
self-managed** — we store `next_charge_at`, and a scheduler scans due subscriptions and charges the saved
payment method off-session.

For v1 I'd pick **Option A** — fewer ways to get double-charging wrong and far less to operate — but I'd
keep the Payment Service as an abstraction so we can move to self-managed later for multi-provider control.

The key safety property either way is **per-period idempotency**: the idempotency key for a recurring
charge is derived from `(subscription_id, billing_period)`. So even if the scheduler runs twice, or a
worker restarts mid-batch, or a provider invoice is retried, the period can only ever produce **one**
charge. And for the self-managed scheduler I'd claim due rows with `SELECT ... FOR UPDATE SKIP LOCKED`
or a lease, so multiple scheduler instances never grab the same subscription.

**I2:** A recurring donor's card gets declined next month. What happens?

**C:** That's **dunning**. On failure we don't immediately cancel — we move the subscription to
`past_due` and retry on a backoff schedule, say days 1, 3, 5, and 7, while emailing the donor to update
their card. If it keeps failing we mark the subscription `canceled` and notify them. If they update the
card, we resume. Card-expiry we can often get ahead of using the provider's account-updater service.
None of this risks a double charge because every attempt for that period shares the same period-derived
idempotency key.

---

### [0:54 – 0:58] Deep dive: aggregates

**I1:** Last big piece — recipients want daily and monthly totals and success counts, ideally fresh.
How do you serve that at scale without melting your database?

**C:** I'd use a **hybrid** model. The donations table and ledger are the **source of truth**;
aggregates are a **derived, rebuildable read model** — that decoupling is the important part, because it
means an aggregation bug can never corrupt the real money data.

**Real-time layer:** the aggregation consumer reads `donation.succeeded/refunded` events and
incrementally updates `campaign_aggregates(campaign_id, granularity, bucket_date)`. The increments are
**idempotent** — keyed off the event id — so a redelivered event doesn't double-count. That gives
recipients near-real-time numbers.

**Batch layer:** a nightly job recomputes the rollups directly from the source of truth and corrects any
drift, and heavy historical/monthly queries are served from the warehouse. So it's fast *and* eventually
exact — Lambda architecture.

**I2:** A disaster hits and one campaign gets a flood of donations into a single aggregate row. That row
is now a write hotspot. Fix it.

**C:** Classic hot-partition. Two mitigations. First, **sharded counters**: instead of one row per
`(campaign_id, bucket_date)`, I keep N sub-counters `(campaign_id, bucket_date, shard)`, spread writes
across shards, and **sum on read**. That turns one hot row into N warm ones. Second, **batch the
increments** in the consumer — aggregate a micro-batch of events in memory and apply one combined update,
which collapses thousands of `+1`s into a handful of writes. For the live "today" number I can use Redis
`INCRBY` on a sharded key, which absorbs the burst in memory. And because aggregates are derived, if the
real-time layer ever falls behind during the spike, donations still succeed — the money path is never
blocked on aggregation, and the nightly batch reconciles the exact totals anyway.

---

### [0:58 – 1:00] Wrap-up

**I1:** We're almost out of time. Summarize, and tell me what you'd do with another hour.

**C:** Summary: this is a money system, so I optimized for **correctness over throughput** — the volume
is modest. The core is a **monotonic donation state machine** over a **relational system of record** with
an **append-only double-entry ledger**. Because charging goes through an external provider, I closed the
three duplication gaps with **end-to-end idempotency**, made confirmation **asynchronous via verified
webhooks with reconciliation as a backstop**, and used a **transactional outbox** so the database and
event bus never diverge. Recurring donations ride a **subscription model with per-period idempotency**
and dunning. Recipient aggregates are a **rebuildable, eventually-consistent read model** with sharded
counters for viral spikes. And we stay in **PCI SAQ-A** by never touching raw card data.

With another hour I'd go deeper on: **multi-currency and FX** in the ledger; **payouts/disbursement** to
recipients driven off the ledger; a **second payment provider with routing and failover** behind the
adapter; and **fraud/AML** controls — velocity limits, risk scoring, and a manual-review hold queue.

**I1:** Thanks — that was a strong, correctness-first design.

---
---

# 20. Anticipated rapid-fire follow-ups (drill these)

**Q: Why not store the card to "make recurring easier"?**
A: Never. Storing PANs drags us into the strictest PCI scope and creates breach liability. The provider
**vaults** the card and gives us a token; recurring charges reference the token. We stay SAQ-A.

**Q: SQL or NoSQL?**
A: Relational for the system of record — transactions, a ledger, relational integrity, and the volume is
low. NoSQL/warehouse for the analytics/aggregate read side. Right tool per workload.

**Q: How do you prevent a refund from being issued twice?**
A: Idempotency key on the refund call (both to our API and to the provider), and the state machine —
`SUCCEEDED → REFUNDED` only happens once; a duplicate refund webhook is a no-op.

**Q: How do recipients only see their own data?**
A: Object-level authorization — the token's identity must own the campaign being queried; enforced in the
service, not just the gateway.

**Q: What's the source of truth for "how much did this campaign raise"?**
A: The ledger / donations table. The aggregate tables are a cache and can be rebuilt from it at any time.

**Q: How do you handle a provider outage during a donation spike?**
A: Circuit-breaker around the provider; accept and queue donations as `PROCESSING` and submit with backoff
when it recovers, or fail fast and ask the donor to retry — the idempotency key makes retries safe. A
second provider behind the adapter gives true failover.

**Q: At-least-once vs exactly-once?**
A: Delivery is at-least-once everywhere. We achieve exactly-once *effects* via idempotency keys, unique
constraints, the monotonic FSM, and idempotent consumers.

**Q: Why a transactional outbox instead of just publishing to Kafka in the service?**
A: To avoid the dual-write problem. Writing to the DB and to Kafka as two separate operations can leave
them inconsistent on a crash. The outbox makes the event part of the same DB transaction; a relay
publishes it reliably afterward.

**Q: Why keyset pagination over offset?**
A: Stable under concurrent inserts and O(log n) to seek deep pages — important for hot campaigns with
millions of donations.

**Q: How do you make the recurring scheduler safe across multiple instances?**
A: Claim due rows with `FOR UPDATE SKIP LOCKED` or a lease, and use `(subscription_id, period)` as the
idempotency key so a period can only ever be charged once regardless of how many schedulers run.

---

# 21. One-page cheat sheet (glance before you walk in)

- **Frame:** money system → correctness > throughput. Volume is modest (~1–2K writes/sec peak).
- **NFRs:** exactly-once *effects*, durability, strong consistency for ledger/state, eventual for
  aggregates, PCI SAQ-A, auditability, reconcilability.
- **FSM:** CREATED→PROCESSING→(AUTHORIZED→)SUCCEEDED; →FAILED/CANCELED; SUCCEEDED→REFUNDED. Monotonic + guarded.
- **APIs:** `POST /donations` with mandatory `Idempotency-Key`, returns `processing` + `clientSecret`;
  reads with keyset pagination; `/aggregates`; `/refund`; webhook ingress.
- **Data:** Postgres SoR. `donations` (unique idempotency_key) vs `payments` (attempts). `subscriptions`,
  `webhook_events` (unique event_id), `outbox`, **double-entry `ledger_entries`**, `campaign_aggregates`.
- **Flow:** tokenize at provider → POST → insert donation+outbox in one txn → charge w/ provider
  idempotency key → webhook (verify, dedupe, ack fast, process async) → SUCCEEDED + ledger + events →
  reconciliation backstop.
- **Idempotency, 3 hops:** client→API (unique key), API→provider (provider idempotency key),
  provider→us (event-id dedupe + monotonic FSM). Plus **outbox** for DB→bus.
- **Webhooks:** verify HMAC, persist+dedupe, ack 200 fast, process async, DLQ. **Reconciliation** polls
  for missed events + settlement reconciliation.
- **Recurring:** prefer provider-managed (Stripe Billing) for v1; per-period idempotency
  `(sub_id, period)`; dunning on failure; `SKIP LOCKED` for self-managed scheduler.
- **Aggregates:** hybrid — real-time idempotent counters + nightly batch from source of truth; sharded
  counters / batched increments for hot viral campaigns; aggregates are a rebuildable cache.
- **Refunds:** idempotent; reversing ledger entries; state → REFUNDED on webhook.
- **Security:** tokenization (SAQ-A), TLS, encrypt PII, object-level authz, signed webhooks, immutable audit.
- **Trade-offs:** relational SoR, async confirm + reconcile, provider-managed recurring v1, eventual
  aggregates, exactly-once effects on at-least-once delivery.
- **More time:** multi-currency/FX, payouts off the ledger, multi-provider routing/failover, fraud/AML.
```
