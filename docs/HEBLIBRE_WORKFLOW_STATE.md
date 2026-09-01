# HebLibre Workflow State

## Purpose
This file is the canonical handoff state for the HebLibre project.
GitHub is the source of truth. Chat history, agent memory, and local workspace state are non-authoritative.

## Current repository state
- Repository: `brasilia736211600-netizen/HebLibre`
- Active development branch: `genspark-dev`
- Current HEAD: `e934a2d55ae56cd2607ccde2fa407c60854be052`
- Default branch: `l10n_crowdin`
- Project type: Android application based on the FOSS Browser/WebView codebase
- Verified via fresh `git fetch`: local HEAD = `origin/genspark-dev` HEAD, working tree clean.
- A recurring GitHub auth failure (`Invalid username or token`) occurred again during this step and was resolved by re-invoking the GitHub setup tool, then re-verified via `git fetch` before pushing. Known, non-blocking, recurring pattern (also seen in prior steps).
- CI run status still cannot be checked via API with the current credential (`HTTP 403: Resource not accessible by integration`, missing `actions:read`/`checks:read` scope). Known limitation, not a new blocker.

## Verification ladder
Never mark a capability as complete merely because code exists.
- SOURCE-VERIFIED: implementation exists and source inspection supports the claim.
- TEST-VERIFIED: automated tests pass for the relevant contract.
- CI-VERIFIED: GitHub Actions passes for the exact commit.
- ANDROID-RUNTIME-VERIFIED: verified on an Android device/emulator.
- DOCUMENTED: recorded here and/or in the project map.

The strongest applicable status must always be stated explicitly.

## Completed baseline
### Build/toolchain recovery
- Portable Eclipse Temurin JDK 11 was used for the legacy Gradle 5.4.1 toolchain.
- Minimal Android SDK components were installed for compile SDK 29 / build-tools 28.0.3.
- `compileOptions` was restored to Java 8 compatibility.
- 19 `Objects.requireNonNull(findPreference(...))` sites were fixed with explicit `androidx.preference.Preference` typing because JDK 11 + source 8 exposed generic inference failures.
- Local debug APK build succeeded.
- Baseline build commit: `e11562fd42639d5b12e5468c14d461523aa96529`.

Status: BUILD-VERIFIED / not Android-runtime verified.

### Minimal TDD harness
- Added JUnit 4.12 test dependency.
- Added `BrowserUnitTest` with four deterministic `BrowserUnit.isURL` tests.
- `./gradlew :app:testDebugUnitTest` passed locally.
- Test harness commit: `6211fc57cc5589f24106df0c1aa2a4e7b6ae2058`.

Status: TEST-VERIFIED.

### CI
- GitHub Actions workflow uses JDK 17 for Android SDK setup and JDK 11 for the legacy Gradle build/test toolchain.
- Successful CI head before the documentation-only commits: `9aca596d6361104d7982563c3784512caab98cfa`.
- Successful workflow run: `33459477325`.

Status: CI-VERIFIED for `9aca596d6361104d7982563c3784512caab98cfa`.
- Documentation-only commits `1a930a715c17110c07b318f5e75e150959787bc0` and `19bd8a826f422f545367b2d62ebf1f99d63cdc1e` have not yet been treated as CI-verified until their branch CI result is checked.

### Runtime
- No Android device/emulator runtime verification has been completed yet.

Status: NOT VERIFIED.

## Product-development state
Product feature implementation has not yet started on this branch after the baseline/CI recovery.

### Current phase
`P1 — Profile / Identity Isolation — Implementation (OPENED, step 3 of N complete)`

