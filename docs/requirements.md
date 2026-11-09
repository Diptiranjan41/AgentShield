# AgentShield Requirements Specification

**Product:** a runtime security gateway that authorizes every AI agent tool call
by identity, user delegation, context and risk.

**Core principle:** an agent may request an action, but it must never decide its own authority.
The gateway derives identity, user and action itself. It never trusts them from the agent.

## How to read this document

| Field | Meaning |
|---|---|
| **ID** | `REQ-<AREA>-<NNN>`. Tests reference it with `@Tag("REQ-ID-001")`. |
| **Pri** | `MUST` = required for its phase to count as done. `SHOULD` = do if time allows. `COULD` = cut first. |
| **Phase** | Week group in the pipeline (see Phase map). CI enforces tests for every phase up to the current one. |
| **Verify** | `Test` = automated test, enforced by CI. `Review` = checked by reading docs or code, not automated. |
| **Status** | `TODO` -> `DONE`, only when the test is green in CI. |

**Phase map:** 0 Foundations (wk 1) | 1 Identity + Audit (wk 2-3) | 2 Policy engine (wk 3-4) |
3 Gateway + eval harness (wk 5-6) | 4 Context, risk, injection (wk 7-8) | 5 Delegation (wk 9) |
6 Approval (wk 10) | 7 Dashboard (wk 11) | 8 Evaluation + report (wk 12)

**Cut line:** MVP = phases 0-3. Strong = add phases 4 and 6. Full = add phases 5, 7 and the ablations.
If time runs short, drop whole phases from the end (dashboard first), never the evaluation.

Rules for every requirement: it must be testable, one behavior per row, and fail closed
(when unsure, deny).

---

## 1. Documentation and foundations (REQ-DOC)

| ID | Requirement | Pri | Phase | Verify | Status |
|---|---|---|---|---|---|
| REQ-DOC-001 | `docs/threat-model.md` defines attacker types, protected assets and trust boundaries | MUST | 0 | Review | TODO |
| REQ-DOC-002 | `docs/attack-catalog.md` lists at least 20 attacks, each with ID, category, expected decision and reason | MUST | 0 | Review | TODO |
| REQ-DOC-003 | `docs/policy-schema.md` defines the policy format including `effect`, `scope`, `expiresAt`, `conditions` | MUST | 0 | Review | TODO |
| REQ-DOC-004 | `docs/risk-model.md` documents every risk factor, its weight and the decision thresholds | SHOULD | 4 | Review | TODO |
| REQ-DOC-005 | README contains a one-command demo (`docker compose up`) and the demo attack script | SHOULD | 8 | Review | TODO |
| REQ-DOC-006 | A limitations section states what the system cannot stop (for example a fully authorized agent misusing legitimate access) | MUST | 8 | Review | TODO |

## 2. Agent identity (REQ-ID)

| ID | Requirement | Pri | Phase | Verify | Status |
|---|---|---|---|---|---|
| REQ-ID-001 | `POST /api/agents` with valid data returns 201 and the new `agentId` | MUST | 1 | Test | TODO |
| REQ-ID-002 | Every registered agent gets a unique `agentId` | MUST | 1 | Test | TODO |
| REQ-ID-003 | Registering an agent with an existing name returns 409 | MUST | 1 | Test | TODO |
| REQ-ID-004 | A newly registered agent has status `ACTIVE` | MUST | 1 | Test | TODO |
| REQ-ID-005 | Registration with a missing name or owner returns 400 | MUST | 1 | Test | TODO |
| REQ-ID-006 | An agent can be moved to `SUSPENDED` or `REVOKED` by an admin | MUST | 1 | Test | TODO |
| REQ-ID-007 | A `SUSPENDED` or `REVOKED` agent cannot authenticate | MUST | 1 | Test | TODO |
| REQ-ID-008 | Each agent is bound to the user it acts for (`ownerUserId`) | MUST | 1 | Test | TODO |
| REQ-ID-009 | Agent credentials expire, and an expired credential is rejected | MUST | 1 | Test | TODO |
| REQ-ID-010 | Credentials are signed tokens carrying `agentId` and `ownerUserId`; unsigned (`alg=none`) or tampered tokens are rejected | MUST | 1 | Test | TODO |
| REQ-ID-011 | `REVOKED` is terminal: a revoked agent can never return to `ACTIVE` | MUST | 1 | Test | TODO |
| REQ-ID-012 | Revoking an agent invalidates all of its credentials immediately (the next request is denied) | MUST | 1 | Test | TODO |
| REQ-ID-013 | Registration and status endpoints require the `ADMIN` role; an agent credential cannot call them | MUST | 1 | Test | TODO |
| REQ-ID-014 | Agent names follow a validated pattern and length limit; invalid names return 400 | SHOULD | 1 | Test | TODO |
| REQ-ID-015 | Credential secrets are shown once at creation and stored only as hashes | SHOULD | 1 | Test | TODO |

