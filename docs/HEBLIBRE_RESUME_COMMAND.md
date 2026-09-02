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
- P2.1–P2.11 are CI-VERIFIED.
- P2.9 Geolocation: Unit Tests run `33684710168` success, head `e48c1f036aa4c7fcaea7339735c7fe81201c5d9d`.
- P2.10 Save-Data: Unit Tests run `33686256788` success, head `1a71cd2eb358bfd57f3209d141fc40253183dff1`.
- P2.11 Global settings search: Unit Tests run `33688160810` success, head `7cc68ab9eae51faea830f94bd9381fdc880b68e4`.
- Tab reorder core: `TabOrderPolicy` + `BrowserContainer.move()` committed; deterministic JUnit source test committed; CI still pending.
- QR scanner, PWA, true tab hierarchy, and true multi-window are deferred because they require larger camera/lifecycle/model architecture.
- Android runtime remains deferred to final consolidated device validation.
- Reader Mode remains NOT TARGETED for the current P2 cycle.

Next execution:
Source-verify the smallest bounded download privacy/control seam starting from `BrowserUnit.download()` cookie forwarding. Do not duplicate download logic or change authenticated-download behavior by default. Add a deterministic policy test first; integrate only if the existing call structure permits a minimal, correct change. Do not install the APK.
```

## Current authoritative checkpoint
P2.1–P2.11 are CI-VERIFIED. Tab reorder has a deterministic core slice but is not yet UI-complete or CI-verified. QR/PWA/hierarchy/multi-window remain deferred medium/architectural seams. The next source-verification target is bounded download privacy/control work.
