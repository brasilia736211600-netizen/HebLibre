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
- Current engineering HEAD before this documentation commit: `37f0e8d0232ee4e80da74ce66adade7d6917668c`.
- GitHub Actions run `33643362798` is currently in progress for that engineering HEAD; no CI-VERIFIED claim is made for P1 Step 6 until it completes.
- This state update is documentation-only and must not be confused with the engineering HEAD.
- `docs/HEBLIBRE_MASTER_PROJECT_MAP.md`, this state file, and `docs/HEBLIBRE_RESUME_COMMAND.md` are the canonical continuity set.
- Genspark credits are exhausted; all work continues through available GitHub/local capabilities.

## Completed engineering
1. Build/toolchain recovery: Gradle 5.4.1 / AGP 3.5.2 baseline recovered with JDK 11, compile SDK 29, build-tools 28.0.3; debug build verified locally. Baseline `e11562fd42639d5b12e5468c14d461523aa96529`.
2. Minimal JUnit4 harness: `BrowserUnit.isURL` characterization; 4 tests green. `6211fc57cc5589f24106df0c1aa2a4e7b6ae2058`.
3. CI workflow: JDK 17 for SDK tooling, JDK 11 for Gradle. Known successful run `33459477325` for `9aca596d6361104d7982563c3784512caab98cfa`.
4. P1 Step 1: pure-Java `UrlMatcher.containsAnyDomain`; 9/9 tests.
5. P1 Step 2: instance-scoped `BrowserContainer`; 11/11 tests.
6. P1 Step 3: `ProfileScopedWhitelist` keyed by profile id; 14/14 tests.
7. P1 Step 4: `Ninja4.db` whitelist `PROFILE_ID` migration v4→v5, profile-aware CRUD and callers; 17/17 JVM tests plus main-source compilation. Implementation `4f0f97b955ce1e8b3dd039b365f056e48b4c00de`.
8. P1 Step 5: dependency-free `ProfileIdentity` contract with four JVM tests; CI-VERIFIED by run `33641035094` on `c4b6b3a244ae9f036ce3869306d391635085640d`.
9. P1 Step 6 implementation: user-addressable profile id setting, active-profile binding for the four whitelist classes, and restart marker when profile id changes.

## P1 Step 6 — active profile selection
Implemented the smallest user-facing profile selection surface using the existing `PreferenceFragmentCompat`/`SharedPreferences` architecture:
- Added `current_profile_id` `EditTextPreference` to `preference_setting.xml`, defaulting to `default`.
- Added localized base strings for the preference title and an explicit limitation statement: this profile id controls the profile-aware whitelist layer but does not isolate cookies/WebView storage.
- Changed the one-argument constructors of `AdBlock`, `Javascript`, `Cookie`, and `Remote` to read and normalize `ProfileIdentity.PREFERENCE_KEY` from the existing default SharedPreferences store. Their two-argument constructors now use the same normalization contract.
- Changed `Fragment_settings.onSharedPreferenceChanged()` so changing the active profile id follows the existing `restart_changed` path, preventing the app from silently continuing with old per-tab whitelist instances after a profile switch.

### Verification status
- SOURCE-VERIFIED: the Step 6 source changes were inspected directly from GitHub; the commits are narrow and touch only the profile-setting resource/strings, four whitelist classes, and the existing settings fragment.
- CI-VERIFIED: pending. Run `33643362798` targets the final engineering HEAD `37f0e8d0232ee4e80da74ce66adade7d6917668c` and is still running at save time.
- TEST-VERIFIED: pending from a completed execution report for the final Step 6 head. The repository CI workflow contains the unit-test step, but this session does not claim success until the run concludes.
- ANDROID-RUNTIME-VERIFIED: not performed.
- RED→GREEN TDD: not claimed for Step 6 because no pre-change failing execution was captured before the multi-file wiring.

## Architecture boundary
The selected profile id now controls the profile-aware whitelist persistence/in-memory layers for newly constructed whitelist instances and after the existing restart path. It does not isolate the process-wide CookieManager, Chromium WebView disk storage, SharedPreferences as a whole, history, or bookmarks. No full browser-storage identity isolation is claimed.

No multi-process architecture or `WebView.setDataDirectorySuffix` solution has been introduced. No account system, fingerprinting controls, proxy/WebRTC/UA spoofing, or unrelated dependency/refactor work is included.

## Current single next execution step
**P1 Step 6A — wait for and record the CI result for engineering HEAD `37f0e8d0232ee4e80da74ce66adade7d6917668c`.** If green, inspect the completed run's unit-test step and close Step 6 as CI-VERIFIED; if red, inspect the failing job/logs and fix only the demonstrated regression.

## Save-state contract
After every substantive step: verify branch + exact HEAD; record tests, CI and runtime evidence; record diff scope; update this file; commit/push; verify remote HEAD; record exactly ONE next execution step.

## Last updated
2026-09-02 — P1 Step 6 implementation completed and saved; CI run `33643362798` for final engineering HEAD `37f0e8d...` remains pending.
