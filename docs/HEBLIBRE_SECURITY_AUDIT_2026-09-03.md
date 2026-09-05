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

## Finding: whitelist import/export profile mismatch

A legacy pair of `BrowserUnit.exportWhitelist()` / `importWhitelist()` methods used `RecordUnit.DEFAULT_PROFILE_ID` for whitelist queries. The active settings transfer path was already correctly routed through `ProfileScopedWhitelistTransfer`, which resolves `ProfileIdentity.PREFERENCE_KEY` before table reads and duplicate checks.

To remove the inconsistency for any remaining legacy callers, the legacy `BrowserUnit` methods were updated to resolve the active normalized profile and pass that profile to all four whitelist tables. The default profile remains the fallback when no profile is stored.

### Verification status
- SOURCE-VERIFIED: yes — the commit diff is limited to active-profile resolution and profile arguments in the two legacy transfer helpers.
- TEST-VERIFIED: existing `ProfileScopedWhitelistTransferTest` covers null, blank, trimmed, and preserved profile-id normalization.
- CI-VERIFIED: Unit Tests run `33985143542`, head `247768c4e2e442fcb9b42d299d8cf00d3c24b81b`, completed successfully; the `test` job and `Run unit tests` step both completed successfully.
- ANDROID-RUNTIME-VERIFIED: not yet; remains deferred to final consolidated device validation.

## Finding: download-cookie bypass status

The audit previously identified `HelperUnit.save_as()` as a second download path that could bypass the download-cookie policy. Current source has already been updated in both SDK branches so the `Cookie` request header is added only when `DownloadCookiePolicy` permits it. The main `BrowserUnit.download()` path is likewise policy-gated.

### Verification status
- SOURCE-VERIFIED: current `HelperUnit.save_as()` and `BrowserUnit.download()` both consult `send_download_cookies` before forwarding the cookie header.
- CI-VERIFIED: the relevant download-cookie integration/unit-test run `33692045747` completed successfully; the currently consolidated branch test run `33985143542` also completed successfully.
- ANDROID-RUNTIME-VERIFIED: not yet; remains deferred to final consolidated device validation.

## Official Android cross-checks

- Android Network Security Configuration: cleartext traffic defaults to disabled for apps targeting Android 9 / API 28+ unless explicitly enabled or otherwise configured: https://developer.android.com/privacy-and-security/security-config
- `usesCleartextTraffic`: WebView honors the application's cleartext policy for targets API 26+: https://developer.android.com/reference/android/security/NetworkSecurityPolicy.html
- WebView security guidance documents safer file-origin settings and warns about untrusted WebView content: https://developer.android.com/reference/androidx/webkit/WebViewAssetLoader and https://developer.android.com/privacy-and-security/security-tips
- Android application backup guidance supports selective `dataExtractionRules`/backup configuration for sensitive app data: https://developer.android.com/guide/topics/manifest/application-element

## Next bounded work
Prioritize deterministic source/UX work that can be validated without changing unresolved security contracts. Keep SSL override, global cleartext policy, backup semantics, and `sp_remote` file-origin coupling as explicit product/architecture decisions until their contracts are approved.
