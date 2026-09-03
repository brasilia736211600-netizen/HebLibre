# HebLibre Security Audit — 2026-09-03

## Scope
Bounded source audit with targeted official Android security cross-checks. No Android runtime work.

## Finding: SSL certificate errors are user-overridable

`NinjaWebViewClient.onReceivedSslError()` presents the certificate-error dialog and calls `handler.proceed()` when the user selects OK. This means certificate trust failures can be bypassed interactively.

This is a security-policy decision rather than a deterministic compatibility-preserving fix: changing it to unconditional cancellation would improve transport integrity but could break sites/workflows that currently rely on the override path. No runtime change is made without an explicit product contract.

## Finding: remote-origin/file access controls are coupled

`NinjaWebView.initPreferences()` and `loadUrl()` use the same `sp_remote` setting to control:
- `setAllowFileAccessFromFileURLs`
- `setAllowUniversalAccessFromFileURLs`
- `setDomStorageEnabled`

Official Android guidance treats file-origin access as security-sensitive and shows `setAllowFileAccessFromFileURLs(false)` and `setAllowUniversalAccessFromFileURLs(false)` as the safer configuration; the latter is deprecated from API 30. The project targets SDK 29, so this remains a design-level review item rather than an opportunistic API migration. No runtime change made.

## Finding: cleartext remains an application-level capability

`AndroidManifest.xml` sets `android:usesCleartextTraffic="true"`, while the project target SDK is 29 and HTTPS-only navigation is separately opt-in. Current Android guidance says cleartext is disabled by default for apps targeting Android 9 / API 28+ and recommends Network Security Config for explicit domain exceptions. The manifest attribute therefore deliberately broadens cleartext capability beyond the platform default for this target level.

Removing it globally could change compatibility for HTTP destinations, so this remains a product/security decision. No change made.

## Finding: automatic backup contains browser database

`backup_descriptor.xml` explicitly includes `Ninja4.db`. The browser database contains history, bookmarks, tabs, and whitelist data. Automatic backup is therefore privacy-relevant. Android guidance supports retaining backup while excluding sensitive data with backup rules, but changing this project's backup contract can affect restore continuity and user expectations. No change made.

## Finding: whitelist import/export profile mismatch was already corrected in the active path

An earlier source audit identified legacy `BrowserUnit.exportWhitelist()` / `importWhitelist()` methods that use `RecordUnit.DEFAULT_PROFILE_ID`. That finding does **not** represent the active settings transfer path.

### Active-path verification
- `ExportWhiteListTask` routes whitelist export (tables 0/1/2/3) through `ProfileScopedWhitelistTransfer.exportWhitelist()`.
- `ImportWhitelistTask` routes whitelist import (tables 0/1/2/3) through `ProfileScopedWhitelistTransfer.importWhitelist()`.
- `ProfileScopedWhitelistTransfer` resolves `ProfileIdentity.PREFERENCE_KEY` and normalizes it before whitelist table reads and duplicate checks.
- Bookmark import/export remains on the existing `BrowserUnit` path and is intentionally unchanged.

Therefore there is **no new runtime patch required for this finding**. The earlier conclusion that this was the next concrete code change was corrected after tracing the actual settings task call path.

### Verification status
- SOURCE-VERIFIED: yes — active transfer path is profile-aware and the legacy default-profile helpers are not used by the settings transfer tasks.
- TEST-VERIFIED: existing recorded profile/whitelist tests remain the applicable evidence; no new source change was required here.
- CI-VERIFIED: current HEAD Unit Tests run `33703506073` completed successfully.
- ANDROID-RUNTIME-VERIFIED: not yet; remains deferred to final consolidated device validation.

## Finding: download cookie policy has a second download path that bypasses it

The main `BrowserUnit.download()` path now gates the `Cookie` request header behind `DownloadCookiePolicy` and the `send_download_cookies` preference. However, `HelperUnit.save_as()` constructs `DownloadManager.Request` directly and still unconditionally adds `CookieManager.getInstance().getCookie(url)` as a request header.

This creates a concrete policy bypass: disabling download-cookie forwarding does not guarantee that the alternate "Save as" download path stops sending cookies.

This is a bounded correctness/privacy issue and is a higher-value runtime candidate than the already-resolved whitelist audit item. The safest fix is to route this second path through the same cookie-forwarding policy without changing the existing Save As UI or download destination behavior, then add deterministic policy coverage where practical and run CI before any Android runtime validation.

## Official Android cross-checks

- Android Network Security Configuration: cleartext traffic defaults to disabled for apps targeting Android 9 / API 28+ unless explicitly enabled or otherwise configured: https://developer.android.com/privacy-and-security/security-config
- `usesCleartextTraffic`: WebView honors the application's cleartext policy for targets API 26+: https://developer.android.com/reference/android/security/NetworkSecurityPolicy.html
- WebView security guidance documents safer file-origin settings and warns about untrusted WebView content: https://developer.android.com/reference/androidx/webkit/WebViewAssetLoader and https://developer.android.com/privacy-and-security/security-tips
- Android application backup guidance supports selective `dataExtractionRules`/backup configuration for sensitive app data: https://developer.android.com/guide/topics/manifest/application-element

## Next bounded work
Prioritize closing the `HelperUnit.save_as()` download-cookie policy bypass with the smallest shared-policy seam. Keep SSL override, global cleartext policy, backup semantics, and `sp_remote` file-origin coupling as explicit product/architecture decisions until their contracts are approved.