### P1 step 1 (complete)
**What changed:** Extracted the identical `isWhite(String)` domain-matching loop, previously duplicated in `AdBlock`, `Javascript`, `Cookie`, `Remote`, into one pure-Java static helper `de.baumann.browser.unit.UrlMatcher.containsAnyDomain(List<String>, String)`. All four classes now delegate to it. No behavior change, no architecture change, no new dependency, no multi-process/data-directory work (explicitly out of scope, untouched).
**Tests:** New `UrlMatcherTest` (5 tests, all GREEN) characterizes current matching semantics (substring containment, null-safe, empty-list-safe) before any future per-profile refactor. Existing `BrowserUnitTest` (4 tests) still GREEN — no regression. `./gradlew :app:testDebugUnitTest` → BUILD SUCCESSFUL, 9/9 tests pass. Verified locally; Android runtime not required for this step (pure JVM logic) and was correctly not invoked.
**HEAD after this step:** `0da72a2bd6a85e9a9ffb9ce9a2ab01a49e46eced`.
**Diff scope:** 6 files — 4 modified (`AdBlock.java`, `Javascript.java`, `Cookie.java`, `Remote.java`, each: loop replaced by one delegation line), 2 new (`UrlMatcher.java`, `UrlMatcherTest.java`). No unrelated files touched.
**What was NOT done:** No per-profile storage/DB/prefs/cookie isolation yet — this step only prepared a shared seam. No multi-process or `WebView.setDataDirectorySuffix` work (explicitly excluded). No `BrowserContainer`/static-whitelist-field refactor yet.

### P1 step 2 (complete)
**What changed:** Removed process-wide `static` from `BrowserContainer` — `list`, and all of `get`/`add`/`remove`/`indexOf`/`list()`/`size`/`clear`, are now instance-scoped. `BrowserActivity` (the sole caller; `android:launchMode="singleInstance"` so exactly one instance exists per process) now holds `private final BrowserContainer browserContainer = new BrowserContainer();` and all 12 former `BrowserContainer.xxx(...)` call sites in `BrowserActivity.java` were updated to `browserContainer.xxx(...)`. No other file referenced `BrowserContainer`. No behavior change for the production single-instance case. No architecture change, no new dependency, no multi-process/data-directory work.
**Blocker identified and correctly NOT worked around (per instruction to stop and report rather than expand scope):** the candidate whitelist-cache fields (`whitelist` in `AdBlock`, `whitelistJS` in `Javascript`, `whitelistCookie` in `Cookie`, `whitelistRemote` in `Remote`) were inspected but NOT converted to instance state this step. Source inspection (SOURCE-VERIFIED) showed multiple concurrent instances of each class are constructed independently (per-tab in `NinjaWebView`, per-dialog-open in `BrowserActivity`, per-activity in each `Whitelist_*` screen, per-click in `WhitelistAdapter`, per-call in `BrowserUnit` import/export) and today all of them share one process-wide static `List` by design: adding/removing a domain from any instance (e.g. the whitelist management screen) is instantly visible to every already-open tab's `isWhite()` check because they hold a reference to the same static list object. Converting these fields to instance-scoped would silently regress this currently-relied-upon cross-instance visibility (each instance would freeze a stale snapshot at its own construction time) without delivering any actual profile isolation, since there is still no `profile_id` concept anywhere (single `Ninja4.db`, no per-profile parameterization). Fixing that regression would require a real shared-state/refresh mechanism, i.e. new architecture — explicitly out of scope for this step's constraints (no new architecture, no unrelated refactoring, stop-and-report on architectural blockers).
**Tests:** New `BrowserContainerTest` (2 tests). Genuine RED confirmed first: `container.add(...)` on two separate `new BrowserContainer()` instances shared one process-wide list under the old static field, so both isolation assertions failed against the pre-change code. GREEN after the instance-scoping change. Covers: flat-list behavior (`add`/`get`/`indexOf`/`size`/`list()`), and non-sharing between two separate instances. `remove()`/`clear()` are not unit-tested (they cast to the concrete Android `NinjaWebView`/`WebView`, requiring Android runtime — out of scope for JVM tests, consistent with prior phases). Existing `UrlMatcherTest` (5) and `BrowserUnitTest` (4) still GREEN. `./gradlew :app:testDebugUnitTest` → BUILD SUCCESSFUL, 11/11 tests pass, no regression. LOCAL-JVM-VERIFIED; Android runtime not required and correctly not invoked.
**New HEAD:** `9498936c52dd6a7a614da246a8d1638c1a781d1d` (pushed, verified local = `origin/genspark-dev`).
**Diff scope:** 3 files — 2 modified (`BrowserContainer.java`: static → instance methods/field; `BrowserActivity.java`: new instance field + 12 call sites updated to use it), 1 new (`BrowserContainerTest.java`). No unrelated files touched.
**What was NOT done:** `AdBlock`/`Javascript`/`Cookie`/`Remote` whitelist-cache fields remain `static` (see blocker above — deliberately deferred, not an oversight). No per-profile DB/prefs/cookie/DOM-storage isolation. No multi-process or `WebView.setDataDirectorySuffix` work.

