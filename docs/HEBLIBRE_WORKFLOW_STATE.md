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
- Exact current branch HEAD: `b592f8abb419826caa06b2ec254a299c942d9ac7`.
- This HEAD contains the completed P2 Step 1 URL-cleanup implementation.
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

## P2 Step 1 — conservative tracking/query-parameter cleanup
### TDD / implementation
- TDD contract added first as `app/src/test/java/de/baumann/browser/unit/UrlTrackerCleanerTest.java`.
- Pure-Java implementation added as `app/src/main/java/de/baumann/browser/unit/UrlTrackerCleaner.java`.
- Initial cleaner was corrected to preserve raw URI components rather than unintentionally re-encoding them.
- Existing `BrowserUnit.queryWrapper()` was wired to call the cleaner only after an input has already been classified as a URL and before navigation returns it.
- Search-query generation behavior was not changed.
- Existing Google redirect unwrapping was not redesigned.

### Conservative behavior
The cleaner removes only:
- `utm_*`
- `gclid`
- `dclid`
- `fbclid`
- `msclkid`
- `yclid`

It preserves meaningful parameters, parameter order, path, and fragment. Invalid or blank input is returned unchanged. No network access, dependency, broad canonicalization, or arbitrary parameter deletion was introduced.

### Commits
- `0de51ef47591a1aa9c869bb7909906025662e726` — TDD contract.
- `61795dbb75a22dcaa7a2ed8e6fc3d353358eedc4` — cleaner implementation.
- `410b9ba9c8434228c9fac51a10a42e39091c9f35` — preserve raw URI components.
- `b592f8abb419826caa06b2ec254a299c942d9ac7` — wire cleaner into URL navigation.

### Diff scope
Compared with `9ec94a0a5909b37e56f477c874da18d12a62c957`, the implementation is limited to:
- add `UrlTrackerCleaner.java`;
- add `UrlTrackerCleanerTest.java`;
- modify `BrowserUnit.java` for the existing URL path.
No unrelated production files were changed.

### Verification
- SOURCE-VERIFIED: complete for implementation path and scope.
- TEST-VERIFIED: the TDD contract is committed; the exact local test command result is not independently captured in this state record.
- CI-VERIFIED: GitHub Actions run `33648307698`, job `test`, completed successfully; the `Run unit tests` step completed with success on the feature HEAD.
- ANDROID-RUNTIME-VERIFIED: not performed.
- DOCUMENTED: complete.

## Architecture boundary
The P1 profile mechanism still does not isolate process-wide `CookieManager`, Chromium WebView disk storage, the default SharedPreferences store as a whole, history, or bookmarks. No full browser-storage isolation is claimed.

No multi-process architecture, `WebView.setDataDirectorySuffix`, extension runtime, proxy/Tor stack, WebRTC subsystem, DNS-over-HTTPS stack, fingerprinting subsystem, or AI runtime has been added.

## Next execution step
**P2 Step 2 — perform source verification for the smallest next high-value privacy/navigation gap (HTTPS-only mode) and implement it only if the current WebView navigation architecture supports a small deterministic change without introducing a new networking architecture. Start with a characterization test/contract where a plain-JVM seam exists.**

## Last updated
2026-09-02 — P2 Step 1 implementation completed, CI-VERIFIED on run `33648307698`, current HEAD `b592f8abb419826caa06b2ec254a299c942d9ac7`, Android runtime remains deferred.
