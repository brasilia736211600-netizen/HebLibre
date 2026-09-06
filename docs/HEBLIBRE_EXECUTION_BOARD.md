# HebLibre Persistent Execution Board

## Objective
Durable agent-neutral task board. GitHub is the source of truth; the board records what is done, pending, architectural, excluded, and next.

## Status keys
- `DONE-SOURCE`: source verified.
- `DONE-TEST`: deterministic tests verified.
- `DONE-CI`: CI verified.
- `DONE-EMULATOR`: Android emulator runtime verified.
- `DONE-PHYSICAL`: physical-device verified.
- `PARTIAL`: core exists but end-to-end feature is incomplete.
- `RESEARCH`: researched but not implemented.
- `ARCHITECTURAL`: requires design checkpoint.
- `DEFERRED`: intentionally postponed.
- `POLICY-DECISION`: do not silently reopen.
- `EXCLUDED`: outside product scope/safety boundary.

## Current phase
`2026 Social/Profile Browser Synthesis → P1 bounded implementation → consolidated CI/emulator validation`

## Completed foundation
Existing privacy and browser improvements remain at their recorded verification levels: tracking/query cleanup, HTTPS-only, GPC, Desktop Mode, screenshot protection, search bangs, bounded media permission guard, third-party cookie control, geolocation guard, Save-Data, settings search, download-cookie privacy, profile-aware whitelist transfer, tab reorder core, and remote-content default consistency.

Profile foundation is source-complete for named metadata/catalog, profile binding, profile-owned CookieManager path where supported, profile-scoped app-owned records, session restore, secure transfer/import/export, curated profile preferences, profile search/filter/sort, explicit profile duplication semantics, and Privacy & Storage diagnostics. Current code includes optional profile-local language configuration with validation.

## Updated feature synthesis
### Implement / extend
- Fast profile switching integrated into normal browsing flow.
- Profile name/color/icon/groups/tags/notes.
- Profile health/consistency diagnostics.
- Site permission editor.
- Site-data viewer/clearer.
- Measurable resource and popup/redirect controls.
- Lightweight social-web utilities: selection, preview, permitted downloads, translation/zoom/sound, PDF/print, notes.
- Developer/page diagnostics and API testing.
- Read-only privacy/fingerprint exposure audit and local defensive test harness.

### Architectural
- Genuine per-profile proxy/request routing.
- Complete WebView disk/storage partitioning beyond supported APIs.
- DoH/custom resolver.
- Full WebRTC privacy architecture.
- Android-compatible extension/script runtime.
- Multi-window/PWA/Reader Mode/QR where justified.

### Excluded
- Fraud-system bypass, identity-verification bypass, ban evasion.
- Detection-evasion fingerprint spoofing or stealth behavioral automation.
- Credential theft or covert session sharing.

## Immediate execution sequence
1. Finish current Runtime Smoke and Unit Tests on source checkpoint `60b5f8db367e83a98948708238e056a06a508df6`.
2. Download and checksum-verify the exact x86_64 APK artifact.
3. Run one consolidated emulator validation of current browser/profile flows.
4. Implement the Site Permission Editor against a profile-aware deterministic store.
5. Extend resource/popup controls using the existing interception/navigation seams.
6. Integrate fast profile switching into BrowserActivity without breaking restart semantics.
7. Add the read-only Fingerprint Exposure Audit/local diagnostic harness.
8. Re-run deterministic tests and consolidated Runtime Smoke.
9. Only when the full feature checkpoint is coherent, perform the single physical-device pass.

## Validation policy
Do not use repeated APK installation as the development loop. Physical-device testing remains `PENDING` until the new product wave is complete. Distinguish source, test, CI, emulator, artifact, and physical evidence.

## Performance gate
Every added feature must justify startup, RAM, storage, battery, dependency, and lifecycle cost. Prefer current Android/WebView APIs and small deterministic seams. Reject heavy always-on services and speculative frameworks.

## Continuity rule
After every material step, update exact HEAD, task, decisions, changed/unchanged files, evidence, blockers, and next executable step.

## Last synchronized
2026-09-06 — refreshed from Social Browser public GitHub feature inventory and ten-product 2026 anti-detect comparison; evasion capabilities were replaced by legitimate privacy/diagnostic requirements.