### P1 step 3 (complete)
**What changed:** Introduced `de.baumann.browser.unit.ProfileScopedWhitelist` — a minimal in-memory registry (`Map<String profileId, List<String>>`) that keeps one whitelist `List` instance per profile id, creating it lazily on first access (`forProfile(String)`, `DEFAULT_PROFILE = "default"` for null/unspecified ids). `AdBlock`, `Javascript`, `Cookie`, `Remote` each now hold one `static final ProfileScopedWhitelist` instead of the previous single `static final List<String>` whitelist field, and each gained a second constructor `(Context, String profileId)`. The pre-existing single-arg constructor now delegates to the new one with `ProfileScopedWhitelist.DEFAULT_PROFILE`, so the per-instance `whitelist`/`whitelistJS`/`whitelistCookie`/`whitelistRemote` field is unchanged in every other respect (still final, still read by `isWhite`/`addDomain`/`removeDomain`/`clearDomains` exactly as before) and behaves identically to the pre-change static field for every existing caller. All 15 pre-existing single-arg construction sites (`BrowserActivity`, `NinjaWebView`, `Whitelist_AdBlock`/`_Javascript`/`_Cookie`/`_Remote`, `WhitelistAdapter`, `BrowserUnit`) were left untouched — confirmed via source grep, zero call sites required modification. Same-profile instances (the current, only-used case) still share one List reference exactly as before, so live cross-instance whitelist visibility within a profile is preserved unchanged. Different-profile ids (available via the new constructor, not yet called anywhere in production code) get a separate List, preventing whitelist leakage between profiles once/if a caller starts passing distinct profile ids.
**Tests:** New `ProfileScopedWhitelistTest` (3 tests). Genuine RED confirmed first: the class did not exist, so the test file failed to compile (`error: cannot find symbol`) against the pre-change source — not a fabricated assertion failure. GREEN after implementation. Covers: same-id calls return the same List instance and see each other's additions (`sameProfileId_returnsSameListInstance_acrossCalls`); different ids do not share state (`differentProfileIds_doNotShareState`); `null` id falls back to `DEFAULT_PROFILE` (`nullProfileId_fallsBackToDefaultProfile`). Existing `BrowserContainerTest` (2), `BrowserUnitTest` (4), `UrlMatcherTest` (5) all still GREEN — no regression. `./gradlew :app:testDebugUnitTest --stacktrace` → BUILD SUCCESSFUL, 14/14 tests pass. LOCAL-JVM-VERIFIED (verified via actual test-results XML, `tests`/`failures`/`errors` counts inspected per class). Android runtime not exercised and no ANDROID-RUNTIME-VERIFIED claim is made — `AdBlock`/`Javascript`/`Cookie`/`Remote` themselves (which touch `Context`, `RecordAction`/SQLite, and asset loading) are not directly unit-tested; only the pure-Java `ProfileScopedWhitelist` contract is.
**New HEAD:** `e934a2d55ae56cd2607ccde2fa407c60854be052` (pushed, verified local = `origin/genspark-dev`; one recurring GitHub-auth failure was hit and resolved via GitHub setup re-invocation before this push, per the known non-blocking pattern).
**Diff scope:** 6 files — 4 modified (`AdBlock.java`, `Javascript.java`, `Cookie.java`, `Remote.java`; each: static whitelist field → `ProfileScopedWhitelist` + per-instance field, `loadDomains` takes the list as a parameter instead of touching the old static directly, new 2-arg constructor, 1-arg constructor delegates), 2 new (`ProfileScopedWhitelist.java`, `ProfileScopedWhitelistTest.java`). No unrelated files touched; `git diff --check` clean.
**Remaining limitations (DOCUMENTED, not new blockers):** (1) This boundary is in-memory-only — the underlying `Ninja4.db` whitelist tables (`RecordAction`/`RecordUnit.TABLE_WHITELIST`/`TABLE_JAVASCRIPT`/`TABLE_COOKIE`/`TABLE_REMOTE`) remain a single unpartitioned table with no `profile_id` column; persisted whitelist domains are still shared across all profiles even though the in-memory view can now be separated. Partitioning the DB was explicitly out of scope for this step (constraint #5) and is a candidate for a future, separately-authorized step. (2) No caller currently passes a non-default profile id anywhere in production code — the new constructor exists but is unused by the app today, so there is no active multi-profile behavior yet; this step only builds the boundary, not a profile-switching feature (consistent with YAGNI — no UI/feature work was in scope). (3) CookieManager, WebView data-directory, SharedPreferences, history/bookmarks isolation, and multi-process architecture remain untouched, as instructed.
**What was NOT done:** No persisted/DB whitelist partitioning. No profile-switching UI or feature. No cookie/DOM-storage/prefs/history isolation. No multi-process or `WebView.setDataDirectorySuffix` work. No unrelated refactoring, no dependency additions.

### Exact current objective (done)
Determined the smallest viable profile/identity isolation boundary using the existing architecture, before implementing feature code. Full source-level trace completed at HEAD `3c86c4857e0b96b31c992cd00885fccfdfb4d932`. No production code modified during the trace (read-only).

### Investigation completed (SOURCE-VERIFIED)
1. WebView lifecycle: `NinjaWebView` (per-tab object) created in `BrowserActivity.addAlbum()`, destroyed via `BrowserContainer.remove()/clear()` calling `NinjaWebView.destroy()`.
2. Cookie/storage/database handling: `android.webkit.CookieManager.getInstance()` is a process-wide singleton (used in `NinjaWebViewClient`, `BrowserUnit`, `HelperUnit`). WebView DOM storage/IndexedDB/WebSQL/cache backed by one shared Chromium profile dir (`app_webview/`, hardcoded in `BrowserUnit.clearIndexedDB`). No `WebView.setDataDirectorySuffix()` call exists anywhere in the codebase.
3. Settings/preferences: all identity-relevant toggles (JS/cookies/ad-block/remote-content/UA/search-engine/saveHistory) live in one shared `PreferenceManager.getDefaultSharedPreferences(context)` file, read independently by `NinjaWebView`, `NinjaWebViewClient`, `BrowserActivity`, `RecordAction`, `BrowserUnit`.
4. Tab/session lifecycle: `BrowserContainer.list` is a single `private static final List<AlbumController>` — one flat global tab list for the whole process.
5. Global vs per-tab state: only the `NinjaWebView` Java object and its `WebSettings` feature-toggle flags are genuinely per-instance. Cookies, DOM storage, history, bookmarks, tab list, and whitelist domains (JS/AdBlock/Cookie/Remote) are all process-wide singletons or single shared SQLite file (`Ninja4.db`, hardcoded name in `RecordHelper`) with no `profile_id` concept anywhere in `RecordUnit`.
6. Smallest existing isolation seam: **none exists today.** Every stateful subsystem is global/static. Full leakage map and boundary analysis recorded in the P0 discovery report (chat transcript); summary: tabs/history/bookmarks/whitelist-domains/prefs are isolable via non-architectural refactor (remove `static`, parameterize DB/prefs file by profile id); cookies + WebView disk storage are capped by an Android/WebView platform ceiling (`CookieManager` singleton, `setDataDirectorySuffix` must be called once before any WebView exists in the process) — true multi-profile cookie/storage isolation without a full-restart-per-switch would require multi-process architecture, which is explicitly deferred (YAGNI) pending proof it's required.

### TDD status
No isolation boundary class exists yet to test (nothing to instantiate — `BrowserContainer`/`AdBlock`/`Javascript`/`Cookie`/`Remote` are all `static`-only). Writing an isolation test now would require inventing behavior that doesn't exist, which violates the no-artificial-RED rule. Proposed first legitimate pure-Java TDD seam (NOT yet authorized, NOT yet implemented): extract the duplicated `isWhite(List<String>, String)` matching logic from `AdBlock`/`Javascript`/`Cookie`/`Remote` into one pure-Java static helper (`UrlMatcher.containsAnyDomain`), with a first pure-JVM test characterizing it — no Robolectric, no Android dependency, no static/global-state change yet. This is a prerequisite seam for the later per-profile whitelist refactor, not the isolation feature itself.

### Explicit YAGNI boundary
Do not implement the following during P0 discovery:
- User-Agent spoofing
- Proxy support
- WebRTC leak controls
- Canvas fingerprint changes
- WebGL fingerprint changes
- Audio fingerprint changes
- Timezone spoofing
- Locale spoofing
- Other fingerprinting work

Do not add a new architecture unless the existing architecture is demonstrably insufficient.

## Work-allocation policy
Use the two-agent setup intentionally.

### Genspark should consume credits on
- Deep repository archaeology and cross-file architectural tracing.
- Long implementation tasks with many dependent edits.
- Complex debugging where iterative reasoning is required.
- Running the full local/CI test loop after substantive changes.
- Multi-file feature implementation and refactoring.
- Producing a complete evidence-backed implementation report.

### ChatGPT / low-cost orchestration should handle
- Reading and verifying GitHub state.
- Comparing commits, branches, CI state, and diffs.
- Small documentation/state updates.
- Small, deterministic repository edits when no long reasoning loop is needed.
- Detecting whether a task is already complete.
- Preparing precise bounded prompts for Genspark.
- Reviewing Genspark output against the verification ladder and YAGNI.

### Parallelism rule
Do not leave Genspark waiting on work that can be done independently. While Genspark performs a long implementation/test cycle, perform independent GitHub/state/documentation verification here when safe. Do not create conflicting edits on the same files/branch concurrently.

## Execution protocol for every substantive step
`READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → COMMIT → SAVE STATE`

A step is not considered closed until:
- the actual repository state is checked,
- the implementation/test evidence is recorded,
- the resulting commit SHA is known,
- CI status is checked when applicable,
- the state file is updated,
- and the next single execution step is written down.

## Current single next execution step
P1 step 3 (`ProfileScopedWhitelist` boundary for `AdBlock`/`Javascript`/`Cookie`/`Remote`) is complete, tested, committed, and pushed. The in-memory boundary now exists but nothing in production code calls the new `(Context, String profileId)` constructors yet — there is no profile-switching feature or UI, and the underlying `Ninja4.db` whitelist tables are still unpartitioned (see Remaining limitations above). Await explicit user authorization for P1 step 4. Candidate directions to surface to the user (NOT started, NOT authorized): (a) partition the persisted whitelist tables in `Ninja4.db` by `profile_id` so the in-memory boundary is backed by real persisted separation, or (b) stop at the in-memory boundary and consider P1's tab/whitelist isolation scope closed pending a decision on whether any actual multi-profile UI/feature is in scope at all. No candidate step is proposed unilaterally beyond surfacing this decision point.

## Last updated
2026-09-01 (P1 step 3 executed: introduced `ProfileScopedWhitelist`, wired into AdBlock/Javascript/Cookie/Remote via a new profile-aware constructor overload with the existing single-arg constructor delegating to `DEFAULT_PROFILE` — zero behavior change for all 15 existing call sites. TDD RED→GREEN via `ProfileScopedWhitelistTest` (3 tests, genuine compile-failure RED first). 14/14 unit tests GREEN, pushed as `e934a2d55ae56cd2607ccde2fa407c60854be052`, verified local = remote. Boundary is in-memory only; DB partitioning and any profile-switching feature remain out of scope and undecided.)
