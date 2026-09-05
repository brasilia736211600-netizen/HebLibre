# HebLibre Agent Context — Current State

## Repository
`brasilia736211600-netizen/HebLibre` — active branch `genspark-dev`.

## Current live checkpoint
`ae5c6d7fda37de4ba33e3b20f01abf6599c57ec6` — WebKit 1.9.0 + Android 33 CI toolchain alignment.

## Previous application-source checkpoints
- `7520c0326f88e1cb99764b699eeb62297b873489` — WebKit 1.9.0 with compileSdk/build-tools 33 alignment.
- `cab285b5abea743593a4f0813d9ee8f099c642a5` — active-profile WebView binding wired immediately after `super(context)`.
- `a9677287744bf80bf06626522a8e0831d04e9d9b` — capability-checked WebView profile-binding seam.
- `247768c4e2e442fcb9b42d299d8cf00d3c24b81b` — profile-aware whitelist import/export correction.

## Work completed in this session
- Added `ProfileMetadata`, a dependency-free reusable profile metadata contract with stable profile id ownership, name, color, icon, notes, tags, and group.
- Added JVM tests covering metadata normalization, required fields, optional fields, duplicate/blank tag handling, and immutability.
- Evaluated WebKit `1.14.0`; CI proved the existing legacy build toolchain could not consume that dependency cleanly because of the newer annotation-experimental dependency path.
- Reconciled the dependency spike to `androidx.webkit:webkit:1.9.0`, the first stable multi-profile release, and aligned the CI Android SDK installation plus app compileSdk/build-tools to Android 33/33.0.2 while preserving `minSdkVersion 21` and `targetSdkVersion 29`.
- Added `WebViewProfileBindingPolicy` and `WebViewProfileBinder`. The binder checks `MULTI_PROFILE`, preserves legacy behavior for the default profile, and binds only named profiles through `WebViewCompat.setProfile()`.
- Wired `WebViewProfileBinder.bindActiveProfile(context, this)` immediately after `super(context)` in `NinjaWebView(Context)`.
- Added deterministic tests for the binding policy.
- Created and synchronized this `docs/AGENT_CONTEXT/` control plane. The earlier GitHub commits `2ebf571...` and `4557891...` contained context files but were not ancestors of the live branch; current-branch context is authoritative.

## Evidence
- SOURCE-VERIFIED: current profile metadata, WebKit dependency, WebView binding seam, CI toolchain, and context files are present on `genspark-dev`.
- TEST-VERIFIED: pending the latest `Unit Tests` run after the toolchain alignment.
- CI-VERIFIED: the `1.14.0` run failed; its failure drove the bounded downgrade to `1.9.0`. The new `1.9.0` CI run is in progress.
- ANDROID-RUNTIME-VERIFIED: not claimed; the current runtime-smoke runs are intermediate build validation, not final consolidated device validation.
- DOCUMENTED: yes, current context is synchronized to the latest live HEAD.

## Architecture findings
AndroidX WebKit 1.9.0 introduced the stable WebView multi-profile API: `WebViewCompat.setProfile()` binds a WebView to a named Profile and `ProfileStore` manages available profiles. Profile-specific CookieManager/WebStorage/ServiceWorker-related state is available through the Profile API, while legacy static WebView APIs such as `CookieManager.getInstance()` continue to address the default profile.

The current application constructs `NinjaWebView` directly. Binding is now placed immediately after WebView construction and before WebView configuration/navigation. The binder is capability-checked, so unsupported WebView implementations retain legacy behavior and no isolation claim is made.

The build baseline is intentionally conservative: `minSdkVersion 21`, `targetSdkVersion 29`, `compileSdkVersion 33`, WebKit 1.9.0. Do not raise targetSdk or minSdk as a side effect of profile work.

## Deferred policy decisions
Do not change SSL certificate override semantics, application cleartext policy, automatic backup semantics, or the coupling of file-origin access with DOM storage without an explicit product/architecture decision.

Do not fake profile-local proxying with process-global `ProxyController` behavior.

Do not add profile switcher UI, storage migration, or profile-specific cookie/storage cleanup until the binding/build contract is CI-verified.

## Next executable action
Wait only for the current CI evidence needed to validate the WebKit 1.9.0/toolchain change. If CI passes, inspect the final diff and then implement the minimal profile-switch/rebind lifecycle contract; if it fails, diagnose from CI logs before any further feature work.
