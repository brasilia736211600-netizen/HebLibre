# HebLibre Master Project Map

## Authority
- Repository: `brasilia736211600-netizen/HebLibre`
- Active development branch: `genspark-dev`
- Default branch: `l10n_crowdin`
- GitHub is the source of truth; chat history and agent memory are non-authoritative.
- Canonical execution protocol: `READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → COMMIT → SAVE STATE`.
- Canonical handoff state: `docs/HEBLIBRE_WORKFLOW_STATE.md`.
- Canonical resume command: `docs/HEBLIBRE_RESUME_COMMAND.md`.
- Feature gap record: `docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md`.

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
- P1 Step 7: inspected the browser/profile lifecycle and test tree. No deterministic dependency-free seam exists for end-to-end Settings → restart → WebView/profile construction, so no synthetic test or new test framework was added.
- P1 Step 8 runtime verification: explicitly deferred because no Android runtime is available; it is not a blocker for independent engineering.
- P1 Step 8A: created `docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md` to prevent reimplementing existing HebLibre functionality and to select the smallest genuinely new feature from the WebLibre pool.

## HebLibre original baseline — already implemented
HebLibre already provides substantial browser functionality, including:
- multi-tab browsing and tab overview;
- Home, Bookmarks, History;
- search/autocomplete and configurable search engines;
- navigation/tool gestures;
- find-in-page;
- PDF/print flow;
- downloads/download handling;
- fullscreen/video-related handling;
- JavaScript, Cookie, Remote, and AdBlock controls with domain whitelists;
- Safe Browsing;
- bookmark import/export;
- custom User-Agent setting.

These capabilities are not migration targets merely because WebLibre also has them.

## Current profile boundary
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

## WebLibre feature-pool decisions
The full comparison is maintained in `docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md`.

### First selected implementation
**Conservative tracking/query-parameter cleanup**.

Reason: current navigation already centralizes user-input URL normalization in `BrowserUnit.queryWrapper()`, while source inspection shows no generic tracking-parameter filtering. A pure-Java URL cleaner can therefore be tested independently and then wired through the existing path with minimal surface-area change.

### Explicit non-goals for this step
- no full URL canonicalization;
- no arbitrary query-parameter deletion;
- no network requests;
- no new dependency;
- no navigation-policy rewrite;
- no simultaneous fingerprinting, cookie, WebRTC, proxy, DNS, or extension architecture work.

## Explicit YAGNI boundaries
Do not add without demonstrated product need:
- multi-process architecture;
- WebView data-directory switching;
- full cookie/DOM-storage isolation beyond current platform architecture;
- account/user systems;
- broad fingerprinting controls;
- proxy/Tor networking stack;
- WebRTC engine controls;
- Firefox extension runtime;
- large AI/model runtime;
- unrelated refactors or dependency upgrades;
- emulator/instrumentation infrastructure solely to close the deferred profile runtime verification item.

## Verification status
- SOURCE-VERIFIED: P1 Steps 1–8A analysis and implementation boundaries documented.
- TEST-VERIFIED: deterministic JVM suite has recorded green results through P1 Step 6.
- CI-VERIFIED: P1 Step 6 engineering HEAD `37f0e8d0232ee4e80da74ce66adade7d6917668c` passed run `33643362798`.
- ANDROID-RUNTIME-VERIFIED: active-profile switching not yet verified on device/emulator; explicitly DEFERRED.
- DOCUMENTED: master map, workflow state, resume command, and gap matrix are the canonical continuity set.

## Current phase
`P2 — WebLibre Feature Gap Implementation`

P1 profile/identity work has reached its current architecture boundary. Development now proceeds with the selected high-value, low-risk gap rather than waiting for deferred Android runtime validation.

## Single next execution target
**P2 Step 1 — add and test the pure-Java conservative URL tracking-parameter cleaner, then integrate it into the existing `BrowserUnit.queryWrapper()` URL path without changing unrelated behavior.**

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
2026-09-02 — feature gap analysis persisted; existing HebLibre capabilities explicitly separated from the WebLibre feature pool; P2 URL-cleanup implementation selected as the sole next target.
