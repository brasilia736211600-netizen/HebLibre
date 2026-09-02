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
- CI workflow recovery: complete for the previously verified CI commit; later CI status may be unavailable through the current GitHub App scope.
- P1 Step 1: extracted shared pure-Java `UrlMatcher.containsAnyDomain` and added tests.
- P1 Step 2: made `BrowserContainer` instance-scoped and added isolation tests.
- P1 Step 3: added `ProfileScopedWhitelist` keyed by profile id and tests; existing default behavior preserved.
- P1 Step 4: migrated the four whitelist SQLite tables to `PROFILE_ID`, profile-scoped CRUD, and version 4→5 migration; full JVM suite reached 17/17 and main Java compilation passed.
- Continuity recovery: created and synchronized the canonical master map, workflow state, and resume command for operation without Genspark.

## Current architecture boundary
### Already profile-capable
- In-memory whitelist state: `ProfileScopedWhitelist`.
- Persisted whitelist tables: `WHITELIST`, `JAVASCRIPT`, `COOKIE`, `REMOTE` with `PROFILE_ID`.
- `AdBlock`, `Javascript`, `Cookie`, `Remote` accept an explicit profile id.

### Still process/global scoped
- No production source currently selects a non-default profile id.
- `BrowserActivity` is the process-lifetime root.
- `NinjaWebView` is the per-tab WebView construction site.
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
- unrelated refactors or dependency upgrades.

## Current phase
`P1 — Profile / Identity Isolation — implementation`

P1 Steps 1–4 are complete. The persistence and in-memory whitelist boundaries exist, but there is no user-facing profile selection yet.

## Single next execution target
**P1 Step 5 — verify whether an immediately useful profile-selection surface exists in the current UI.** Inspect the existing overflow/bottom-sheet menu and preference conventions first. Only implement a selector when it can produce observable behavior immediately by choosing the profile id used by the existing whitelist infrastructure. Do not imply that this selects a separate cookie/WebView-storage identity.

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
2026-09-02 — synchronized with the reconciled workflow state; branch currently contains documentation-only continuity commits after engineering HEAD `4f0f97b...`.
