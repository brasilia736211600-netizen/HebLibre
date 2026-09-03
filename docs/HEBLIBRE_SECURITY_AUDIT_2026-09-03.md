# HebLibre Security Audit — 2026-09-03

## Scope
Bounded source audit with one concrete profile-isolation correction verified in the active code path. No Android runtime work.

## Finding: SSL certificate errors are user-overridable

`NinjaWebViewClient.onReceivedSslError()` presents the certificate-error dialog and calls `handler.proceed()` when the user selects OK. This means certificate trust failures can be bypassed interactively.

This is a security-policy decision rather than a deterministic compatibility-preserving fix: changing it to unconditional cancellation would improve transport integrity but could break sites/workflows that currently rely on the override path. No runtime change is made without an explicit product contract.

## Finding: remote-origin/file access controls are coupled

`NinjaWebView.initPreferences()` and `loadUrl()` use the same `sp_remote` setting to control:
- `setAllowFileAccessFromFileURLs`
- `setAllowUniversalAccessFromFileURLs`
- `setDomStorageEnabled`

The setting therefore controls both file-origin access and DOM storage. This coupling may be intentional legacy behavior, but separating the concerns would be an architectural change rather than a safe micro-fix. No change made.

## Finding: cleartext remains an application-level capability

`AndroidManifest.xml` sets `android:usesCleartextTraffic="true"`, while HTTPS-only navigation is opt-in. Removing cleartext at the application level would change compatibility for HTTP destinations. No change made.

## Finding: whitelist import/export profile mismatch was already corrected in the active path

An earlier source audit identified legacy `BrowserUnit.exportWhitelist()` / `importWhitelist()` methods that use `RecordUnit.DEFAULT_PROFILE_ID`. That finding does **not** represent the active settings transfer path.

### Active-path verification
- `ExportWhiteListTask` routes whitelist export (tables 0/1/2/3) through `ProfileScopedWhitelistTransfer.exportWhitelist()`.
- `ImportWhitelistTask` routes whitelist import (tables 0/1/2/3) through `ProfileScopedWhitelistTransfer.importWhitelist()`.
- `ProfileScopedWhitelistTransfer` resolves `ProfileIdentity.PREFERENCE_KEY` and normalizes it before all whitelist table reads and duplicate checks.
- Bookmark import/export remains on the existing `BrowserUnit` path and is intentionally unchanged.

Therefore there is **no new runtime patch required for this finding**. The earlier conclusion that this was the next concrete code change was based on inspecting the legacy helper without first tracing the active task call path.

### Verification status
- SOURCE-VERIFIED: yes — active transfer path is profile-aware and legacy default-profile methods are not used by the settings transfer tasks.
- TEST-VERIFIED: existing recorded profile/whitelist tests remain the applicable evidence; no new source change was required here.
- CI-VERIFIED: no new CI run is required solely for this audit correction because the active source behavior was already present in the verified code checkpoint.
- ANDROID-RUNTIME-VERIFIED: not yet; remains deferred to final consolidated device validation.

## Next bounded work
Continue source verification for deterministic, compatibility-preserving seams. Treat SSL override policy, backup semantics, and remote/file-origin policy coupling as explicit product or architecture decisions before changing runtime behavior.
