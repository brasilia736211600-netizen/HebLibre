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
- Current HEAD: `c4b6b3a244ae9f036ce3869306d391635085640d`.
- Branch is live and verified directly from GitHub.
- `docs/HEBLIBRE_MASTER_PROJECT_MAP.md`, this state file, and `docs/HEBLIBRE_RESUME_COMMAND.md` are the canonical continuity set.
- Genspark credits are exhausted; all work continues through available GitHub/local capabilities.
- GitHub Actions is readable through the available GitHub integration for this repository; do not fall back to the old 403 assumption when direct evidence is available.

## Completed engineering
1. Build/toolchain recovery: Gradle 5.4.1 / AGP 3.5.2 baseline recovered with JDK 11, compile SDK 29, build-tools 28.0.3; debug build verified locally. Baseline `e11562fd42639d5b12e5468c14d461523aa96529`.
2. Minimal JUnit4 harness: `BrowserUnit.isURL` characterization; 4 tests green. `6211fc57cc5589f24106df0c1aa2a4e7b6ae2058`.
3. CI workflow: JDK 17 for SDK tooling, JDK 11 for Gradle. Known successful run `33459477325` for `9aca596d6361104d7982563c3784512caab98cfa`.
4. P1 Step 1: pure-Java `UrlMatcher.containsAnyDomain`; 9/9 tests.
5. P1 Step 2: instance-scoped `BrowserContainer`; 11/11 tests.
6. P1 Step 3: `ProfileScopedWhitelist` keyed by profile id; 14/14 tests.
7. P1 Step 4: `Ninja4.db` whitelist `PROFILE_ID` migration v4→v5, profile-aware CRUD and callers; 17/17 JVM tests plus main-source compilation. Implementation `4f0f97b955ce1e8b3dd039b365f056e48b4c00de`.
8. Continuity recovery: master map/resume/state documents created and reconciled for operation without Genspark.

## P1 Step 5 — identity contract
`ProfileIdentity` is the canonical dependency-free profile-id contract:
- `PREFERENCE_KEY = "current_profile_id"`.
- `DEFAULT_PROFILE_ID` reuses `RecordUnit.DEFAULT_PROFILE_ID`.
- `normalize()` maps null/blank ids to the default and trims surrounding whitespace.

`ProfileIdentityTest` contains four deterministic JVM tests covering null, blank, trimmed, and non-blank ids.

### Verification
- SOURCE-VERIFIED: implementation and test source inspected.
- CI-VERIFIED: GitHub Actions run `33641035094` for commit `c4b6b3a244ae9f036ce3869306d391635085640d` completed with conclusion `success`. The `test` job and its `Run unit tests` step both completed successfully.
- The CI result verifies the repository test workflow executed successfully at this HEAD; no Android-runtime claim is made.
- RED→GREEN TDD history was not captured for this identity contract, so no RED→GREEN claim is made.

## Architecture boundary
Current production UI is not yet wired to `ProfileIdentity`; existing production constructors still default to the default profile. The profile-id persistence boundary exists, but there is not yet a user-facing profile selector.
SharedPreferences, CookieManager, Chromium WebView disk storage, history, and bookmarks remain global/unpartitioned. No full browser-storage identity isolation is claimed.

No multi-process architecture or `WebView.setDataDirectorySuffix` solution has been introduced. Do not add these, account systems, fingerprinting controls, proxy/WebRTC/UA spoofing, or unrelated dependencies/refactors without demonstrated need (YAGNI).

## Current single next execution step
**P1 Step 6 — implement the smallest user-facing profile selector using the existing SharedPreferences path, with a deterministic testable identity-selection seam where possible.** Reuse `ProfileIdentity.PREFERENCE_KEY`, preserve the default profile, and wire the selected id into the existing profile-aware whitelist constructors. Do not expand into cookies, WebView storage, history/bookmarks, multi-process, or unrelated settings.

## Save-state contract
After every substantive step: verify branch + exact HEAD; record tests, CI and runtime evidence; record diff scope; update this file; commit/push; verify remote HEAD; record exactly ONE next execution step.

## Last updated
2026-09-02 — verified P1 Step 5 through GitHub Actions and advanced the single next step to the minimal user-facing profile selector.
