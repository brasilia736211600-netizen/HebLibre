# HebLibre Resume Command

Use this exact bootstrap whenever work resumes in a new chat, session, model, or AI agent.

```text
@GitHub @Thinking

Resume HebLibre from GitHub only. Do NOT rely on chat memory, prior-agent memory, plugin memory, or unstated local state.

Repository: brasilia736211600-netizen/HebLibre
Active branch: genspark-dev

READ THESE DURABLE FILES FIRST, IN ORDER:
1. docs/HEBLIBRE_AI_AGENT_CONTRACT.md
2. docs/HEBLIBRE_WORKFLOW_STATE.md
3. docs/HEBLIBRE_MASTER_PROJECT_MAP.md
4. docs/HEBLIBRE_EXECUTION_BOARD.md
5. docs/HEBLIBRE_DECISION_LOG.md
6. docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md
7. Verify refs/current HEAD directly from GitHub.
8. Verify relevant CI/runtime evidence against the actual HEAD/checkpoint.

EXECUTE:
READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → REVIEW → COMMIT → SAVE STATE

CONTINUITY RULES:
- GitHub is the source of truth.
- `استمر` / `continue` means continue autonomously from the repository state; do not wait for routine confirmation.
- Every meaningful change must end with a durable Git commit and synchronized workflow state.
- Never claim work was done, tested, reviewed, or runtime-validated without repository/evidence support.
- Keep SOURCE-VERIFIED, TEST-VERIFIED, CI-VERIFIED, ANDROID-RUNTIME-VERIFIED, and DOCUMENTED distinct.
- TDD first when a useful deterministic JVM seam exists.
- Apply YAGNI and compatibility-first engineering.
- Re-read current source before implementing; documentation may be stale.
- Rejected/reverted experiments are not completed features.
- Preserve established UX contracts unless a complete replacement path is explicitly justified.
- WebLibre is a separate feature/design source pool, not continuity authority.
- Use process/coordinator/advisor/memory/prompt tooling only when actually available and useful; never fabricate tool results.
- Use CodeRabbit only when its real review surface is available; never claim a CodeRabbit result otherwise.

ANDROID RULE:
Do not repeatedly build/install/test the APK during feature work. Finish source review, deterministic tests, CI, review, and documentation first. Then perform one consolidated physical-device validation pass. Emulator evidence does not equal physical-device evidence.

CURRENT VERIFIED SCOPE:
- P2.1–P2.11 complete at recorded SOURCE/TEST/CI levels.
- Download cookie privacy complete and CI-VERIFIED.
- Tab reorder core complete and CI-VERIFIED; dedicated UI remains partial.
- Remote-content default consistency complete and CI-VERIFIED.
- Whitelist import/export is profile-aware in both active and legacy paths and CI-VERIFIED; latest source fix: `247768c4e2e442fcb9b42d299d8cf00d3c24b81b`; consolidated Unit Tests run `33985143542` passed.
- GitHub-hosted Android emulator smoke completed successfully on a CI checkpoint; this is not physical-device validation.
- Current branch may contain documentation/CI hardening commits after the source checkpoint; always verify actual HEAD.

DO NOT SILENTLY REOPEN:
- SSL certificate override policy.
- Application-level cleartext traffic policy.
- Automatic backup semantics for `Ninja4.db`.
- `sp_remote` coupling of file-origin access and DOM storage.
- Complete profile/WebView storage isolation.
- DoH, per-container proxy/Tor, broad fingerprinting defenses, full WebRTC privacy, extensions/uBlock, and on-device AI architecture.

CURRENT RELEASE GATE:
Read `docs/HEBLIBRE_EXECUTION_BOARD.md`. The bounded implementation scope is complete for the recorded feature set. The next major executable gate is physical target-device validation. Record any runtime defects, batch related fixes, run CI, and perform one final physical-device recheck.
```

## Handoff requirement
At the end of a session, update `docs/HEBLIBRE_WORKFLOW_STATE.md` with the exact HEAD, completed work, evidence, unresolved decisions, and next executable step. Update the master map/execution board when scope changes.

## Historical rule
Do not trust old resume snippets that conflict with current GitHub state. The current branch, current source, current tests/CI, and these durable documents win.