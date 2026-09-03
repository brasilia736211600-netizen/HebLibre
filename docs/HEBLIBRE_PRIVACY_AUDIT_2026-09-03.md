# HebLibre Privacy Audit — 2026-09-03

## Scope
Bounded source audit only. No Android runtime work and no architectural subsystem changes.

## Finding: third-party cookie default contract needs an explicit product decision

### Evidence
- `ThirdPartyCookiePolicy.acceptThirdPartyCookies(boolean)` correctly maps the explicit `blockThirdPartyCookies` flag to WebView acceptance (`!blockThirdPartyCookies`).
- `NinjaWebView.applyThirdPartyCookiePolicy()` currently reads `block_third_party_cookies` with fallback `false`, preserving compatibility when the preference key is absent.
- `preference_start.xml` does not currently expose `block_third_party_cookies` as a user-facing preference.
- Existing cookie settings copy in `strings.xml` describes third-party cookies as always disabled, which is stronger than the current implementation contract.

### Decision
Do **not** change the runtime default in this audit. A default-true change would be a behavior change relative to the current compatibility contract and requires an explicit product decision plus UI/resource updates and CI verification.

### Status
SOURCE-VERIFIED finding. No behavior change made.

## Finding: automatic backup contains browser database

`backup_descriptor.xml` explicitly includes `Ninja4.db`, and `RecordHelper` confirms that `Ninja4.db` contains history, bookmarks, tabs, and whitelist tables. This is privacy-relevant but changing backup semantics would affect restore expectations, so it remains an architectural/product decision rather than an opportunistic patch.

## Finding: cleartext traffic is enabled at the application level

`AndroidManifest.xml` sets `android:usesCleartextTraffic="true"`. HTTPS-only navigation is an opt-in feature, so removing cleartext globally would be a compatibility change affecting HTTP navigation. No change made.

## Next bounded work
Continue source verification for deterministic, compatibility-preserving privacy/UX seams. Do not convert product-policy findings into runtime changes without an explicit contract.
