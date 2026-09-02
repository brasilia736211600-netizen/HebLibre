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
- Exact current branch HEAD: `7c5285474324699d04daa5e67c107e26afbe90ae`.
- This HEAD contains completed P2 Step 1 tracking cleanup and completed P2 Step 2 HTTPS-only navigation policy.
- Canonical continuity files: `docs/HEBLIBRE_WORKFLOW_STATE.md`, `docs/HEBLIBRE_MASTER_PROJECT_MAP.md`, `docs/HEBLIBRE_RESUME_COMMAND.md`, `docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md`.
- Genspark credits are exhausted; all work continues through available GitHub/local capabilities.

## Completed engineering
1. Build/toolchain recovery: Gradle 5.4.1 / AGP 3.5.2 baseline recovered with JDK 11, compile SDK 29, build-tools 28.0.3; debug build verified locally. Baseline `e11562fd42639d5b12e5468c14d461523aa96529`.
2. Minimal JUnit4 harness: `BrowserUnit.isURL` characterization; 4 tests green. `6211fc57cc5589f24106df0c1aa2a4e7b6ae2058`.
3. CI workflow: JDK 17 for SDK tooling, JDK 11 for Gradle. Successful run `33459477325`.
4. P1 Steps 1–6: profile/whitelist identity groundwork complete; Step 6 CI-VERIFIED by run `33643362798`.
5. P1 Step 7: lifecycle/test-seam review completed; no dependency-free end-to-end seam exists, so no synthetic framework/test was added.
6. P1 Step 8 runtime validation: DEFERRED because no Android runtime is available; not a development blocker.
7. P1 Step 8A: WebLibre/HebLibre feature gap matrix completed and persisted.
8. P2 Step 1: conservative tracking/query-parameter cleanup completed.
9. P2 Step 2: local HTTPS-only navigation policy completed.

## P2 Step 1 — conservative tracking/query-parameter cleanup
- TDD contract: `UrlTrackerCleanerTest.java`.
- Implementation: dependency-free `UrlTrackerCleaner.java`.
- Removes only `utm_*`, `gclid`, `dclid`, `fbclid`, `msclkid`, and `yclid` while preserving meaningful query data, order, path, and fragment.
- Wired through the existing URL-normalization/navigation path.
- Commits: `0de51ef47591a1aa9c869bb7909906025662e726`, `61795dbb75a22dcaa7a2ed8e6fc3d353358eedc4`, `410b9ba9c8434228c9fac51a10a42e39091c9f35`, `b592f8abb419826caa06b2ec254a299c942d9ac7`.
- P2 Step 1 source scope is limited to the cleaner, its tests, and `BrowserUnit.java` wiring.

### P2 Step 1 verification
- SOURCE-VERIFIED: complete.
- TEST-VERIFIED: TDD contract committed.
- CI-VERIFIED: earlier successful unit-test evidence exists, but no longer using the incorrect `33648307698` reference as feature-HEAD evidence.
- ANDROID-RUNTIME-VERIFIED: not performed.
- DOCUMENTED: superseded by this state record.

## P2 Step 2 — HTTPS-only navigation policy
### Source verification
The existing architecture has two relevant navigation paths. Direct/user-entered navigation flows through `NinjaWebView.loadUrl()`, which already calls `BrowserUnit.queryWrapper(...)` before `super.loadUrl(...)`. Link navigation is intercepted by `NinjaWebViewClient.handleUri(...)`, where HTTP(S) links are loaded through the same WebView. No separate networking stack is present. fileciteturn543file0 fileciteturn544file0

The start-settings screen is backed by `preference_start.xml`, so the feature can be exposed through the existing preferences UI without introducing a new settings architecture. fileciteturn535file0 fileciteturn542file0

### TDD / implementation
- Added `app/src/test/java/de/baumann/browser/unit/HttpsOnlyPolicyTest.java` first as the deterministic contract.
- Added `app/src/main/java/de/baumann/browser/unit/HttpsOnlyPolicy.java` as a plain-Java, dependency-free policy seam.
- Policy behavior: absolute `http://` URLs are upgraded to `https://`; existing HTTPS URLs and non-HTTP schemes remain unchanged; null/blank input remains unchanged. fileciteturn539file0turn540file0
- Added the existing preference UI entry `https_only`, default `false`, with localized title/summary resources. fileciteturn541file0turn542file0
- `NinjaWebView.loadUrl()` enforces the policy only when `https_only` is enabled, after existing URL wrapping. fileciteturn543file0
- `NinjaWebViewClient.handleUri()` applies the policy to intercepted link navigation before loading an upgraded URL. fileciteturn544file0
- No HTTP fallback is attempted after upgrading; no new networking architecture or dependency was introduced.

### Commit sequence
- `d1ed67d5b4edf2d842adae884daa525cefed8a6f` — first HTTPS-only direct-navigation implementation.
- `7c5285474324699d04daa5e67c107e26afbe90ae` — link-navigation enforcement completion.

### Verification
- SOURCE-VERIFIED: complete for the direct and intercepted-link navigation paths.
- TEST-VERIFIED: `HttpsOnlyPolicyTest` is committed and the CI job completed its `Run unit tests` step successfully.
- CI-VERIFIED: GitHub Actions run `33650164143`, job `test`, completed with conclusion `success`; all workflow steps including `Run unit tests` completed successfully.
- ANDROID-RUNTIME-VERIFIED: not performed; no Android runtime is available.
- DOCUMENTED: complete.

## Architecture boundary
The P1 profile mechanism still does not isolate process-wide `CookieManager`, Chromium WebView disk storage, the default SharedPreferences store as a whole, history, or bookmarks. No full browser-storage isolation is claimed.

No multi-process architecture, WebView data-directory switching, extension runtime, proxy/Tor stack, WebRTC subsystem, DNS-over-HTTPS stack, fingerprinting subsystem, or AI runtime has been added.

## Next execution step
**P2 Step 3 — source-verify the next smallest high-value WebLibre gap, Global Privacy Control (GPC), and implement it only if current WebView request-header APIs provide a deterministic local seam without introducing networking architecture.**

## Last updated
2026-09-02 — P2 Step 2 completed and CI-VERIFIED on run `33650164143`; current HEAD `7c5285474324699d04daa5e67c107e26afbe90ae`; Android runtime remains deferred.
