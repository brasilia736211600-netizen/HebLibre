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
- Exact current branch HEAD verified before this save-state update: `bbbe60ef2850f3098e8d2fbcd0ec4306c0939a37`.
- The previous engineering continuity HEAD was `93087837a331ab99af121c61273d9df9587698a4`.
- `docs/HEBLIBRE_MASTER_PROJECT_MAP.md`, this state file, `docs/HEBLIBRE_RESUME_COMMAND.md`, and `docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md` are the canonical continuity set.
- Genspark credits are exhausted; all work continues through available GitHub/local capabilities.

## Completed engineering
1. Build/toolchain recovery: Gradle 5.4.1 / AGP 3.5.2 baseline recovered with JDK 11, compile SDK 29, build-tools 28.0.3; debug build verified locally. Baseline `e11562fd42639d5b12e5468c14d461523aa96529`.
2. Minimal JUnit4 harness: `BrowserUnit.isURL` characterization; 4 tests green. `6211fc57cc5589f24106df0c1aa2a4e7b6ae2058`.
3. CI workflow: JDK 17 for SDK tooling, JDK 11 for Gradle. Successful run `33459477325` for `9aca596d6361104d7982563c3784512caab98cfa`.
4. P1 Step 1: pure-Java `UrlMatcher.containsAnyDomain`; 9/9 tests.
5. P1 Step 2: `BrowserContainer` instance-scoped; 11/11 tests.
6. P1 Step 3: `ProfileScopedWhitelist` keyed by profile id; 14/14 tests.
7. P1 Step 4: `Ninja4.db` whitelist `PROFILE_ID` migration v4→v5, profile-aware CRUD and callers; 17/17 JVM tests plus main-source compilation. Implementation `4f0f97b955ce1e8b3dd039b365f056e48b4c00de`.
8. P1 Step 5: dependency-free `ProfileIdentity` contract with four JVM tests; CI-VERIFIED by run `33641035094`.
9. P1 Step 6: active profile id setting, active-profile binding for the four whitelist classes, and restart marker when profile id changes; CI-VERIFIED by run `33643362798` on engineering HEAD `37f0e8d0232ee4e80da74ce66adade7d6917668c`.
10. P1 Step 7: browser/profile lifecycle and JVM test tree inspected. No deterministic dependency-free seam exists for end-to-end Settings → restart → WebView/profile construction, so no synthetic test or new test framework was added.
11. Continuity reconciliation: runtime verification was explicitly moved to DEFERRED status and removed as a development blocker.
12. P1 Step 8A: built and persisted a source-based HebLibre ↔ WebLibre feature gap matrix. It explicitly distinguishes HebLibre functionality that already exists from genuinely missing/partial features and records the YAGNI/architectural boundaries. The matrix selects conservative tracking/query-parameter cleanup as the next implementation candidate.

## P1 Step 7 — lifecycle/test-seam review
### Result
SOURCE-VERIFIED: current `BrowserActivity`, `Settings_Activity`, `Fragment_settings`, `NinjaWebView`, and JVM test structure were reviewed for a deterministic dependency-free integration seam.

No existing seam allows a plain JVM test to prove the complete Android sequence:
`Settings current_profile_id → SharedPreferences → restart marker/activity restart → new WebView/whitelist construction`.

Adding Robolectric, instrumentation tests, a fake Activity stack, or emulator-only infrastructure solely for this proof would expand architecture/testing scope without a demonstrated requirement. No such change was made.

### Verification status
- SOURCE-VERIFIED: complete.
- TEST-VERIFIED: no new Step 7 test was appropriate because no dependency-free seam exists.
- CI-VERIFIED: existing Step 6 CI evidence remains valid for the production wiring; no new production code was added by Step 7.
- ANDROID-RUNTIME-VERIFIED: not performed.
- DOCUMENTED: complete in this state file and the master map.

## P1 Step 8 — Android runtime verification
Status: **DEFERRED**.

Purpose when an Android runtime is available: execute one focused test of active-profile switching and confirm that a newly constructed whitelist instance uses the selected normalized profile id after the existing restart path.

This is a validation task, not a prerequisite for continued engineering work. No emulator/instrumentation framework is being introduced solely to close this item. No failure is inferred from the absence of runtime evidence.

## P1 Step 8A — gap analysis
Status: **COMPLETED / DOCUMENTED**.

The feature gap matrix was added as:
`docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md`

It records:
- HebLibre functionality already present before WebLibre feature-pool work;
- partial capabilities that should not be mistaken for complete parity;
- easy/medium/architectural candidates;
- explicit YAGNI exclusions;
- the first implementation candidate: conservative tracking/query-parameter cleanup.

This step is documentation/source analysis, not Android-runtime verification.

## Architecture boundary
The selected profile id controls the profile-aware whitelist persistence/in-memory layers for newly constructed whitelist instances and after the existing restart path. It does not isolate the process-wide `CookieManager`, Chromium WebView disk storage, SharedPreferences as a whole, history, or bookmarks. No full browser-storage identity isolation is claimed.

No multi-process architecture or `WebView.setDataDirectorySuffix` solution has been introduced. No account system, fingerprinting controls, proxy/WebRTC/UA spoofing, or unrelated dependency/refactor work is included.

## Next execution step
**P2 Step 1 — implement the conservative tracking/query-parameter cleanup selected by the gap matrix: first add a pure-Java URL-cleaner contract plus focused JVM tests, then wire it into the existing `BrowserUnit.queryWrapper()` navigation path without changing unrelated behavior.**

## Save-state contract
After every substantive step: verify branch + exact HEAD; record tests, CI and runtime evidence; record diff scope; update this file; commit/push; verify remote HEAD; record exactly ONE next execution step.

## Last updated
2026-09-02 — verified current branch HEAD `bbbe60ef2850f3098e8d2fbcd0ec4306c0939a37`; completed and persisted the HebLibre/WebLibre gap matrix; selected conservative tracking/query cleanup as the next implementation step.
