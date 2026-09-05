# HebLibre Continuation Checkpoint — 2026-09-06

## Current branch
`genspark-dev`

## Source checkpoint at this save
`7792b08e7143ec0069dc6abe6317114c14fa0d13`

## Completed in this continuation
- Re-verified the current GitHub branch and authoritative workflow context.
- Downloaded and independently verified the latest available historical Android Runtime Smoke artifact. It is from an older source checkpoint and is explicitly not current release evidence.
- Confirmed the latest current Runtime Smoke and Unit workflow failures occur before any runner step, so no current APK was produced by those runs.
- Confirmed the Runner Probe also fails before any step, including its `ubuntu-22.04` job. This classifies the current CI blocker as runner allocation/startup, not a Gradle or emulator test failure.
- Added an atomic SQLite transaction around `RecordAction.deleteProfileRecords(...)` so all app-owned profile records/privacy-rule rows are purged as one unit.
- Detected and fully reverted an attempted JAXP/XML hardening change after compatibility review; no incompatible XML-hardening code remains on the branch.
- Added `ProfilePreferencesStore`, a compatibility bridge that stores a curated set of browser/privacy preferences in per-profile SharedPreferences namespaces while preserving the existing global-preference consumers.
- Updated `ProfileManagerActivity` so profile switching saves the current profile's browser/privacy settings, initializes the target profile, loads its settings into the legacy global preference store, and requests the existing controlled restart. New profiles inherit the currently active settings on first initialization; deleted profiles have their profile preference namespace cleared.
- Extended `ProfileManagerSmokeActivity` with deterministic isolation checks for profile-local `desktop_mode` and `userAgent` values, including restoration and cleanup.

## Verification classification
- SOURCE-VERIFIED: yes for the current branch source changes and smoke test wiring.
- TEST-VERIFIED: not current. The new smoke path has not executed because GitHub runners are unavailable; historical unit/runtime evidence remains tied to older checkpoints.
- CI-VERIFIED: blocked. Current GitHub-hosted jobs fail before steps.
- ANDROID-RUNTIME-VERIFIED: historical only on older checkpoint; no fresh current runtime result.
- ARTIFACT-VERIFIED: historical artifact only; no current artifact exists while runner startup is blocked.
- DOCUMENTED: yes.

## Profile-settings scope
The current profile-local preference set covers the explicitly inspected browser/privacy settings from `preference_setting.xml` and `preference_start.xml`: desktop mode, screenshot protection, media-permission guard, third-party-cookie control, download-cookie behavior, images, Save-Data, HTTPS-only, Global Privacy Control, history saving, location, ad blocking, JavaScript, cookies, remote content, favorite/start URL, search engine/custom search, and user-agent override.

UI-only preferences (theme, toolbar layout, tab presentation, gesture mappings, filter colors) are intentionally not moved yet. They require a separate product decision because they affect application-wide UX rather than browsing identity and should not be made profile-local by guessing.

Profile transfer currently remains metadata/history/bookmarks/tabs oriented. Browser preference transfer is not yet included in the portable profile format.

## Current material blockers
1. GitHub-hosted runner allocation/startup failure affects both `ubuntu-24.04` build/smoke workflows and the `ubuntu-22.04` runner probe.
2. A current APK cannot be downloaded from Actions until a runner executes the build.
3. WebView storage/cookie isolation beyond the supported AndroidX WebKit multi-profile capability remains bounded by platform behavior and must not be overstated.
4. Per-profile WebView proxy routing remains architectural; no global proxy emulation is allowed.
5. Full profile-local application UI preferences are intentionally deferred pending a product-level ownership decision.

## Next executable work
- Add or refine deterministic coverage around `ProfilePreferencesStore` only where stable seams exist.
- Review profile transfer boundaries and decide whether browser preferences belong in the portable encrypted/plain contract; avoid speculative format churn.
- Once CI runner allocation recovers, run consolidated Unit + Runtime Smoke, download the fresh x86_64 artifact, verify checksum, and perform the final runtime gate.
