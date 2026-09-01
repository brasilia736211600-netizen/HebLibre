# HebLibre Workflow State

## Purpose
This file is the canonical handoff state for the HebLibre project.
GitHub is the source of truth. Chat history, agent memory, and local workspace state are non-authoritative.

## Current repository state
- Repository: `brasilia736211600-netizen/HebLibre`
- Active development branch: `genspark-dev`
- Current HEAD: `1ded82a77b240e41f416376d3a27663974d273e8`
- Default branch: `l10n_crowdin`
- Project type: Android application based on the FOSS Browser/WebView codebase
- Re-verified live against `origin/genspark-dev` (fresh `git fetch`, not assumed from memory): local HEAD, remote HEAD, and this document agree exactly, working tree clean.
- CI run status for the current branch cannot be checked via API with the current credential (`HTTP 403: Resource not accessible by integration` on `actions/runs`, `check-runs`, and `status` endpoints — missing `actions:read`/`checks:read` scope). Repository is private, so no unauthenticated fallback exists. This is a known, previously documented limitation, not a new blocker.
- The four latest commits (`1a930a7`, `19bd8a8`, `3c86c48`, `1ded82a`) are documentation-only; no product code changed in any of them.

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
`P0 — Profile / Identity Isolation — Deep Architecture Trace COMPLETE`

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
P0 (Deep Architecture Trace) is complete and closed. The previously proposed `UrlMatcher` extraction is **preparatory P1 production code, not a P0 deliverable** — re-checked this round and confirmed it must NOT be started automatically just because a prior report mentioned it; that would be unauthorized speculative production work under this engagement's phase-gating rules and YAGNI.
**Decision point (requires explicit user authorization, not autonomous action):** whether to open `P1 — Profile / Identity Isolation — Implementation`, and if so, whether its first bounded step is the `UrlMatcher` seam or something else. Nothing will be implemented until the user explicitly authorizes entering P1.

## Last updated
2026-09-01 (Re-verified branch/HEAD/remote/working-tree with a fresh `git fetch` — no drift found, local and remote both at `1ded82a77b240e41f416376d3a27663974d273e8`. CI run status remains unreadable via API due to a known credential scope gap. No code changed. Confirmed `UrlMatcher` is not yet authorized and correctly withheld.)
