# HebLibre Master Project Map

## Authority
- Repository: `brasilia736211600-netizen/HebLibre`
- Active development branch: `genspark-dev`
- Default branch: `l10n_crowdin`
- GitHub is the source of truth; chat history and agent memory are non-authoritative.
- Canonical execution protocol: `READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → COMMIT → SAVE STATE`.
- Canonical handoff state: `docs/HEBLIBRE_WORKFLOW_STATE.md`.
- Canonical resume command: `docs/HEBLIBRE_RESUME_COMMAND.md`.

## Verification ladder
Never collapse these levels:
1. SOURCE-VERIFIED
2. TEST-VERIFIED
3. CI-VERIFIED
4. ANDROID-RUNTIME-VERIFIED
5. DOCUMENTED

## Project baseline
Legacy Android browser/WebView application based on the FOSS Browser codebase. Current toolchain baseline uses Gradle 5.4.1 / AGP 3.5.2, compile SDK 29, build-tools 28.0.3, and JDK 11 for Gradle. JDK 17 is used for Android SDK tooling in CI.

## Completed engineering sequence
- Build/toolchain recovery: complete; debug build verified locally.
- Minimal JUnit4 harness: complete; `BrowserUnit.isURL` characterization tests.
- CI workflow recovery: complete; JDK 17 is used for SDK tooling and JDK 11 for Gradle.
- P1 Step 1: extracted shared pure-Java `UrlMatcher.containsAnyDomain` and added tests.
- P1 Step 2: made `BrowserContainer` instance-scoped and added isolation tests.
- P1 Step 3: added `ProfileScopedWhitelist` keyed by profile id and tests; existing default behavior preserved.
- P1 Step 4: migrated the four whitelist SQLite tables to `PROFILE_ID`, profile-scoped CRUD, and version 4→5 migration; full JVM suite reached 17/17 and main Java compilation passed.
- P1 Step 5: added dependency-free `ProfileIdentity` normalization contract and tests; CI-VERIFIED by run `33641035094`.
- P1 Step 6: added the user-facing active profile id preference, bound all four whitelist classes to the selected normalized profile id, and wired profile changes into the existing restart marker path; CI-VERIFIED by run `33643362798`.
- P1 Step 7: inspected the browser/profile lifecycle and test tree. No deterministic dependency-free seam exists for end-to-end Settings → restart → WebView/profile construction, so no synthetic test or new test framework was added. This is SOURCE-VERIFIED and the runtime boundary is documented.
- Continuity reconciliation: runtime verification is explicitly deferred rather than treated as a development blocker; resume documentation was corrected.

## Current architecture boundary
### Already profile-capable
- In-memory whitelist state: `ProfileScopedWhitelist`.
- Persisted whitelist tables: `WHITELIST`, `JAVASCRIPT`, `COOKIE`, `REMOTE` with `PROFILE_ID`.
- `AdBlock`, `Javascript`, `Cookie`, `Remote` accept and normalize an explicit profile id.
- Production one-argument whitelist construction reads `ProfileIdentity.PREFERENCE_KEY` from the existing default SharedPreferences store.
- Settings expose `current_profile_id` as an `EditTextPreference` with default `default`.
- Changing the active profile id follows the existing `restart_changed` path.

### Still process/global scoped
- `BrowserActivity` is the process-lifetime root.
- `SharedPreferences` remain one default preference store.
- `CookieManager` remains process-wide.
- Chromium WebView disk storage remains shared; no `WebView.setDataDirectorySuffix` usage exists.
- History/bookmarks remain unpartitioned.
- No multi-process profile architecture is implemented.

## Explicit YAGNI boundaries
Do not add without demonstrated product need:
- multi-process architecture;
- WebView data-directory switching;
- cookie/DOM-storage isolation mechanisms beyond what the current Android architecture can support safely;
- account/user systems;
- fingerprinting changes;
- proxy/WebRTC/UA-spoofing work;
- unrelated refactors or dependency upgrades;
- emulator/instrumentation infrastructure solely to close the deferred runtime verification item.

## Verification status
- SOURCE-VERIFIED: P1 Steps 1–7 and the corresponding source/lifecycle review are documented.
- TEST-VERIFIED: the deterministic JVM suite is covered by the recorded successful CI unit-test executions.
- CI-VERIFIED: P1 Step 6 was verified by run `33643362798`, job `test`, conclusion `success`, on engineering HEAD `37f0e8d0232ee4e80da74ce66adade7d6917668c`.
- ANDROID-RUNTIME-VERIFIED: not performed for active-profile switching; this is explicitly DEFERRED because no Android runtime is currently available.
- DOCUMENTED: this map, `docs/HEBLIBRE_WORKFLOW_STATE.md`, and `docs/HEBLIBRE_RESUME_COMMAND.md` form the continuity set.

## Current phase
`P1 — Profile / Identity Isolation — implementation boundary reached`

P1 Steps 1–7 are complete to their stated verification boundaries. The active profile selector is implemented and CI-verified. Android runtime verification is deferred and does not block further evidence-backed engineering.

## Single next execution target
**P1 Step 8A — continue with the smallest evidence-backed engineering task that does not depend on Android runtime; do not invent a new profile-isolation architecture. When an Android runtime becomes available, perform the focused deferred profile-switching runtime verification as a validation task, not as a prerequisite for unrelated work.**

## Continuity requirements
Every substantive change must update `docs/HEBLIBRE_WORKFLOW_STATE.md` with:
- exact branch and HEAD;
- what changed;
- tests and their exact result;
- CI evidence or explicit inability to verify;
- Android runtime evidence or explicit non-verification;
- diff scope;
- one and only one next execution step.

Every new agent must read the state file and this map before acting. If they conflict with live GitHub, reconcile them before implementation.

## Credit-independent operation
Genspark credits are exhausted. The project must continue using available GitHub/local capabilities. Do not wait for Genspark, do not claim work was performed by Genspark, and do not spend effort reproducing work already verified in GitHub.

## Last synchronized
2026-09-02 — reconciled P1 Step 7 boundary, explicitly deferred Android runtime verification, and corrected the resume target.
