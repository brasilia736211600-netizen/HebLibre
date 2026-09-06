# HebLibre Agent Context — Current State

## Repository
`brasilia736211600-netizen/HebLibre` — active branch `genspark-dev`.

## Current application/source checkpoint
`086c7038faa5bbb8b9d2a24f392edfe9cce3a07e` — latest application/test checkpoint; subsequent commits are documentation/diagnostic cleanup only.

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
- Runtime smoke harnesses are debug-only and exercise production Profile Manager and Profile Transfer screens without exporting those production activities.
- Runtime smoke coverage includes database migration, profile isolation, rollback, privacy-rule isolation/purge, preference isolation, preference transfer, and active-profile deletion/default-preference restoration.
- JVM transfer test coverage includes oversized-input rejection.

## Verification
- SOURCE-VERIFIED: application/test checkpoint `086c7038...` remains the release-candidate source basis.
- TEST-VERIFIED: no fresh hosted test pass exists after `086c...` because current Jobs terminate before the first step.
- CI-VERIFIED: current Unit run `34002495828` failed before steps; exact Job rerun `101530040003` also failed before steps. A minimal `ubuntu-latest` probe failed the same way. This is not evidence of an application/test failure.
- ANDROID-RUNTIME-VERIFIED: historical baseline Runtime Smoke `33994758706` passed on an older checkpoint only.
- ARTIFACT-VERIFIED: historical Actions artifact `9977732103` was downloaded and checksum-verified; it is not the current build.
- DOCUMENTED: GitHub Issue #3 and this context record the blocker.

## Actions blocker diagnosis
A decisive repository-level comparison is now available:
- `HebLibre` is **private**.
- The user's other repository `WebLibre` is **public** and its Actions job `101530796057` successfully received a GitHub-hosted runner and executed multiple steps on 2026-09-06.
- GitHub's current documentation states that standard GitHub-hosted runners are free for public repositories, while private repositories consume the account's included Actions minutes. GitHub Free includes 2,000 standard-runner minutes/month; when the quota is exhausted and there is no valid payment method, further usage is blocked.
- HebLibre's pre-step failures therefore now have **private-repository Actions quota/billing state as the leading cause**, rather than a runner-label or application defect.
- The diagnosis is highly consistent with the observed `steps: null` / no-log failures, but billing usage cannot be read through the available connector, so it is not yet mathematically proven from account telemetry.
- A secondary possibility remains an Actions backend/repository state defect; no more runner-label probes should be added unless new evidence requires one.
- The temporary runner probe has been removed.

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
- `D-035`: WebLibre public vs HebLibre private comparison makes private Actions quota/billing the leading runner-blocker hypothesis; no privacy change is made automatically.

## Current blockers / release gate
1. Verify/restore the GitHub Actions allowance for the **private** HebLibre repository (or attach a valid payment method / upgrade as appropriate). Do not expose the repository publicly merely to bypass CI billing without an explicit decision.
2. Once a runner starts, execute Unit Tests and Android Runtime Smoke on the latest branch descendant.
3. Download the current x86_64 APK + checksum and independently verify the digest.
4. Execute one consolidated emulator smoke gate, then final physical-device validation.
5. Per-profile proxy and complete WebView storage isolation remain intentionally deferred.

## Next executable slice
Keep application source stable at `086c7038...`. The next technical action after Actions access is restored is the consolidated current build/test/smoke chain; no further runner-label experimentation is warranted.