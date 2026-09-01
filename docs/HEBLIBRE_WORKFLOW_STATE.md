# HebLibre Workflow State

## Purpose
This file is the canonical handoff state for the HebLibre project.
GitHub is the source of truth. Chat history, agent memory, and local workspace state are non-authoritative.

## Current repository state
- Repository: `brasilia736211600-netizen/HebLibre`
- Active development branch: `genspark-dev`
- Current HEAD: `9498936c52dd6a7a614da246a8d1638c1a781d1d`
- Default branch: `l10n_crowdin`
- Project type: Android application based on the FOSS Browser/WebView codebase
- Verified via fresh `git fetch`: local HEAD = `origin/genspark-dev` HEAD, working tree clean.
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
`P1 — Profile / Identity Isolation — Implementation (OPENED, step 2 of N complete)`

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
P1 step 2 (`BrowserContainer` instance-scoping) is complete, tested, committed, and pushed. `AdBlock`/`Javascript`/`Cookie`/`Remote` whitelist-cache fields are a genuine architectural blocker for simple static-removal (see P1 step 2 note above) — NOT a bounded step until a decision is made on how to preserve current cross-instance whitelist visibility (e.g. a shared registry/reload mechanism), which is itself new architecture and requires explicit user authorization/direction before any design work starts. Await explicit user authorization for P1 step 3. No candidate step is proposed unilaterally beyond surfacing this decision point.

## Last updated
2026-09-01 (P1 step 2 executed: BrowserContainer static → instance refactor, TDD RED→GREEN via BrowserContainerTest, 11/11 unit tests GREEN, pushed as `9498936c52dd6a7a614da246a8d1638c1a781d1d`, verified local = remote. AdBlock/Javascript/Cookie/Remote static whitelist fields inspected and deliberately left untouched — converting them would regress currently-relied-upon cross-instance whitelist visibility without delivering real isolation; reported as blocker per instructions rather than worked around.)
