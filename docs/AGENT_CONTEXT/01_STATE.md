# HebLibre Agent Context — Current State

## Repository
`brasilia736211600-netizen/HebLibre` — active branch `genspark-dev`.

## Current branch checkpoint
`aa8b9ba3530808d1301560060c44b78f4ed7d59a` — latest synchronized branch descendant containing the final CI smoke assertion hardening.

## Latest application/source checkpoint
`cc3d2bc77994a08db1a7f657f94690b17fc22a54` — latest application/test source change; subsequent changes are CI/documentation only. This commit restores the required profile-aware `RecordAction.listTab()` API used by session/profile smoke code.

## Completed bounded work
- Profile metadata/catalog and deterministic profile identity policy.
- AndroidX WebKit 1.9.0 multi-profile binding with capability checks; minSdk 21 preserved; compile/build baseline Android 33 / build-tools 33.0.2; targetSdk 29 retained.
- Profile manager UI with production activities non-exported.
- Profile-scoped HISTORY/BOOKMARK/TAB records with v5 -> v6 migration to `default`.
- Profile-scoped privacy-rule rows: WHITELIST/JAVASCRIPT/COOKIE/REMOTE.
- Profile-owned session persistence and launcher restore; external BrowserActivity intent behavior preserved.
- Profile transfer: versioned plain + AES-GCM encrypted export/import, PBKDF2 key derivation, exact headers, malformed encrypted-dimension rejection, wrong-password/tamper rejection, backward-compatible PREFERENCES section.
- Profile transfer includes only a curated typed browser/privacy preference snapshot; WebView cookies/storage/login secrets are excluded.
- Transactional HISTORY/BOOKMARK/TAB import with rollback and cleanup of a newly-created profile on failure.
- Profile deletion purges app-owned records/privacy-rule rows transactionally and clears profile-local preference namespace.
- Active-profile deletion immediately switches the legacy global preference view back to `default` and requests the existing restart behavior.
- Transfer parser rejects input larger than 8 MiB before structural parsing/decryption.
- Restored `RecordAction.listTab()` as a profile-aware read API required by profile/session code and smoke harnesses.
- Runtime smoke harnesses are debug-only and exercise production Profile Manager and Profile Transfer screens without exporting those production activities.
- Runtime smoke coverage includes database migration, profile isolation, rollback, privacy-rule isolation/purge, preference isolation, preference transfer, active-profile deletion/default-preference restoration, launch, navigation, and restart/session restore.
- JVM transfer test coverage includes oversized-input rejection.

## Verification
- SOURCE-VERIFIED: latest application/test checkpoint `cc3d2bc...` is present on `genspark-dev`; no application source changes were introduced after it.
- TEST-VERIFIED: Unit Tests run `34051212987` succeeded completely, including ARM split build, APK output verification, artifact upload, and `:app:testDebugUnitTest`.
- CI-VERIFIED: Unit Tests run `34051212987` succeeded. Android Runtime Smoke run `34051212916` succeeded end-to-end on a real GitHub-hosted runner.
- ANDROID-RUNTIME-VERIFIED: Runtime Smoke `34051212916` installed the current x86_64 debug APK on API 29 x86_64 Pixel 2 emulator; verified process liveness, `example.com`/`Example Domain`, production Profile Manager launch, default profile UI, production Profile Transfer launch/UI, restart, restored `Example Domain`, and absence of fatal app exceptions.
- ARTIFACT-VERIFIED: Runtime artifact `9994608514` was downloaded from GitHub Actions. ZIP SHA-256 verified independently as `a41fd22149bdca09d9df128b46b29fb518c4bd096779dee261dc0e26b5d13827`. Embedded APK SHA-256 verified independently as `cf8b57af86cbfabe60e973ec1e970f6f2514910a4db9778ecafa37df88366865`. Unit ARM artifact `9994609950` was also downloaded and its ZIP SHA-256 independently verified as `2f97f109b1dde9dae356cb4e98b362004e0ee58c76007da7e3441b056a93ccde`; it contains both ARM APK splits.
- DOCUMENTED: CI blocker, source fix, smoke hardening, and final verification are recorded here and in `02_CONTINUATION_2026-09-06.md`.