## 3. Audit trail (REQ-AUD)

| ID | Requirement | Pri | Phase | Verify | Status |
|---|---|---|---|---|---|
| REQ-AUD-001 | Every registration and status change writes an audit event | MUST | 1 | Test | TODO |
| REQ-AUD-002 | Each audit event stores the hash of the previous event | MUST | 1 | Test | TODO |
| REQ-AUD-003 | `verifyChain()` detects a modified, deleted or reordered event | MUST | 1 | Test | TODO |
| REQ-AUD-004 | Every gateway decision (ALLOW, DENY, APPROVAL_REQUIRED) writes an event with reason code, policy version, risk score and delegation chain | MUST | 3 | Test | TODO |
| REQ-AUD-005 | 50 concurrent appends produce one valid, unforked chain | MUST | 1 | Test | TODO |
| REQ-AUD-006 | The application database role has no UPDATE or DELETE permission on the audit table | SHOULD | 1 | Test | TODO |
| REQ-AUD-007 | If an audit event cannot be written, the tool call is not executed (fail closed) | MUST | 3 | Test | TODO |
| REQ-AUD-008 | Audit events never contain credentials, tokens or raw sensitive payloads | MUST | 1 | Test | TODO |
| REQ-AUD-009 | Audit events can be queried by agent, user, time range and decision | SHOULD | 3 | Test | TODO |

## 4. Policy engine (REQ-POL)

| ID | Requirement | Pri | Phase | Verify | Status |
|---|---|---|---|---|---|
| REQ-POL-001 | A request with no matching ALLOW policy is denied (`NO_MATCHING_ALLOW`) | MUST | 2 | Test | TODO |
| REQ-POL-002 | An explicit DENY overrides any ALLOW (`EXPLICIT_DENY`) | MUST | 2 | Test | TODO |
| REQ-POL-003 | Permissions are scoped to a specific resource; access to another resource is denied | MUST | 2 | Test | TODO |
| REQ-POL-004 | An expired permission is rejected (`PERMISSION_EXPIRED`) | MUST | 2 | Test | TODO |
| REQ-POL-005 | Every decision returns a machine-readable reason code from a documented list | MUST | 2 | Test | TODO |
| REQ-POL-006 | Every decision records the policy version that produced it | MUST | 2 | Test | TODO |
| REQ-POL-007 | Policies are validated against the schema; an invalid policy is rejected on load | MUST | 2 | Test | TODO |
| REQ-POL-008 | A policy change creates a new version; old versions are immutable | SHOULD | 2 | Test | TODO |
| REQ-POL-009 | Evaluation is deterministic: the same input and policy version always give the same decision (property-based test) | MUST | 2 | Test | TODO |
| REQ-POL-010 | Any internal engine error results in DENY (`ENGINE_ERROR`), never ALLOW | MUST | 2 | Test | TODO |
| REQ-POL-011 | Wildcards are allowed only in documented positions; user-supplied regex is never evaluated | SHOULD | 2 | Test | TODO |
| REQ-POL-012 | An agent can never access a resource owned by a user other than its bound user (cross-user access) | MUST | 2 | Test | TODO |
| REQ-POL-013 | A static RBAC baseline mode exists for evaluation comparison | MUST | 2 | Test | TODO |
| REQ-POL-014 | An unrestricted baseline mode exists and is disabled outside the `eval` profile | MUST | 2 | Test | TODO |

