# HebLibre Security Audit — 2026-09-03

## Scope
Bounded source audit with one concrete profile-isolation correction. No Android runtime work.

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

## Finding: whitelist import/export was hard-coded to the default profile

`BrowserUnit.exportWhitelist()` and `BrowserUnit.importWhitelist()` query/check whitelist tables with `RecordUnit.DEFAULT_PROFILE_ID` rather than the active `ProfileIdentity` value. This was inconsistent with the profile-scoped persistence introduced for whitelist tables.

### Correction
A profile-aware transfer entry point, `ProfileScopedWhitelistTransfer`, now derives the active profile through `ProfileIdentity` and uses that profile id for whitelist export/import table reads and duplicate checks. `ExportWhiteListTask` and `ImportWhitelistTask` route their whitelist operations through this entry point. Existing bookmark import/export behavior is unchanged.

A small dependency-free test seam was added through `ProfileScopedWhitelistTransfer.normalizeProfileId(String)` so the profile-normalization contract can be verified on the JVM without constructing Android `Context` objects.

### Verification status
- SOURCE-VERIFIED: yes — active-profile resolution, profile-filtered CRUD, task routing, and the pure normalization seam are present.
- TEST-VERIFIED: pending CI execution for the current source revision.
- CI-VERIFIED: pending for the current source revision.
- ANDROID-RUNTIME-VERIFIED: not yet; remains deferred to final consolidated device validation.

## Next bounded work
Verify the new profile-aware transfer path in CI, then continue source verification for deterministic compatibility-preserving seams. Treat SSL override policy, backup semantics, and remote/file-origin policy coupling as explicit product or architecture decisions before changing runtime behavior.
