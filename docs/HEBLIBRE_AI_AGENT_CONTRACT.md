# HebLibre AI Agent Continuity Contract

## Purpose
This file is the durable operating contract for every future human, AI agent, plugin, or chat session working on HebLibre. It exists to prevent loss of project intent, forgetting, duplicated work, unsafe regressions, and continuity gaps.

## Communication language
- All human-facing conversation about HebLibre MUST be in Arabic unless the user explicitly requests another language.
- Repository source code, identifiers, commit messages, CI output, and filenames remain in their native/project language.
- Durable project-control documents must be written in clear English to minimize ambiguity between sessions and agents.
- Internal reasoning may be performed in English when useful for precision; human-facing output remains Arabic.

## Authority hierarchy
1. The current GitHub repository and current branch are authoritative.
2. `docs/HEBLIBRE_WORKFLOW_STATE.md` is the operational checkpoint.
3. `docs/HEBLIBRE_MASTER_PROJECT_MAP.md` is the project scope ledger.
4. `docs/HEBLIBRE_EXECUTION_BOARD.md` is the executable task board and release checklist.
5. `docs/HEBLIBRE_DECISION_LOG.md` is the historical decision authority for intentionally deferred or rejected paths.
6. `docs/HEBLIBRE_PRODUCT_SCOPE_2026-09-06.md` is the active product-direction contract.
7. `docs/HEBLIBRE_RESUME_COMMAND.md` is the shortest bootstrap instruction for a new session or agent.
8. Audit and source-analysis documents preserve evidence and rationale.
9. Chat history, model memory, plugin memory, and unstated local state are non-authoritative.

## Mandatory bootstrap
Before modifying code, every new session MUST:
- inspect the `docs/` control-plane area and read all current project-control documents;
- establish which document is the current operational checkpoint and which is the current product scope;
- verify the actual `genspark-dev` ref/HEAD directly from GitHub;
- reconcile the live source tree against the latest documented application-source checkpoint;
- verify relevant CI/runtime evidence against the exact source checkpoint;
- identify one executable next step before making changes.

Documentation is guidance, not proof. Current source and current GitHub evidence override stale documentation.

## Canonical execution loop
`READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → REVIEW → COMMIT → SAVE STATE`

A task is not complete until the resulting state is committed to GitHub and the operational checkpoint is synchronized.

## Durable continuity rule
Every meaningful engineering step must leave enough information in GitHub for a completely new agent to resume without chat context. The checkpoint must record:
- current branch and exact HEAD;
- source checkpoint being changed;
- exact task being executed;
- key reasoning or decision that materially affects implementation;
- files changed or intentionally left unchanged;
- tests and exact CI evidence;
- unresolved questions or blockers;
- one next executable step;
- whether Android runtime validation is pending, emulator-verified, or physical-device-verified.

Do not require the next agent to reconstruct important context from prose in the chat.

## Important-step persistence rule
Do not commit every trivial thought or intermediate observation. Persist every material decision, architecture conclusion, rejected approach, security conclusion, scope change, test conclusion, or discovered blocker before it can be lost.

## Verification vocabulary
Keep these labels separate:
- `SOURCE-VERIFIED`: current source was inspected directly.
- `TEST-VERIFIED`: deterministic tests cover the behavior and have passed.
- `CI-VERIFIED`: GitHub Actions passed for the exact relevant source checkpoint.
- `ANDROID-RUNTIME-VERIFIED`: behavior was executed on Android; emulator and physical-device evidence must be distinguished.
- `DOCUMENTED`: intent, decision, or result is persisted in repository documentation.

Never turn a checked box into runtime evidence.

## Project engineering principles
- YAGNI: implement only demonstrated, bounded value.
- TDD first when a meaningful deterministic JVM seam exists.
- Prefer compatibility-preserving changes unless a deliberate product contract says otherwise.
- Prefer small deterministic policy seams and dependency-free JVM tests.
- Avoid broad refactors when a local change is sufficient.
- Preserve existing UX contracts unless the replacement is complete and explicitly justified.
- Do not resurrect reverted experiments without new evidence.
- Security-sensitive behavior that changes compatibility or trust semantics requires an explicit decision entry.
- Optimize for low memory use, fast startup, minimal dependencies, and predictable lifecycle behavior.

## Product-scope control
The active product direction is defined by `docs/HEBLIBRE_PRODUCT_SCOPE_2026-09-06.md`.

The product direction supersedes older chat-only feature requests. The target is a lightweight Android browser combining useful profile-management and privacy capabilities from mature multi-profile/social browsers with legitimate privacy-oriented lessons from leading anti-detect browsers.

Anti-detect capability is interpreted as privacy/profile isolation and configuration consistency. Features whose primary purpose is to defeat fraud systems, platform security, identity verification, bans, or detection are excluded.

Competitor features are requirements candidates, not automatic implementation requirements. Every candidate must pass the scope gates, implementation-seam test, performance test, and YAGNI review before entering the execution board.

## Parallel execution rules
Parallelize only genuinely independent workstreams such as:
- bounded source audits;
- deterministic policy/test design;
- product-scope research;
- documentation reconciliation;
- CI/evidence inspection.

Serialize dependent mutations to the same branch/file. Never perform conflicting writes to the same path concurrently.

## Tooling rules
Use agreed add-ons only when their actual capability is available and useful:
- process/coordinator tooling for independent work units or orchestration;
- advisor tooling for non-trivial architectural decisions;
- CodeRabbit only when a usable repository/CLI review surface actually exists;
- memory/prompt tooling only as convenience, never as authority.

Never claim an add-on result that was not actually produced.

## Android validation policy
Do not repeatedly build/install the APK during feature development. First complete source review, deterministic tests, CI, review, and documentation. Then perform consolidated Android validation.

Distinguish GitHub-hosted emulator evidence from physical target-device evidence.

## Git safety and continuity
Every meaningful source change MUST leave a durable trail:
1. source change;
2. tests or explicit reason they are not applicable;
3. CI evidence when available;
4. commit;
5. workflow-state update;
6. map/backlog update when scope or priority changed;
7. decision-log update when a material decision changed.

## Reversion rule
When a change is rejected, reverted, or shown to be incomplete:
- return runtime source to the last clean verified behavior;
- document the reason;
- do not count the rejected change as a feature;
- record the rejected path when doing so prevents future repetition.

## Decision rule for deferred work
A deferred feature is not forgotten. It must have a named scope, explicit status, reason, promotion condition, and owner document.

## Current project state contract
Do not assume the project is still at an older checkpoint. Verify the live branch and current source before every implementation session. Documentation may itself need reconciliation before coding begins.

## Session handoff minimum
At the end of every meaningful session, synchronize the operational checkpoint with:
- live HEAD;
- exact work completed;
- exact evidence;
- material reasoning/decisions;
- unresolved items;
- next executable step;
- Android validation state.

## Historical scope reset
On 2026-09-06 the product direction was intentionally reset: older chat-only feature requests are no longer authoritative. The new scope is the 2026 product-scope document plus the current source and decision evidence in GitHub.