## 5. Tool gateway (REQ-GW)

| ID | Requirement | Pri | Phase | Verify | Status |
|---|---|---|---|---|---|
| REQ-GW-001 | A tool-call request contains only the credential, tool name and parameters; `agentId` and `userId` are derived server-side from the credential | MUST | 3 | Test | TODO |
| REQ-GW-002 | An `agentId`, `userId` or `action` supplied in the request body is rejected with 400 | MUST | 3 | Test | TODO |
| REQ-GW-003 | Action and resource type are derived from the tool registry, not from the request | MUST | 3 | Test | TODO |
| REQ-GW-004 | An unregistered tool is denied (`TOOL_NOT_REGISTERED`) | MUST | 3 | Test | TODO |
| REQ-GW-005 | Tool parameters are validated against the tool's declared schema; invalid input is rejected | MUST | 3 | Test | TODO |
| REQ-GW-006 | Mock tools accept calls only from the gateway; a direct call without the gateway's service identity is rejected | MUST | 3 | Test | TODO |
| REQ-GW-007 | No response, log or audit event ever contains tool-service credentials | MUST | 3 | Test | TODO |
| REQ-GW-008 | Every response includes the decision, reason code and a `requestId` | MUST | 3 | Test | TODO |
| REQ-GW-009 | Authorization is re-evaluated at execution time, not only when the request arrives | MUST | 3 | Test | TODO |
| REQ-GW-010 | Each tool declares action, resource type, sensitivity and side effect (`READ`, `WRITE`, `EXTERNAL`) | MUST | 3 | Test | TODO |
| REQ-GW-011 | Tool execution has a timeout and a result size limit | SHOULD | 3 | Test | TODO |
| REQ-GW-012 | Per-agent rate limit; excess calls are denied (`RATE_LIMITED`) | SHOULD | 4 | Test | TODO |
| REQ-GW-013 | A reused `requestId` is rejected (replay protection) | SHOULD | 3 | Test | TODO |
| REQ-GW-014 | The mock tool set includes invoice read, customer database read/export, email send and payment execute | MUST | 3 | Test | TODO |

## 6. Context, risk and injection defense (REQ-CTX, REQ-RSK, REQ-INJ)

| ID | Requirement | Pri | Phase | Verify | Status |
|---|---|---|---|---|---|
| REQ-CTX-001 | Every session has a taint state: `CLEAN` or `TAINTED` | MUST | 4 | Test | TODO |
| REQ-CTX-002 | The agent runtime reports untrusted-content ingestion through a provenance API; the gateway then marks the session `TAINTED` | MUST | 4 | Test | TODO |
| REQ-CTX-003 | An agent cannot clear its own taint; only a new session or an admin can | MUST | 4 | Test | TODO |
| REQ-CTX-004 | In a `TAINTED` session, tools with `HIGH` sensitivity or `EXTERNAL` side effects are denied or need approval, as policy says | MUST | 4 | Test | TODO |
| REQ-CTX-005 | Resources carry sensitivity labels: `PUBLIC`, `INTERNAL`, `CONFIDENTIAL` | MUST | 4 | Test | TODO |
| REQ-CTX-006 | External tools (email, HTTP) send only to an allowlisted destination | MUST | 4 | Test | TODO |
| REQ-CTX-007 | A destination not on the allowlist is denied (`DESTINATION_NOT_ALLOWED`) even when the user may export the data | MUST | 4 | Test | TODO |
| REQ-CTX-008 | Destinations are normalized before matching, so tricks like `allowed.com@evil.com`, mixed case and look-alike subdomains do not bypass the allowlist | MUST | 4 | Test | TODO |
| REQ-RSK-001 | The risk score (0-100) is rule-based and explainable; weights are documented | MUST | 4 | Test | TODO |
| REQ-RSK-002 | A decision returns the list of factors that raised the score | SHOULD | 4 | Test | TODO |
| REQ-RSK-003 | Configurable thresholds map a score to ALLOW, APPROVAL_REQUIRED or DENY | MUST | 4 | Test | TODO |
| REQ-RSK-004 | Per-session action budgets limit volume (for example the number of records read) | SHOULD | 4 | Test | TODO |
| REQ-RSK-005 | Sequence rule: reading `CONFIDENTIAL` data and then calling an `EXTERNAL` tool in one session raises the risk | SHOULD | 4 | Test | TODO |
| REQ-RSK-006 | Risk scoring is deterministic for the same inputs | MUST | 4 | Test | TODO |
| REQ-INJ-001 | The injection classifier is optional and advisory only; it can raise risk but never grant access | MUST | 4 | Test | TODO |
| REQ-INJ-002 | With the classifier disabled, every injection scenario in the attack catalog is still blocked by policy | MUST | 4 | Test | TODO |
| REQ-INJ-003 | A classifier failure or timeout never moves a decision toward ALLOW | MUST | 4 | Test | TODO |
| REQ-INJ-004 | Each blocked high-risk call in a tainted session raises a security event | SHOULD | 4 | Test | TODO |
| REQ-INJ-005 | Every attack in the catalog has an automated test tagged `attack` | MUST | 4 | Test | TODO |

