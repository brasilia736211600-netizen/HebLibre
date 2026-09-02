# HebLibre Workflow State

## Canonical continuity contract
GitHub is the source of truth. Chat history, agent memory, and local unstated state are non-authoritative.
Repository: `brasilia736211600-netizen/HebLibre`
Branch: `genspark-dev`
Default branch: `l10n_crowdin`

Required lifecycle for every substantive step:
`READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → COMMIT → SAVE STATE`

Verification levels must remain distinct:
- SOURCE-VERIFIED
- TEST-VERIFIED
- CI-VERIFIED
- ANDROID-RUNTIME-VERIFIED
- DOCUMENTED

Rules: YAGNI; TDD whenever deterministic testing is possible; no unrelated files; do not block development on Android runtime; do not resurrect WebLibre state; do not invent architecture without evidence; when a task is long, continue independent non-conflicting work rather than waiting.

## Current repository state
- Latest verified branch: `genspark-dev`
- Latest verified HEAD before this documentation update: `2a561adb40fad1408e369f3de03b4abdd375b2a8`
- Working tree was verified clean before the documentation update.
- CI read/check API remains limited by the known GitHub App `actions/checks` permission gap; do not claim CI status unless directly verified.

## Completed work

### Baseline / build recovery
Legacy Android project recovered on JDK 11 with Gradle 5.4.1 / AGP 3.5.2, compile SDK 29 and build-tools 28.0.3. Java-8 compatibility restored and 19 preference generic-inference sites corrected. Debug APK build succeeded.
Baseline: `e11562fd42639d5b12e5468c14d461523aa96529`.
Status: BUILD-VERIFIED; not Android-runtime-verified.

### TDD harness
Added JUnit 4.12 and four deterministic `BrowserUnit.isURL` tests.
`./gradlew :app:testDebugUnitTest` passed.
Commit: `6211fc57cc5589f24106df0c1aa2a4e7b6ae2058`.
Status: TEST-VERIFIED.

### CI recovery
Workflow uses JDK 17 for Android SDK tooling and JDK 11 for legacy Gradle execution. Known successful CI run: `33459477325` for `9aca596d6361104d7982563c3784512caab98cfa`.
Status: CI-VERIFIED only for that exact commit.

### P1 Step 1 — URL matcher seam
Extracted duplicated `isWhite` matching logic from `AdBlock`, `Javascript`, `Cookie`, `Remote` into pure-Java `UrlMatcher.containsAnyDomain`.
Added `UrlMatcherTest` (5 tests). Existing tests remained green: 9/9.
Commit lineage includes `0da72a2bd6a85e9a9ffb9ce9a2ab01a49e46eced`.
Status: SOURCE-VERIFIED + TEST-VERIFIED.

### P1 Step 2 — BrowserContainer instance isolation
Removed process-wide static tab list/state from `BrowserContainer`; `BrowserActivity` owns one instance and its call sites were updated.
Added `BrowserContainerTest` (2 tests), genuine RED→GREEN. Full suite: 11/11.
Commit: `9498936c52dd6a7a614da246a8d1638c1a781d1d`.
Status: SOURCE-VERIFIED + TEST-VERIFIED.

### P1 Step 3 — In-memory profile-scoped whitelist
Added pure-Java `ProfileScopedWhitelist` registry keyed by profile id. `AdBlock`, `Javascript`, `Cookie`, `Remote` gained `(Context, String profileId)` constructors while existing constructors retain `default` behavior.
Added `ProfileScopedWhitelistTest` (3 tests). Full suite: 14/14.
Commit lineage: `e934a2d55ae56cd2607ccde2fa407c60854be052`.
Status: SOURCE-VERIFIED + TEST-VERIFIED.

### P1 Step 4 — Persisted whitelist profile_id
Migrated `Ninja4.db` version 4→5. Added `PROFILE_ID` to `WHITELIST`, `JAVASCRIPT`, `COOKIE`, `REMOTE`; added profile-aware whitelist CRUD and migration logic; threaded profile id through all relevant production callers while preserving `default` behavior.
Added `RecordUnitProfileIdTest` (3 tests), genuine RED→GREEN. Full JVM suite: 17/17; main-source compilation passed separately.
Implementation commit: `4f0f97b955ce1e8b3dd039b365f056e48b4c00de`.
Documentation state was subsequently recorded in `2a561adb40fad1408e369f3de03b4abdd375b2a8`.
Status: SOURCE-VERIFIED + TEST-VERIFIED; NOT ANDROID-RUNTIME-VERIFIED.

## Architectural boundary / non-goals
No real non-default profile is currently selected by production code. There is no profile UI/account system yet.
SharedPreferences remain unpartitioned. CookieManager and Chromium WebView disk storage remain process-wide; `WebView.setDataDirectorySuffix` and multi-process architecture are deliberately deferred pending demonstrated need. History/bookmarks are also not yet profile-partitioned.
No fingerprinting, proxy, WebRTC, UA-spoofing, or other unrelated architecture work is authorized under YAGNI.

## Important recent discovery
A read-only trace confirmed the actual profile plumbing entry point: `BrowserActivity` is the process-lifetime root, `SharedPreferences` is initialized there, and `NinjaWebView` is the per-tab construction site for the four whitelist classes. The smallest plumbing change would thread one active profile id from `BrowserActivity` into `NinjaWebView` and then into the already-capable whitelist constructors. However, doing only that would still leave the value permanently `default` and create no user-visible profile feature.

## Current single next execution step
**P1 Step 5 — implement only the smallest deterministic profile-identity plumbing seam that has demonstrable value.** First verify the live GitHub HEAD and inspect the exact current constructors/call sites. If the seam remains behaviorally a no-op, do NOT add it merely for abstraction's sake. Prefer the smallest pure-Java profile-selection/value contract that can be TDD-tested before touching Android UI. Do not start a profile UI/account system, cookie storage isolation, SharedPreferences partitioning, or multi-process work until the minimal identity contract is proven useful.

## Save-state requirement
After Step 5 (or any later substantive step): verify exact branch/HEAD, tests, CI evidence, diff scope; update this file; record exactly ONE next execution step; commit and push; verify local/remote equality.

## Last updated
2026-09-02 — continuity state reconciled against live GitHub; Genspark credits are exhausted, so work must continue with available GitHub/local capabilities without pretending Genspark execution occurred.
