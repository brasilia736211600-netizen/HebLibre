# HebLibre Agent Context — Current State

## Repository
`brasilia736211600-netizen/HebLibre` — active branch `genspark-dev`.

## Current live checkpoint
`a9677287744bf80bf06626522a8e0831d04e9d9b` — capability-checked WebView profile-binding seam.

## Previous application-source checkpoints
- `12c237e0ab9832adb509889c1d1202cd73f2e8bf` — AndroidX WebKit dependency evaluation (`1.14.0`).
- `247768c4e2e442fcb9b42d299d8cf00d3c24b81b` — profile-aware whitelist import/export correction.

## Work completed in this session
- Added `ProfileMetadata`, a dependency-free reusable profile metadata contract with stable profile id ownership, name, color, icon, notes, tags, and group.
- Added JVM tests covering metadata normalization, required fields, optional fields, duplicate/blank tag handling, and immutability.
- Upgraded AndroidX WebKit from `1.3.0-alpha03` to `1.14.0` as the compatibility candidate while preserving `minSdkVersion 21`.
- Added `WebViewProfileBindingPolicy` and `WebViewProfileBinder`. The binder checks `MULTI_PROFILE`, preserves legacy behavior for the default profile, and binds only named profiles through `WebViewCompat.setProfile()`.
- Added deterministic tests for the binding policy.
- Created this `docs/AGENT_CONTEXT/` control plane on the active branch; the earlier GitHub commits `2ebf571...` and `4557891...` contained the same context files but were not ancestors of the live branch, so current-branch context is authoritative.

## Evidence
- SOURCE-VERIFIED: current profile metadata, WebKit dependency, binding policy/binder, and context files are present on `genspark-dev`.
- TEST-VERIFIED: pending the current GitHub Actions run for the latest source checkpoint.
- CI-VERIFIED: pending. The prior `1.14.0` build/test run is still in progress and predates the binder commit.
- ANDROID-RUNTIME-VERIFIED: not claimed; final consolidated validation only.
- DOCUMENTED: yes, current context is synchronized to the latest live HEAD.

## Architecture findings
AndroidX WebKit multi-profile support exists from WebKit `1.9.0`; `WebViewCompat.setProfile()` associates a WebView with a named Profile and `ProfileStore` manages profiles. The current application constructs `NinjaWebView` directly. Binding must occur immediately after WebView construction and before WebView configuration/navigation.

`androidx.webkit:webkit:1.14.0` is the selected compatibility candidate for the current `minSdkVersion 21` baseline. WebKit `1.15.0` raises minSdk to 23; do not upgrade past 1.14.x without an explicit support-floor decision.

The binding seam is intentionally not yet wired into `NinjaWebView`; the next source mutation must add exactly that early-constructor call, then verify build/tests before broader lifecycle work. Until it is wired, no runtime profile isolation is claimed.

## Deferred policy decisions
Do not change SSL certificate override semantics, application cleartext policy, automatic backup semantics, or the coupling of file-origin access with DOM storage without an explicit product/architecture decision.

Do not fake profile-local proxying with process-global `ProxyController` behavior.

## Next executable action
Wire `WebViewProfileBinder.bindActiveProfile(context, this)` immediately after `super(context)` in the `NinjaWebView(Context)` constructor, then run the affected CI/build checks and inspect the integrated diff before adding any profile switcher UI or storage migration.