## Actions blocker — resolved operationally
The former pre-step Actions failure was specific to the private-repository entitlement path. After `HebLibre` was changed to **public**, GitHub-hosted runners allocated normally and both Unit and Android Runtime Smoke completed. No runner-label churn or application workaround was required. Diagnostic `runner-probe.yml` was removed.

## Runtime findings and fixes from the restored runner
1. First live build exposed a real compile error: `ProfileSessionStore`/profile smoke code required `RecordAction.listTab()`. Fixed in `cc3d2bc...` with a profile-aware query constrained by active profile.
2. First live emulator run reached the app, navigation, and Profile Manager but failed on brittle UI text `Default (default)`; source/harness logic itself was functioning.
3. One diagnostic run captured actual UIAutomator XML: profile row rendered `Active  Default  (default)` and the button rendered `NEW PROFILE`. Smoke assertions were hardened to test stable rendered semantics rather than exact typography.
4. Final Runtime Smoke initially failed only on the transfer title assertion; source inspection showed the activity sets title `Profile transfer` but Android button text is transformed for display. The final smoke now checks for the actual semantic control text with case-insensitive matching (`Import profile`).
5. Final run `34051212916` passed every step.

## Architectural boundaries
- WebView profile binding must occur before WebView use/navigation.
- Active-profile changes do not rebind an already-live WebView; restart is required.
- Session persistence is owned by the profile that created live WebViews.
- Session restore persists HTTP(S) URLs/titles, not pixel-perfect WebView navigation state or internal storage.
- Transfer excludes WebView-internal cookies/storage/login secrets.
- UI-only preferences (theme/layout/gesture/filter presentation) remain global until explicitly classified; they are not guessed into profile-local storage.
- Complete WebView data-directory isolation, per-profile proxy routing, and same-URL in-process switching are not claimed complete.
- Do not emulate profile proxying with process-global `ProxyController`.
- Do not change SSL override, cleartext, backup, or file-origin/DOM-storage semantics without explicit architectural decision.

## Durable decisions
- `D-022`: complete profile-local settings require explicit ownership/migration coverage.
- `D-023`: no process-global ProxyController emulation for profile proxying.
- `D-024`: pre-step Actions failures are runner/workflow initialization failures.
- `D-025`: deletion predicates use SQLite selection args.
- `D-026`: production Profile Transfer remains non-exported; smoke enters through debug-only launcher.
- `D-027`: exact transfer headers + encrypted dimension validation + fail-closed malformed input.
- `D-028`: profile-record imports are transactional; activation only after success.
- `D-029`: delete user-profile app-owned history/bookmarks/tabs before catalog removal.
- `D-030`: delete all profile-scoped app-owned privacy-rule rows with the profile.
- `D-031`: curated browser/privacy settings use a typed compatibility bridge; UI-only preferences remain global.
- `D-032`: deleting the active profile immediately restores the default preference view and requests restart.
- `D-033`: transfer input is bounded to 8 MiB before parsing/decryption.
- `D-034`: temporary runner probes are diagnostic only and must not remain in the release branch.
- `D-035`: private vs public comparison made private Actions quota/billing the leading former blocker; no privacy change was made automatically.
- `D-036`: Smoke UI assertions must target stable rendered semantics, not brittle exact typography.

## Current blockers / release gate
1. No current CI blocker remains for hosted build/test execution.
2. Current x86_64 APK artifact is available and emulator-verified.
3. ARM64-v8a and armeabi-v7a debug APKs are available from the successful Unit artifact.
4. Physical-device validation remains the final evidence gate before calling the project fully release-ready; it is not required for routine development because the hosted emulator smoke is now operational.
5. Per-profile proxy and complete WebView storage isolation remain intentionally deferred.

## Next executable slice
Keep the application source stable at `cc3d2bc...`. Use the verified current APK artifact for final distribution/physical-device validation. No further CI experimentation is required unless a new concrete failure appears.
