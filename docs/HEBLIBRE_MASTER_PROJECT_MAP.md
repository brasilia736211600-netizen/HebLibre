# HebLibre Master Project Map

## Authority
- Repository: `brasilia736211600-netizen/HebLibre`
- Active branch: `genspark-dev`
- Default branch: `l10n_crowdin`
- GitHub is the source of truth.
- Control-plane docs override chat memory and unstated local state.

## Canonical workflow
`READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → REVIEW → COMMIT → SAVE STATE`

## Active product direction
`docs/HEBLIBRE_PRODUCT_SCOPE_2026-09-06.md` is the active scope baseline. HebLibre is a lightweight Android profile/workspace browser combining useful social-browser and leading anti-detect-browser patterns for legitimate privacy, multi-account organization, session continuity, diagnostics, and productivity.

The product does **not** include fraud-system bypass, identity-verification bypass, ban evasion, detection-evasion fingerprint spoofing, stealth bot behavior, or covert credential/session theft. Defensive observation, measurement, local test harnesses, and privacy diagnostics remain in scope.

## Competitive synthesis
Social Browser public feature reference: multi-tab/multi-user workflows, profile UA/proxy settings, import/export, password protection, multi-user site access, autofill/password tools, downloads, translation, zoom/sound, PDF, page editing, bookmarks, developer tools, session sharing, resource blocking, and permission/site-note/API/RSS concepts.

Anti-detect reference set used for the 2026 synthesis: Multilogin, GoLogin, AdsPower, Dolphin Anty, Octo Browser, Kameleo, Incogniton, MoreLogin, Undetectable, Nstbrowser. Recurring useful patterns: reusable isolated profiles, metadata/groups/tags/notes, persistent sessions/cookies, import/export, profile-specific settings, proxy association/status, automation/API integrations, profile storage, collaboration, and consistency diagnostics.

## Completed / current engineering
- Build/toolchain recovery, JUnit harness, CI workflow recovery.
- HTTPS-only, GPC, Desktop Mode, screenshot protection, search bangs, bounded media permission guard, third-party cookie control, geolocation guard, Save-Data, settings search, download-cookie privacy, profile-aware whitelists, tab reorder core, remote-content default consistency.
- Named profile metadata/catalog and manager UI.
- Profile-scoped app-owned HISTORY/BOOKMARK/TAB with migration and deletion purge.
- Profile session persistence and restore.
- Profile import/export with versioned format, AES-GCM option, PBKDF2, malformed-input/tamper/wrong-password rejection, and transactional record import.
- Curated profile-owned browser/privacy settings with compatibility bridge.
- Profile transfer of curated settings and encrypted transfer tests.
- WebView profile binding and profile-owned CookieManager path where supported.
- Profile Workspace improvements: search/filter/sort and explicit safe duplication/template semantics.
- Privacy & Storage diagnostics page and profile-scoped data clearing.
- Optional profile-local language configuration with validation; empty value preserves WebView default behavior.
- Runtime Smoke contains debug-only entry points for profile manager/transfer validation without exporting production activities.
- Runner diagnostics isolated a prior GitHub-hosted runner allocation problem; the latest source checkpoint has successfully reached a live Runtime Smoke runner and Gradle build.

## Existing browser baseline — do not reimplement
Multi-tab browsing, tab overview, Home/Bookmarks/History, search/autocomplete and configurable search engines, navigation gestures, find-in-page, PDF/print, downloads, fullscreen/video handling, JavaScript/Cookie/Remote/AdBlock controls with whitelists, Safe Browsing, bookmark import/export, custom User-Agent, clear-on-exit, AMOLED/pure-black theme, and related legacy browser behavior.

## Current architecture boundaries
- Complete profile-local WebView disk partitioning is not claimed beyond supported AndroidX WebKit APIs.
- Not all UI-only preferences are profile-owned; theme/toolbar/tab/gesture/filter settings require explicit ownership decisions.
- Per-profile proxy routing is not implemented. Do not use process-global `ProxyController` as a fake isolation mechanism.
- Full DoH/custom resolver and full WebRTC privacy remain architectural.
- Extension/script runtime is architectural until a real Android-compatible runtime is selected.

## Next feature wave
### P1 — Profile/workspace
- Finish profile metadata/groups/tags/notes and fast switcher integration in normal browsing flow.
- Profile health/consistency diagnostics.
- Site permission editor.
- Site-data viewer/clearer.
- Profile-aware session controls and transfer UX hardening.

### P1 — Privacy/social web
- Measurable popup/redirect/resource controls.
- Lightweight social-web conveniences: selection, previews, legally permitted downloads, translation/zoom/sound/PDF utilities, notes.
- Privacy status/fingerprint exposure audit as read-only observability.

### P2 — Developer/testing
- Page analyzer and API testing tools as diagnostics.
- Local defensive test page that exposes the signals visible to a site and helps validate detection logic without evasion.
- Optional developer tooling only where dependency and memory budgets remain acceptable.

### P3 — Architectural candidates
- Genuine per-profile network routing/proxies.
- Complete storage partitioning.
- DoH.
- Full WebRTC privacy architecture.
- Android-compatible extensions/script runtime.
- Multi-window/PWA/Reader Mode/QR features where justified.

## Performance gate
Every candidate feature must justify startup, RAM, storage, battery, dependency, and lifecycle cost. Avoid always-on services and large libraries when a small deterministic seam exists.

## Validation ladder
`SOURCE-VERIFIED → TEST-VERIFIED → CI-VERIFIED → ANDROID-RUNTIME-VERIFIED → DOCUMENTED`

Do not substitute one evidence level for another. Physical-device validation stays pending until the complete new product wave is assembled.

## Current CI/runtime checkpoint
Latest source fix commit: `60b5f8db367e83a98948708238e056a06a508df6` (`fix: import privacy rule constants from correct package`). Runtime Smoke run `34052412857` reached the hosted runner, completed runner setup and dependency setup, and was building the x86_64 smoke APK when last inspected. Final Runtime result and Unit Tests result must be recorded after completion; no physical-device claim is made here.

## Current next executable step
1. Finish and verify the current Runtime Smoke and Unit Tests results on the latest source checkpoint.
2. Download the exact x86_64 APK artifact and verify its published checksum.
3. Use one consolidated emulator run for the full current profile/privacy flow.
4. Continue the bounded P1 feature wave (permission editor → resource/popup controls → profile-switch integration) with deterministic tests.
5. Only after the complete planned feature checkpoint is assembled, perform the single physical-device validation pass.

## Last synchronized
2026-09-06 — updated product synthesis to explicitly combine Social Browser workflow patterns with the ten-product 2026 anti-detect reference set while replacing evasion requirements with defensive diagnostics/testing.
