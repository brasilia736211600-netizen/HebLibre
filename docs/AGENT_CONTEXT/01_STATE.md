# HebLibre Agent Context — Current State

## Repository
`brasilia736211600-netizen/HebLibre` — active branch `genspark-dev`.

## Current source checkpoint
`086c7038faa5bbb8b9d2a24f392edfe9cce3a07e` — latest application/test checkpoint.

## Latest documented branch checkpoint
`20f048f61065a81ca1c4e1ab20f6fe8fd2e2e9c5` before the current source/test commits; this file is being synchronized again now.

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
- Active-profile deletion immediately switches the legacy global preference view back to `default` and requests the existing restart flag.
- Transfer parser now rejects input larger than 8 MiB before structural parsing/decryption.
- Runtime smoke harnesses are debug-only and exercise production Profile Manager and Profile Transfer screens without exporting those production activities.
- Runtime smoke coverage includes database migration, profile isolation, rollback, privacy-rule isolation/purge, preference isolation, preference transfer, and active-profile deletion/default-preference restoration.
- JVM transfer test coverage includes oversized-input rejection.

## Verification
- SOURCE-VERIFIED: current source checkpoint `086c7038...` is present on `genspark-dev`.
- TEST-VERIFIED: fresh hosted execution is unavailable because current GitHub Actions jobs terminate before the first step; test sources are present and inspected.
- CI-VERIFIED: current Unit, Runtime Smoke, and Runner Probe jobs consistently fail before runner steps; this is classified as runner allocation/startup, not an application failure.
- ANDROID-RUNTIME-VERIFIED: historical baseline Runtime Smoke run `33994758706` passed on an older checkpoint; current profile/preference hardening is not yet runtime-verified.
- ARTIFACT-VERIFIED: historical Actions artifact `9977732103` was downloaded through the official GitHub artifact API; its APK SHA-256 was independently verified against the embedded checksum. It is not current release evidence.
- Current exact artifact download is blocked until a runner executes the current build.

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

## Recent durable decisions
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

## Current blockers / gate
1. GitHub-hosted runner allocation/startup is preventing fresh Unit and Runtime Smoke execution.
2. No current x86_64 APK artifact exists from Actions until a runner executes.
3. Fresh emulator verification is therefore pending; historical baseline smoke is the only runtime evidence.
4. Physical-device validation remains pending for the final consolidated build.
5. Per-profile proxy and complete WebView storage isolation remain intentionally deferred.

## Next executable slice
1. On the first Actions run that actually assigns a runner, verify Unit Tests and Runtime Smoke on `086c7038...` or its latest descendant.
2. Download the exact APK + checksum from that run, independently verify checksum, and run the consolidated emulator gate once.
3. Then perform the final physical-device validation gate; only after that classify the build as release-ready.
