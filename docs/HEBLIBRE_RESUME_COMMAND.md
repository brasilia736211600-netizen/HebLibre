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
- P2.9 Geolocation: Unit Tests run `33684710168` success.
- P2.10 Save-Data: Unit Tests run `33686256788` success.
- P2.11 Global settings search: Unit Tests run `33688160810` success.
- Download cookie control: `DownloadCookiePolicy` + integrated `BrowserUnit.download()` path; CI run `33692045747` success.
- Tab reorder core: `TabOrderPolicy` + `BrowserContainer.move()` + controller-identity JVM tests; CI run `33692092276` success.
- Remote-content default consistency: `sp_remote` fallback aligned across preference/navigation/init paths; CI run `33694722442` success.
- Tab reorder UI is intentionally PARTIAL: source tracing confirms `BrowserActivity` owns both the container/model and tab view container, while `AlbumItem` owns the tab item and long-click close behavior. A temporary incomplete controller seam was reverted and must not be resurrected without the full mutation path.
- QR scanner, PWA, true tab hierarchy, and true multi-window are deferred.
- Reader Mode remains NOT TARGETED for the current P2 cycle.
- Android runtime remains deferred to final consolidated device validation.

Next execution:
Continue parallel source verification on independent bounded privacy/UX seams while keeping tab reorder UI deferred until a complete non-breaking mutation path can be edited safely. Preserve long-click close behavior. Prefer deterministic JVM coverage and CI before any Android runtime work. Do not install the APK.
```

## Current authoritative checkpoint
P2.1–P2.11, download-cookie privacy, tab reorder core, and remote-content default consistency are CI-VERIFIED. Tab reorder UI remains PARTIAL because its complete BrowserActivity/AlbumItem mutation path has not yet been safely implemented. QR/PWA/hierarchy/multi-window remain deferred. Android runtime remains deferred.
