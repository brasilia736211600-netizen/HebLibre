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
- Exact current branch HEAD verified remotely: `b5473771a71cb9cd23a46348953bace140dab295`.
- Previous documentation reconciliation commit: `957f3fc3c7cd49702cac50fad056426d6c6d1e29`.
- Engineering HEAD for P1 Step 6 remains `37f0e8d0232ee4e80da74ce66adade7d6917668c`.
- GitHub Actions run `33643362798` for that engineering HEAD completed successfully.
- The run's `test` job completed successfully, including `Run unit tests`.
- `docs/HEBLIBRE_MASTER_PROJECT_MAP.md`, this state file, and `docs/HEBLIBRE_RESUME_COMMAND.md` are the canonical continuity set.
- Genspark credits are exhausted; all work continues through available GitHub/local capabilities.

## Completed engineering
1. Build/toolchain recovery: Gradle 5.4.1 / AGP 3.5.2 baseline recovered with JDK 11, compile SDK 29, build-tools 28.0.3; debug build verified locally. Baseline `e11562fd42639d5b12e5468c14d461523aa96529`.
2. Minimal JUnit4 harness: `BrowserUnit.isURL` characterization; 4 tests green. `6211fc57cc5589f24106df0c1aa2a4e7b6ae2058`.
3. CI workflow: JDK 17 for SDK tooling, JDK 11 for Gradle. Successful run `33459477325` for `9aca596d6361104d7982563c3784512caab98cfa`.
4. P1 Step 1: pure-Java `UrlMatcher.containsAnyDomain`; 9/9 tests.
5. P1 Step 2: `BrowserContainer` instance-scoped; 11/11 tests.
6. P1 Step 3: `ProfileScopedWhitelist` keyed by profile id; 14/14 tests.
7. P1 Step 4: `Ninja4.db` whitelist `PROFILE_ID` migration v4→v5, profile-aware CRUD and callers; 17/17 JVM tests plus main-source compilation. Implementation `4f0f97b955ce1e8b3dd039b365f056e48b4c00de`.
8. P1 Step 5: dependency-free `ProfileIdentity` contract with four JVM tests; CI-VERIFIED by run `33641035094` on `c4b6b3a244ae9f036ce3869306d391635085640d`.
9. P1 Step 6: active profile id setting, active-profile binding for the four whitelist classes, and restart marker when profile id changes; CI-VERIFIED by run `33643362798` on engineering HEAD `37f0e8d0232ee4e80da74ce66adade7d6917668c`.

## P1 Step 6 — active profile selection
Implemented with the existing `PreferenceFragmentCompat`/`SharedPreferences` architecture:
- `current_profile_id` `EditTextPreference`, default `default`.
- Base strings explicitly state the profile id affects the profile-aware whitelist layer and does not isolate cookies/WebView storage.
- One-argument `AdBlock`, `Javascript`, `Cookie`, and `Remote` constructors read and normalize `ProfileIdentity.PREFERENCE_KEY` from default SharedPreferences; two-argument constructors use the same normalization contract.
- `Fragment_settings.onSharedPreferenceChanged()` sends profile changes through the existing `restart_changed` path so newly constructed per-tab whitelist instances use the new profile id.

### Verification status
- SOURCE-VERIFIED: Step 6 source inspected; change set is narrow and limited to the profile setting resource/strings, four whitelist classes, and the existing settings fragment.
- TEST-VERIFIED: CI run `33643362798` completed its `Run unit tests` step successfully for engineering HEAD `37f0e8d0232ee4e80da74ce66adade7d6917668c`.
- CI-VERIFIED: run `33643362798`, job `test`, conclusion `success`; all listed workflow steps completed successfully.
- ANDROID-RUNTIME-VERIFIED: not performed.
- RED→GREEN TDD: not claimed for Step 6 because no pre-change failing execution was captured before the multi-file wiring.

## Architecture boundary
The selected profile id controls the profile-aware whitelist persistence/in-memory layers for newly constructed whitelist instances and after the existing restart path. It does not isolate the process-wide `CookieManager`, Chromium WebView disk storage, SharedPreferences as a whole, history, or bookmarks. No full browser-storage identity isolation is claimed.

No multi-process architecture or `WebView.setDataDirectorySuffix` solution has been introduced. No account system, fingerprinting controls, proxy/WebRTC/UA spoofing, or unrelated dependency/refactor work is included.

## Current single next execution step
**P1 Step 7 — inspect the current browser/profile lifecycle for the smallest deterministic integration-test seam around active-profile switching; add only a focused dependency-free test if such a seam already exists. Otherwise document the runtime boundary and do not expand architecture.**

## Save-state contract
After every substantive step: verify branch + exact HEAD; record tests, CI and runtime evidence; record diff scope; update this file; commit/push; verify remote HEAD; record exactly ONE next execution step.

## Last updated
2026-09-02 — P1 Step 6 CI completed successfully; master map reconciled; remote branch HEAD verified as `b5473771...`; Android runtime verification remains outstanding.