## 7. Delegation (REQ-DEL)

| ID | Requirement | Pri | Phase | Verify | Status |
|---|---|---|---|---|---|
| REQ-DEL-001 | A child agent receives only an explicit scope given by its parent | MUST | 5 | Test | TODO |
| REQ-DEL-002 | A child scope must be a subset of the parent scope; a widening attempt is rejected | MUST | 5 | Test | TODO |
| REQ-DEL-003 | A delegation expires no later than its parent | MUST | 5 | Test | TODO |
| REQ-DEL-004 | Delegation depth is limited (default 3) | MUST | 5 | Test | TODO |
| REQ-DEL-005 | Revoking a parent revokes all descendants | MUST | 5 | Test | TODO |
| REQ-DEL-006 | A child cannot delegate a permission it did not receive, including the right to delegate | MUST | 5 | Test | TODO |
| REQ-DEL-007 | Every audit event for a delegated call includes the full delegation chain | MUST | 5 | Test | TODO |
| REQ-DEL-008 | Delegation cycles are rejected | SHOULD | 5 | Test | TODO |
| REQ-DEL-009 | A child always acts for the same user as its parent (no user switching, no confused deputy) | MUST | 5 | Test | TODO |
| REQ-DEL-010 | Calls from a child whose parent is suspended or revoked are denied | MUST | 5 | Test | TODO |

## 8. Human approval (REQ-APR)

| ID | Requirement | Pri | Phase | Verify | Status |
|---|---|---|---|---|---|
| REQ-APR-001 | An APPROVAL_REQUIRED decision creates a request holding a canonical hash of agent, user, tool, resource and parameters | MUST | 6 | Test | TODO |
| REQ-APR-002 | An approval is valid only for the exact hashed request; any changed parameter voids it | MUST | 6 | Test | TODO |
| REQ-APR-003 | An approval can be used once; a replay is rejected | MUST | 6 | Test | TODO |
| REQ-APR-004 | An approval expires after a configurable time (default 10 minutes) | MUST | 6 | Test | TODO |
| REQ-APR-005 | The approver must hold the `APPROVER` role and must not be the requesting user | MUST | 6 | Test | TODO |
| REQ-APR-006 | Policy is re-evaluated at execution; if it now says DENY, an existing approval does not help | MUST | 6 | Test | TODO |
| REQ-APR-007 | A rejection is recorded and the agent receives a clear rejected result | MUST | 6 | Test | TODO |
| REQ-APR-008 | Request, approval, rejection and expiry are all written to the audit trail | MUST | 6 | Test | TODO |
| REQ-APR-009 | An agent credential cannot call approval endpoints | MUST | 6 | Test | TODO |

## 9. Security dashboard (REQ-DSH)

