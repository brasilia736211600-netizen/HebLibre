# HebLibre Resume Bootstrap

Use this compact command in every new chat, session, model, or AI agent.

```text
@GitHub @Thinking

Resume HebLibre from GitHub only.
Do NOT use chat memory, prior-agent memory, plugin memory, or unstated local state.

READ ALL CURRENT PROJECT-CONTROL DOCUMENTS UNDER `docs/` FIRST.
Treat those documents as the durable control plane, then verify the live repository state directly from GitHub.

Required order of operations:
READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → REVIEW → COMMIT → SAVE STATE

Rules:
- Human-facing output: Arabic only unless explicitly requested otherwise.
- Project-control documents: clear English.
- Internal reasoning may use English for precision; do not expose private chain-of-thought.
- GitHub is the source of truth.
- `استمر` means continue autonomously from the verified repository checkpoint.
- Never infer the current step from old chat text.
- Before coding, reconcile documentation with actual source and actual HEAD.
- Preserve verified work; do not redo completed work without new evidence.
- TDD first when a deterministic JVM seam exists.
- YAGNI, compatibility-first, low-memory, fast-startup, reliability-first.
- Persist every material decision, architecture conclusion, rejected approach, scope change, blocker, test conclusion, and completed milestone to GitHub before it can be lost.
- Every meaningful code change requires tests or an explicit reason, CI evidence when available, commit, and synchronized state.
- Serialize conflicting writes; parallelize only independent audits/research/test-design/evidence tasks.
- Do not claim tool, CI, test, emulator, or device results that were not actually verified.
- Do not repeatedly build/install APKs during feature development; physical-device validation is consolidated near release.

Current product direction:
- Lightweight, fast, reliable Android browser.
- Combine high-value profile-management/privacy patterns from mature social/multi-profile browsers and leading anti-detect products.
- Focus on legitimate privacy, profile separation, session continuity, data control, and configuration consistency.
- Do NOT implement features whose primary purpose is defeating fraud/security detection, bans, identity verification, or platform enforcement.
- Competitor features are candidates, not automatic requirements.
- Follow the active product-scope contract before adding work.

Do not silently reopen rejected or policy-blocked work. When scope changes, update the durable project-control documents before implementation.

At the end of the session, save the exact HEAD, work completed, evidence, material decisions, unresolved items, and one next executable step.
```

## Historical rule
This bootstrap intentionally does not name individual control files. The current `docs/` control plane determines the exact documents and ordering. Older resume snippets are subordinate to the current repository state.
