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
Do not build, install, or repeatedly test the APK after each feature. Complete source review, deterministic JVM tests, CI, review, and documentation first. Reserve Android build/install/runtime verification for one consolidated final device-validation phase; collect runtime regressions, fix them together, and rerun final validation only as needed.

## Tooling policy
- GitHub is the operational source of truth.
- Apply YAGNI, minimal scope, deterministic tests, and evidence-based claims.
- CodeRabbit is used only when its required local CLI/repository surface is actually available; no CodeRabbit result is claimed here.
- WebLibre is a separate project and only a feature/design source pool.
- Do not introduce architecture, dependencies, or subsystem replacements without demonstrated need.

## Current repository state
- Remote `genspark-dev` HEAD verified directly from GitHub: `7cc68ab9eae51faea830f94bd9381fdc880b68e4`.
- P2.1–P2.10 are CI-VERIFIED.
- P2.9 Geolocation CI evidence: Unit Tests run `33684710168`, success, head `e48c1f036aa4c7fcaea7339735c7fe81201c5d9d`.
- P2.10 Save-Data CI evidence: Unit Tests run `33686256788`, success, head `1a71cd2eb358bfd57f3209d141fc40253183dff1`.
- P2.11 Global settings search CI evidence: Unit Tests run `33688160810`, success, head `7cc68ab9eae51faea830f94bd9381fdc880b68e4`.
- Android runtime verification remains intentionally deferred to the final device pass.

## Completed engineering
1. Build/toolchain recovery: Gradle 5.4.1 / AGP 3.5.2, compile SDK 29, build-tools 28.0.3, JDK 11 for Gradle, JDK 17 for CI SDK tooling.
2. Minimal JUnit4 harness and CI workflow recovery.
3. P1 profile/identity groundwork and lifecycle/test-seam review; full profile storage isolation remains unimplemented.
4. P2.1 tracking/query-parameter cleanup — CI-VERIFIED.
5. P2.2 HTTPS-only navigation — CI-VERIFIED.
6. P2.3 Global Privacy Control — CI-VERIFIED.
7. P2.4 Desktop Mode — CI-VERIFIED.
8. P2.5 Screenshot Protection — CI-VERIFIED.
9. P2.6 built-in search bang routing — CI-VERIFIED.
10. P2.7 bounded WebView media permission privacy guard — CI-VERIFIED by run `33679583870`.
11. P2.8 optional third-party cookie blocking — CI-VERIFIED by run `33679583870`.
12. P2.9 geolocation privacy guard — CI-VERIFIED by run `33684710168`.
13. P2.10 Save-Data preference contract/fallback correction — CI-VERIFIED by run `33686256788`.
14. P2.11 global settings search — source-verified implementation with deterministic JVM contract; CI-VERIFIED by run `33688160810`.

## P2.11 — Global settings search
`Fragment_settings` adds a search field above the existing `PreferenceFragmentCompat` list and recursively filters preferences by title/summary using `Preference#setVisible`. `SettingsSearchPolicy` supplies the dependency-free deterministic matching contract with JVM tests. Existing preference actions and stored values are not replaced. SOURCE-VERIFIED: complete. TEST-VERIFIED: source test committed; local execution unavailable in this tool surface. CI-VERIFIED: complete via Unit Tests run `33688160810` at HEAD `7cc68ab9eae51faea830f94bd9381fdc880b68e4`.

## Existing-feature corrections
- Clear-on-exit is already implemented; do not reimplement.
- AMOLED/pure-black support is already implemented; do not reimplement.
- Desktop Mode is already implemented; do not reselect it.

## Architecture boundary
No multi-process profile isolation, WebView data-directory switching, extension runtime, proxy/Tor stack, DNS-over-HTTPS stack, broad anti-fingerprinting subsystem, or on-device AI runtime has been introduced.

## Reader Mode decision
Reader Mode was source-traced against the current native WebView architecture. No bounded dependency-free `evaluateJavascript`/reader-extraction seam was established; ad-hoc HTML/JS extraction would be invasive and insufficiently deterministic. Reader Mode is therefore NOT TARGETED in the current P2 cycle.

## Next execution step
**Source-verify the next smallest high-value bounded privacy/UX seam from `docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md`. Prefer dependency-free deterministic JVM coverage. Do not install the APK.**

## Last updated
2026-09-03 — P2.11 CI success reconciled and checkpoint advanced.
