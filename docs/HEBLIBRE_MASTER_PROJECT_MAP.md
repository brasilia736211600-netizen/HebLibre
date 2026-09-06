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
`docs/HEBLIBRE_PRODUCT_SCOPE_2026-09-06.md` is the active scope baseline. HebLibre is a lightweight Android profile/workspace browser combining useful social-browser and leading anti-detect-browser patterns for legitimate privacy, multi-account organization, session continuity, diagnostics, productivity, and authorized defensive security testing.

General-purpose fraud-system bypass, identity-verification bypass, ban evasion, detection-evasion fingerprint spoofing, stealth bot behavior, and covert credential/session theft are not product features. The security-testing objective is implemented through an isolated authorized red-team laboratory model rather than a portable third-party evasion engine.

## Competitive synthesis
Social Browser public feature reference: multi-tab/multi-user workflows, profile UA/proxy settings, import/export, password protection, multi-user site access, autofill/password tools, downloads, translation, zoom/sound, PDF, page editing, bookmarks, developer tools, session sharing, resource blocking, and permission/site-note/API/RSS concepts.

Anti-detect reference set used for the 2026 synthesis: Multilogin, GoLogin, AdsPower, Dolphin Anty, Octo Browser, Kameleo, Incogniton, MoreLogin, Undetectable, Nstbrowser. Recurring useful patterns: reusable isolated profiles, metadata/groups/tags/notes, persistent sessions/cookies, import/export, profile-specific settings, proxy association/status, automation/API integrations, profile storage, collaboration, and consistency diagnostics. These are adopted as privacy/workflow requirements, not as instructions for third-party detection evasion.

## Current engineering state
- Existing privacy/browser baseline: HTTPS-only, GPC, Desktop Mode, screenshot protection, search bangs, bounded media permission guard, third-party cookie control, geolocation guard, Save-Data, settings search, download-cookie privacy, profile-aware whitelists, tab reorder, remote-content default consistency.
- Profile metadata/catalog and manager UI.
- Profile-scoped app-owned HISTORY/BOOKMARK/TAB with migration and deletion purge.
- Profile session persistence/restore.
- Versioned profile import/export with AES-GCM option, PBKDF2, malformed/tamper/wrong-password rejection, transactional record import.
- Curated profile-owned browser/privacy settings with compatibility bridge.
- Profile workspace search/filter/sort, duplication/template semantics.
- Privacy & Storage diagnostics and profile-scoped clearing.
- Profile-local language configuration with validation.
- Profile-local site permission policy/store/editor with deny rules and cleanup on profile deletion; global media/location guards remain authoritative.
- Read-only local Fingerprint Exposure Audit harness.
- Runtime Smoke has debug-only profile manager/transfer validation paths without exporting production activities.
- GitHub-hosted runner allocation issue was isolated; later runs reached real hosted runners and the Android build path.

## Feature synthesis disposition
### Implement / extend
Profiles, metadata, grouping, tags, notes, fast switching, profile-local data/session/settings, secure transfer, permission editor, popup/redirect/resource controls, social-web utilities, download/bookmark/history tooling, developer diagnostics, Privacy & Fingerprint Exposure Audit, and authorized red-team testing against operator-owned endpoints.

### Architectural
Genuine per-profile proxy/request routing, complete WebView storage partitioning, DoH/custom resolver, full WebRTC privacy architecture, Android-compatible extension/script runtime, multi-window/PWA/Reader Mode/QR where justified.

### Excluded
Third-party fraud/verification bypass, ban evasion, detection-evasion fingerprint spoofing, stealth behavioral automation, and credential/session theft.

## Authorized red-team laboratory
The security-research track is separated from the general browser surface. The lab can define controlled scenarios, vary test signals against an owned/authorized endpoint, assert detector outcomes, capture evidence, and maintain regression cases for the user's future site/platform. It must not expose a portable arbitrary-site evasion switch.

## Next feature wave
1. Finish current Runtime Smoke and Unit Tests on the latest branch head.
2. Verify/download the exact x86_64 smoke artifact and checksum.
3. Run one consolidated emulator pass for profile, site-permission, transfer and fingerprint-audit flows.
4. Continue P1 with site-data viewer/clearer and measurable resource/popup controls.
5. Add the authorized red-team scenario engine after the production privacy/profile path is green.
6. Perform the single physical-device validation pass only after the coherent feature checkpoint.

## Performance gate
Every added feature must justify startup, RAM, storage, battery, dependency, and lifecycle cost. Avoid always-on services and large libraries when a small deterministic seam exists.

## Validation ladder
`SOURCE-VERIFIED → TEST-VERIFIED → CI-VERIFIED → ANDROID-RUNTIME-VERIFIED → DOCUMENTED`

No physical-device verification is claimed until that validation is actually performed.

## Last synchronized
2026-09-06 — updated to retain the requested security-testing objective through a controlled authorized red-team laboratory while keeping the general-purpose browser free of portable third-party anti-fraud/verification evasion.
