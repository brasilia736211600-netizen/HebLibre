# HebLibre Workflow State

## Purpose
This file is the canonical handoff state for the HebLibre project.
GitHub is the source of truth. Chat history, agent memory, and local workspace state are non-authoritative.

## Current repository state
- Repository: `brasilia736211600-netizen/HebLibre`
- Active development branch: `genspark-dev`
- Current HEAD: `0da72a2bd6a85e9a9ffb9ce9a2ab01a49e46eced`
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
`P1 — Profile / Identity Isolation — Implementation (OPENED, step 1 of N complete)`

### P1 step 1 (complete)
**What changed:** Extracted the identical `isWhite(String)` domain-matching loop, previously duplicated in `AdBlock`, `Javascript`, `Cookie`, `Remote`, into one pure-Java static helper `de.baumann.browser.unit.UrlMatcher.containsAnyDomain(List<String>, String)`. All four classes now delegate to it. No behavior change, no architecture change, no new dependency, no multi-process/data-directory work (explicitly out of scope, untouched).
**Tests:** New `UrlMatcherTest` (5 tests, all GREEN) characterizes current matching semantics (substring containment, null-safe, empty-list-safe) before any future per-profile refactor. Existing `BrowserUnitTest` (4 tests) still GREEN — no regression. `./gradlew :app:testDebugUnitTest` → BUILD SUCCESSFUL, 9/9 tests pass. Verified locally; Android runtime not required for this step (pure JVM logic) and was correctly not invoked.
**New HEAD:** `0da72a2bd6a85e9a9ffb9ce9a2ab01a49e46eced` (pushed, verified local = `origin/genspark-dev`).
**Diff scope:** 6 files — 4 modified (`AdBlock.java`, `Javascript.java`, `Cookie.java`, `Remote.java`, each: loop replaced by one delegation line), 2 new (`UrlMatcher.java`, `UrlMatcherTest.java`). No unrelated files touched.
**What was NOT done:** No per-profile storage/DB/prefs/cookie isolation yet — this step only prepared a shared seam. No multi-process or `WebView.setDataDirectorySuffix` work (explicitly excluded). No `BrowserContainer`/static-whitelist-field refactor yet.

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
P1 step 1 (UrlMatcher seam) is complete, tested, committed, and pushed. Await explicit user authorization for P1 step 2. Candidate next bounded step (NOT yet authorized, NOT started): remove `static` from the whitelist-cache fields in `AdBlock`/`Javascript`/`Cookie`/`Remote` and from `BrowserContainer.list`, making them instance-scoped, as the next-smallest piece of the isolation boundary identified in the P0 trace (tabs/whitelist-domains path; still excludes cookies/DOM-storage which remain capped by the documented platform ceiling and multi-process work, which stays out of scope).

## Last updated
2026-09-01 (P1 opened and step 1 executed: UrlMatcher extraction, TDD characterization test, 9/9 unit tests GREEN, pushed as `0da72a2bd6a85e9a9ffb9ce9a2ab01a49e46eced`, verified local = remote.)
