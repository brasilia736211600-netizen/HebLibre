# HebLibre Resume Command

```text
@GitHub @Thinking

Resume HebLibre from GitHub only. Do NOT rely on chat memory, prior-agent memory, plugin memory, or unstated local state.

Repository: brasilia736211600-netizen/HebLibre
Active branch: genspark-dev

Read first:
1. docs/HEBLIBRE_WORKFLOW_STATE.md
2. docs/HEBLIBRE_MASTER_PROJECT_MAP.md
3. docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md
4. Verify current branch and HEAD directly from GitHub
5. Verify latest relevant CI/check state

Execute:
READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → REVIEW → COMMIT → SAVE STATE

Rules:
- `استمر` / `continue` means continue autonomously; do not emit routine progress logs.
- Apply YAGNI and evidence-based claims.
- Keep SOURCE-VERIFIED, TEST-VERIFIED, CI-VERIFIED, ANDROID-RUNTIME-VERIFIED, and DOCUMENTED distinct.
- TDD first when deterministic JVM testing is possible.
- Re-read current source before implementing candidates; old matrix entries may be stale.
- WebLibre is a separate feature/design source pool, not continuity authority.
- Do not repeatedly build/install/test the APK. Reserve Android runtime verification for one consolidated final device-validation phase after source, JVM, CI, review, and documentation work is mature.
- Use CodeRabbit only when its required local CLI/repository surface is actually available; never claim a result when unavailable.

Current verified state:
- P2.1–P2.10 are CI-VERIFIED.
- P2.9 Geolocation: Unit Tests run `33684710168` success, head `e48c1f036aa4c7fcaea7339735c7fe81201c5d9d`.
- P2.10 Save-Data: Unit Tests run `33686256788` success, head `1a71cd2eb358bfd57f3209d141fc40253183dff1`.
- Current branch HEAD after documentation reconciliation is advanced beyond the feature HEAD; verify it directly on resume.
- Android runtime is deferred to final validation.
- Reader Mode is NOT TARGETED for the current P2 cycle: source tracing found no bounded dependency-free reader-extraction seam in the native WebView architecture, so speculative HTML/JS injection is intentionally excluded.

Next execution:
Source-verify global settings search as the smallest remaining high-value local UX seam. Implement only if the existing settings architecture supports a bounded deterministic change; otherwise choose the next smallest bounded privacy/UX feature. Do not install the APK.
```

## Current authoritative checkpoint
P2.1–P2.10 are CI-VERIFIED; Reader Mode has been closed as non-bounded for this cycle; global settings search is the next source-verification target; Android runtime remains reserved for the final device pass.
