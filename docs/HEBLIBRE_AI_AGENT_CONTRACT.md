# HebLibre AI Agent Continuity Contract

## Purpose
This file is the durable operating contract for every future human, AI agent, plugin, or chat session working on HebLibre. It exists to prevent loss of project intent, forgetting, duplicated work, unsafe regressions, and continuity gaps.

## Communication language
- All human-facing conversation about HebLibre MUST be in Arabic unless the user explicitly requests another language.
- This language rule applies to progress updates, explanations, decisions, questions, status reports, and session handoffs in chat.
- Source code, identifiers, commit messages, CI output, and repository filenames remain in their native/project language and must not be translated merely to satisfy the conversation-language rule.
- Do not switch the conversation to English because repository artifacts are written in English; summarize their relevant content in Arabic.

## Authority hierarchy
1. The current GitHub repository and current branch are authoritative.
2. `docs/HEBLIBRE_WORKFLOW_STATE.md` is the operational checkpoint.
3. `docs/HEBLIBRE_MASTER_PROJECT_MAP.md` is the project map and scope ledger.
4. `docs/HEBLIBRE_RESUME_COMMAND.md` is the shortest bootstrap command for a new session/agent.
5. `docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md` records the separate WebLibre feature pool and must not override current source evidence.
6. Audit/decision documents record why work was deliberately not changed.
7. Chat history, model memory, plugin memory, and unstated local state are non-authoritative.

## Mandatory bootstrap
Before modifying code, every new session MUST:
- read this file;
- read `docs/HEBLIBRE_WORKFLOW_STATE.md`;
- read `docs/HEBLIBRE_MASTER_PROJECT_MAP.md`;
- read `docs/HEBLIBRE_RESUME_COMMAND.md`;
- read the gap matrix when feature scope is involved;
- verify the actual `genspark-dev` ref/HEAD directly from GitHub;
- verify relevant recent CI state against the actual source checkpoint.

Do not infer current state from remembered conversation text.

## Canonical execution loop
`READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → REVIEW → COMMIT → SAVE STATE`

A task is not complete until the resulting state is committed to GitHub and the workflow state is updated.

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
- Prefer compatibility-preserving changes unless a deliberate product contract says otherwise.
- Prefer small deterministic policy seams and dependency-free JVM tests.
- Use TDD when a meaningful JVM seam exists: write the smallest failing/edge-case tests first, then implementation.
- Avoid broad refactors when a local change is sufficient.
- Preserve existing UX contracts unless the replacement is complete and explicitly justified.
- Do not resurrect reverted experiments without new evidence.
- Treat security-sensitive behavior as a policy decision when changing it would alter compatibility or user expectations.

## Parallel execution rules
Parallelize only genuinely independent workstreams. Typical safe parallel lanes are:
- bounded source audits;
- dependency-free policy/test design;
- documentation reconciliation;
- CI/result inspection.

Serialize dependent mutations to the same branch/file. Never perform conflicting writes to the same path concurrently.

## Tooling rules
Use the agreed add-ons only when their actual capability is available and useful:
- process/coordinator tooling for independent work units or orchestration;
- advisor tooling for non-trivial architectural decisions;
- CodeRabbit only when a usable repository/CLI review surface actually exists;
- memory/prompt tooling only as convenience, never as authority.

Never claim an add-on result that was not actually produced.

## Android validation policy
Do not repeatedly build/install the APK during feature development. First complete source review, deterministic tests, CI, review, and documentation. Then perform consolidated Android validation.

Distinguish:
- GitHub-hosted emulator validation;
- physical target-device validation.

A successful emulator run does not prove physical-device correctness.

## Git safety and continuity
Every meaningful change MUST leave a durable trail:
1. source change;
2. tests or explicit reason they are not applicable;
3. CI evidence when available;
4. commit;
5. workflow-state update;
6. map/backlog update when scope or priority changed.

Keep verified checkpoint SHAs and CI run IDs in the workflow state and project map.

## Reversion rule
When a change is rejected, reverted, or shown to be incomplete:
- return the runtime source to the last clean verified behavior;
- document the reason;
- retain the rejected design only as a documented decision/audit when it prevents future repetition;
- do not count the rejected change as a completed feature.

## Decision rule for deferred work
A deferred feature is not forgotten. It must have:
- a named scope;
- a status (`DEFERRED`, `PARTIAL`, `MEDIUM`, `ARCHITECTURAL`, or `NOT TARGETED` as appropriate);
- a reason;
- the condition that would justify promotion.

## Current project intent
HebLibre is a lightweight Android WebView browser derived from FOSS Browser. Current work prioritizes privacy, compatibility-preserving hardening, deterministic policy extraction, profile-aware whitelist handling, download privacy, tab management primitives, and a reliable engineering/continuity process.

The separate WebLibre project is a feature/design source pool only. It is not continuity authority for HebLibre.

## Current release gate
The bounded implementation scope is complete at source/test/CI level for the features recorded in the master map. The remaining release-confidence gate is consolidated physical-device validation, followed by one final corrective CI/device cycle if runtime defects are discovered.

## Do-not-forget security decisions
The following remain deliberate decisions unless a new explicit product/architecture contract is recorded:
- SSL certificate error override policy;
- application-level cleartext traffic;
- automatic Android backup semantics for `Ninja4.db`;
- `sp_remote` coupling of file-origin access and DOM storage;
- complete profile storage isolation;
- architectural networking/privacy features such as DoH, per-container proxy/Tor, broad fingerprinting defenses, and full WebRTC privacy.

## Session handoff minimum
At the end of every meaningful work session, update at least `docs/HEBLIBRE_WORKFLOW_STATE.md` with:
- current branch and HEAD;
- exact completed change;
- tests and CI evidence;
- unresolved items;
- next executable step;
- whether Android runtime is pending or complete.