| ID | Requirement | Pri | Phase | Verify | Status |
|---|---|---|---|---|---|
| REQ-DSH-001 | A live feed shows recent decisions with agent, user, tool and reason | SHOULD | 7 | Test | TODO |
| REQ-DSH-002 | An approval queue shows pending requests with full action details | SHOULD | 7 | Test | TODO |
| REQ-DSH-003 | A delegation graph shows the parent and child chain for a selected call | COULD | 7 | Review | TODO |
| REQ-DSH-004 | A button runs chain verification and shows pass or fail | SHOULD | 7 | Test | TODO |
| REQ-DSH-005 | Selecting a denied call explains why (reason code, policy version, risk factors) | SHOULD | 7 | Test | TODO |
| REQ-DSH-006 | Dashboard APIs require authentication and the correct role | MUST | 7 | Test | TODO |
| REQ-DSH-007 | Counts of allowed, denied and approval-required calls are shown | COULD | 7 | Test | TODO |

## 10. Evaluation and research (REQ-EVL)

| ID | Requirement | Pri | Phase | Verify | Status |
|---|---|---|---|---|---|
| REQ-EVL-001 | Scenarios are JSON files with input, expected decision and expected reason | MUST | 3 | Test | TODO |
| REQ-EVL-002 | The runner executes the same scenario suite against three modes: unrestricted, static RBAC, AgentShield | MUST | 3 | Test | TODO |
| REQ-EVL-003 | The harness computes attack block rate, false positive rate, legitimate task success rate, and p50/p95 latency | MUST | 3 | Test | TODO |
| REQ-EVL-004 | The suite has at least 100 scenarios: at least 50 attacks and at least 50 legitimate tasks | MUST | 8 | Test | TODO |
| REQ-EVL-005 | Scenarios are split into a development set and a held-out set; policies are never tuned on the held-out set | MUST | 8 | Review | TODO |
| REQ-EVL-006 | Ablations disable taint tracking, destination allowlist and delegation limits one at a time, and report the effect | SHOULD | 8 | Test | TODO |
| REQ-EVL-007 | Results are written as JSON and `summary.md` and uploaded as a CI artifact | MUST | 3 | Test | TODO |
| REQ-EVL-008 | Attack categories covered: prompt injection, data exfiltration, privilege escalation, cross-user access, confused deputy, replay, delegation abuse, audit tampering | MUST | 8 | Review | TODO |
| REQ-EVL-009 | Evaluation runs are reproducible (fixed seeds, versioned scenario files) | SHOULD | 3 | Test | TODO |

## 11. Non-functional requirements (REQ-NFR)

| ID | Requirement | Pri | Phase | Verify | Status |
|---|---|---|---|---|---|
| REQ-NFR-001 | Authorization p95 latency is under 50 ms at 100 requests per second, excluding tool execution (target, measured in evaluation) | SHOULD | 8 | Test | TODO |
| REQ-NFR-002 | The system fails closed: if the database or any dependency is unavailable, calls are denied | MUST | 3 | Test | TODO |
| REQ-NFR-003 | No secrets are stored in the repository; configuration comes from environment variables | MUST | 1 | Review | TODO |
| REQ-NFR-004 | All request bodies are validated and have a maximum size | MUST | 3 | Test | TODO |
| REQ-NFR-005 | Database changes are made only through Flyway migrations | MUST | 1 | Review | TODO |
| REQ-NFR-006 | Logs are structured and carry the `requestId` | SHOULD | 3 | Test | TODO |
| REQ-NFR-007 | Line coverage is at least 80 percent on the `authorization`, `gateway` and `audit` packages | SHOULD | 4 | Test | TODO |
| REQ-NFR-008 | A high-severity vulnerable dependency fails the build | SHOULD | 1 | Review | TODO |
| REQ-NFR-009 | The full system starts with a single `docker compose up` | SHOULD | 8 | Review | TODO |

## 12. Out of scope

- Building or training the AI agent itself. AgentShield works with any agent that routes tool calls through it.
- Detecting prompt injection with high accuracy. The classifier is only a risk signal.
- Protecting against an agent misusing access it is legitimately authorized for (documented as a limitation).
- Multi-region deployment, Kafka, Redis or cloud hosting.

## 13. Traceability rule

CI runs `scripts/check-traceability.sh`. For every requirement with `Verify = Test` and a phase up to
the current phase (set in `docs/current-phase.txt`), at least one test under `src/test` must contain its ID.
It also fails if a test references an ID that does not exist in this file.