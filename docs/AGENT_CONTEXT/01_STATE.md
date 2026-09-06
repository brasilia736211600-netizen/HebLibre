# HebLibre Agent Context — Current State

## Repository
`brasilia736211600-netizen/HebLibre` — active branch `genspark-dev`.

## Current branch checkpoint
`41a9d0d3c4f01f5c798726933f956a7613debcf3` — diagnostic cleanup commit removing the temporary runner probe.

## Current application/source checkpoint
`086c7038faa5bbb8b9d2a24f392edfe9cce3a07e` — latest application/test checkpoint; no application changes were introduced during the Actions investigation.

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
- Transfer parser rejects input larger than 8 MiB before structural parsing/decryption.
- Runtime smoke harnesses are debug-only and exercise production Profile Manager and Profile Transfer screens without exporting those production activities.
- Runtime smoke coverage includes database migration, profile isolation, rollback, privacy-rule isolation/purge, preference isolation, preference transfer, and active-profile deletion/default-preference restoration.
- JVM transfer test coverage includes oversized-input rejection.

## Verification
- SOURCE-VERIFIED: current application/test checkpoint `086c7038...` remains the basis of the release candidate; branch now also contains documentation/diagnostic commits only.
- TEST-VERIFIED: fresh hosted execution is blocked before the first step; no new hosted test pass exists after `086c...`.
- CI-VERIFIED: current Unit Tests run `34002495828` failed before steps; rerunning its exact Job produced new Job `101530040003`, which again failed with `steps: null`. A minimal `ubuntu-latest` probe also failed before any step. This classifies the blocker as runner/Actions startup, not an app test failure.
- ANDROID-RUNTIME-VERIFIED: historical baseline Runtime Smoke run `33994758706` passed on an older checkpoint; current profile/preference hardening is not yet runtime-verified.
- ARTIFACT-VERIFIED: historical Actions artifact `9977732103` was downloaded and checksum-verified locally; it is not current release evidence.
- DOCUMENTED: GitHub issue #3 records the runner blocker and exact evidence.

## Actions blocker diagnosis
- Last known successful Runtime Smoke run: `33994758706` (2026-09-05), with normal step execution through emulator smoke.
- Latest Unit Tests run `34002495828` on head `4a8ad46...` had Job `101403747772` fail before any step.
- Rerun of the same job created Job `101530040003`; it also failed before any step.
- The temporary minimal `ubuntu-latest` probe failed before any step as well, ruling out a single workflow YAML path or only the `ubuntu-24.04` label.
- No job logs are available for these pre-step failures.
- The symptom matches recent GitHub Community reports of private repositories with fresh standard-runner jobs failing before runner assignment and producing zero-step/no-log runs.
- The temporary diagnostic probe has been removed; production workflows were not rewritten to chase runner labels.
- GitHub-side Actions scheduling/dispatch recovery is currently required. Repository YAML cannot repair a runner that never starts.

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

## Current blockers / release gate
1. GitHub-hosted runner allocation/startup is preventing fresh Unit and Runtime Smoke execution.
2. No current x86_64 APK artifact exists from Actions until a runner executes.
3. Fresh emulator verification is pending; historical baseline smoke is the only runtime evidence.
4. Physical-device validation remains pending for the final consolidated build.
5. Per-profile proxy and complete WebView storage isolation remain intentionally deferred.

## Next executable slice
1. Keep application source stable at `086c7038...` unless a concrete source defect is found.
2. When a GitHub-hosted runner successfully starts, run Unit Tests and Runtime Smoke against the latest branch descendant.
3. Download the exact current APK + checksum and independently verify the digest.
4. Use that artifact for one consolidated emulator smoke gate, then one physical-device validation gate.
5. Only after that evidence chain classify the build as release-ready.
