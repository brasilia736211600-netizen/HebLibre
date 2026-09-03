# HebLibre Security Audit — 2026-09-03

## Scope
Bounded source audit only. No Android runtime work and no behavior change made.

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

## Next bounded work
Prefer deterministic, compatibility-preserving seams that can be covered by dependency-free JVM tests. Treat SSL override policy, backup semantics, and remote/file-origin policy coupling as explicit product or architecture decisions before changing runtime behavior.
