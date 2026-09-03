# HebLibre Workflow State

## Canonical continuity
GitHub is the source of truth. Chat history, agent memory, and unstated local state are non-authoritative.
Repository: `brasilia736211600-netizen/HebLibre`
Branch: `genspark-dev`
Default branch: `l10n_crowdin`

Execution protocol:
`READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → REVIEW → COMMIT → SAVE STATE`

Verification levels: SOURCE-VERIFIED, TEST-VERIFIED, CI-VERIFIED, ANDROID-RUNTIME-VERIFIED, DOCUMENTED. Never conflate them.

## Continuous autonomous-work rule
When the user says `استمر` / `continue`, keep working internally for as long as useful and do not interrupt the workstream with routine progress messages. Investigate, verify, diagnose, prioritize, implement, test, review, reconcile CI, and fix discovered problems. Use parallel work only for genuinely independent units; serialize dependent branch mutations. Before a routine user-facing update, complete productive work when tool/runtime conditions permit. Do not stop after one tiny feature or one CI submission while useful work remains.

## Final Android validation rule
Do not build, install, or repeatedly test the APK after each feature. Complete source review, deterministic JVM tests, CI, review, and documentation first. Reserve Android runtime verification for one consolidated final device-validation phase; collect runtime regressions, fix them together, and rerun final device validation only as needed.

## Tooling policy
- GitHub is the operational source of truth.
- Apply YAGNI, minimal scope, deterministic tests, and evidence-based claims.
- CodeRabbit is used only when its required local CLI/repository surface is actually available; no CodeRabbit result is claimed here.
- WebLibre is a separate project and only a feature/design source pool.
- Do not introduce architecture, dependencies, or subsystem replacements without demonstrated need.

## Current repository state
- Active branch `genspark-dev` is synchronized to the clean verified checkpoint `5fffd65e80e2a616a5273befe7cdca6309441490`.
- P2.1–P2.11 remain CI-VERIFIED from their recorded successful runs.
- Download-cookie integration CI run `33692045747`: completed success.
- BrowserContainer reorder integration-test CI run `33692092276`: completed success.
- Remote-content default consistency correction CI run `33694722442`: completed success.
- Android runtime verification remains intentionally deferred to the final device pass.

## Engineering checkpoint
The verified feature set is complete through P2.11 plus download-cookie privacy, tab reorder core, and remote-content default consistency. No incomplete reorder-controller API remains. The tab overview still has a flat `LinearLayout` item path; `AlbumItem` uses normal click for selection and long-click for close. Tab reorder UI therefore remains PARTIAL until a complete, non-breaking source mutation is ready.

## Deferred work
QR scanner, PWA, true tab hierarchy, multi-window, broader tracking protection, DoH, broad fingerprinting defenses, full WebRTC privacy, complete profile storage isolation, isolated tabs, per-container proxy/Tor, extensions/uBlock, on-device AI, and Reader Mode remain deferred by YAGNI/architecture boundaries.

## Next execution step
Continue parallel source verification on independent bounded privacy/UX seams. Keep tab reorder UI deferred until a complete mutation path can be edited safely. Preserve long-click close behavior. Prefer deterministic JVM coverage and CI. Do not install the APK yet.

## Last updated
2026-09-03 — restored the branch/documentation checkpoint to the last clean verified state after rejecting an unverified third-party-cookie default change.
