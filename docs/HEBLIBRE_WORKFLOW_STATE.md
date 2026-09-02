# HebLibre Workflow State

## Canonical continuity
GitHub is the source of truth. Chat history, agent memory, and unstated local state are non-authoritative.
Repository: `brasilia736211600-netizen/HebLibre`
Branch: `genspark-dev`
Default branch: `l10n_crowdin`

Execution protocol:
`READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → COMMIT → SAVE STATE`

Verification levels: SOURCE-VERIFIED, TEST-VERIFIED, CI-VERIFIED, ANDROID-RUNTIME-VERIFIED, DOCUMENTED. Never conflate them.

## Current repository state
- Current HEAD: `68bf3fdc7ab6f2594ada0dfcce656c33a7b301e2`.
- Continuity docs were reconciled before P1 Step 5 implementation began.
- `docs/HEBLIBRE_MASTER_PROJECT_MAP.md`, this state file, and `docs/HEBLIBRE_RESUME_COMMAND.md` are the canonical continuity set.
- Genspark credits are exhausted; all current work is performed through available GitHub/local capabilities.
- CI read/check access remains limited; no CI-VERIFIED claim is made without direct evidence.

## Completed engineering
1. Build/toolchain recovery: Gradle 5.4.1 / AGP 3.5.2 baseline recovered with JDK 11, compile SDK 29, build-tools 28.0.3; debug build verified locally. Baseline `e11562fd42639d5b12e5468c14d461523aa96529`.
2. Minimal JUnit4 harness: `BrowserUnit.isURL` characterization; 4 tests green. `6211fc57cc5589f24106df0c1aa2a4e7b6ae2058`.
3. CI workflow: JDK 17 for SDK tooling, JDK 11 for Gradle; known successful run `33459477325` for `9aca596d6361104d7982563c3784512caab98cfa`.
4. P1 Step 1: pure-Java `UrlMatcher.containsAnyDomain`; 9/9 tests.
5. P1 Step 2: instance-scoped `BrowserContainer`; 11/11 tests.
6. P1 Step 3: `ProfileScopedWhitelist` keyed by profile id; 14/14 tests.
7. P1 Step 4: `Ninja4.db` whitelist `PROFILE_ID` migration v4→v5, profile-aware CRUD and callers; 17/17 JVM tests plus main-source compilation. Implementation `4f0f97b955ce1e8b3dd039b365f056e48b4c00de`.
8. Continuity recovery: master map/resume/state documents created and reconciled for operation without Genspark.

## P1 Step 5 — identity contract
Added `ProfileIdentity` as a pure-Java identity contract:
- `PREFERENCE_KEY = "current_profile_id"`.
- `DEFAULT_PROFILE_ID` reuses `RecordUnit.DEFAULT_PROFILE_ID`.
- `normalize()` maps null/blank ids to the default and trims surrounding whitespace.

Added `ProfileIdentityTest` with four deterministic JVM tests covering null, blank, trimmed, and non-blank ids.

### Verification status
SOURCE-VERIFIED by direct source inspection. TEST-VERIFIED is pending because the available session has no checked-out project/toolchain capable of running Gradle. A pre-change RED run was not captured, so this must not be reported as RED→GREEN TDD verification.

### Why this seam exists
It gives the eventual selector one stable, dependency-free normalization/storage key contract. It intentionally does not pretend to isolate cookies, WebView disk data, history, bookmarks, or SharedPreferences.

## Architecture boundary
Current production UI is not yet wired to `ProfileIdentity`; existing production constructors still use `default`. SharedPreferences, CookieManager, Chromium WebView disk storage, history, and bookmarks remain global/unpartitioned. No full browser-storage identity isolation is claimed.

No multi-process architecture or `WebView.setDataDirectorySuffix` solution has been introduced. Do not add these, account systems, fingerprinting controls, proxy/WebRTC/UA spoofing, or unrelated dependency/refactors without demonstrated need (YAGNI).

## Current single next execution step
**P1 Step 5A — verify the new identity contract through the project's actual JVM/CI test path.** Run `:app:testDebugUnitTest` against commit `68bf3fdc7ab6f2594ada0dfcce656c33a7b301e2`, inspect the diff for only `ProfileIdentity` + test + continuity updates, and record the exact result. Only after a green test result should UI preference wiring be started.

## Save-state contract
After every substantive step: verify branch + exact HEAD; record tests, CI and runtime evidence; record diff scope; update this file; commit/push; verify remote HEAD; record exactly ONE next execution step.

## Last updated
2026-09-02 — P1 Step 5 identity contract and tests added; actual test execution remains pending.
