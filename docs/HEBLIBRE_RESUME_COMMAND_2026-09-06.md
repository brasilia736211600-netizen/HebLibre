# HebLibre Resume Command — 2026-09-06

Copy/paste the block below into a new agent/chat when continuity is lost.

```text
@GitHub @Thinking

Resume HebLibre from GitHub only. Do NOT rely on chat memory, prior-agent memory, plugin memory, or unstated local state.

Repository: brasilia736211600-netizen/HebLibre
Active branch: genspark-dev
Default branch: l10n_crowdin

READ first, in this order:
1. docs/HEBLIBRE_WORKFLOW_STATE.md
2. docs/HEBLIBRE_SECURITY_AUDIT_2026-09-03.md
3. docs/HEBLIBRE_RESUME_COMMAND_2026-09-06.md

Then VERIFY directly from GitHub:
- current branch HEAD
- current diff against clean implementation checkpoint 5fffd65e80e2a616a5273befe7cdca6309441490
- last commits
- relevant CI runs and their exact head SHAs
- no unverified runtime change was introduced

Use this execution protocol exactly:
READ -> VERIFY -> RECONCILE -> PLAN -> EXECUTE -> TEST -> DIFF -> REVIEW -> COMMIT -> SAVE STATE

Never conflate verification levels:
SOURCE-VERIFIED
TEST-VERIFIED
CI-VERIFIED
ANDROID-RUNTIME-VERIFIED
DOCUMENTED

Current engineering baseline:
- Clean implementation checkpoint: 5fffd65e80e2a616a5273befe7cdca6309441490
- Branch currently contains documentation/audit commits after that checkpoint.
- Existing verified work includes P2.1-P2.11, download-cookie privacy, tab reorder core, remote-content default consistency, and profile-scoped whitelist persistence.
- Android runtime validation is deferred until the final consolidated device-validation phase.

NEW PRODUCT/ARCHITECTURE REQUIREMENTS TO INCORPORATE:

A) Real profile/container isolation
Each browser profile/container must have its own browser identity and state, including:
- cookies/session state
- web storage and service-worker state where supported
- history/bookmarks/tabs or other app-owned records according to the chosen isolation contract
- whitelist state
- user-agent profile
- proxy configuration or an explicitly documented limitation if the platform cannot provide true per-profile proxying
- profile-specific privacy/security settings

Prefer AndroidX WebKit multi-profile support over a home-grown imitation when supported. Profile isolation must be feature-detected and must not silently claim isolation where the runtime does not support it.

B) Account/login-cookie management
Provide a first-class per-profile cookie/session vault for the user's own accounts.
Required UX:
- inspect/import/export cookies for the active profile
- import user-provided cookie data into the selected profile
- capture/backup the active profile's cookie state using supported WebView APIs; do not scrape or steal credentials from other apps
- secure storage for exported/imported session material
- explicit confirmation before exporting authentication material
- never log cookie values, Authorization headers, passwords, or session tokens
- support a portable browser-owned cookie format with at least domain, path, name, value, expiry, secure, HttpOnly, SameSite and partition-related fields when the platform/API exposes them
- clearly distinguish cookies that cannot be exported or replayed exactly because of platform/site security constraints

C) Proxy
The product requirement is per-profile/per-container proxy configuration with:
- HTTP/HTTPS/SOCKS forms where supported
- host, port, optional credentials handled securely
- bypass rules
- enable/disable and direct mode
- validation/test state before activation

Do NOT implement fake per-tab/per-profile proxying using a single global WebView proxy. AndroidX WebKit ProxyController is process-scoped and applies to all WebViews in the app. Treat true per-profile proxying as an architecture decision requiring a verified solution (for example, process-level isolation/tunneling) before implementation.

D) Professional User-Agent management
Each profile/container can use:
- system/default WebView UA
- custom UA
- curated presets for major desktop/mobile operating systems and major browser families/versions
- logically consistent UA metadata/client hints where supported
- editable custom preset
- validation to avoid contradictory combinations

Preset catalog must use real, current, internally consistent platform/browser/version combinations. Do not invent impossible UA strings merely to enlarge the list.

E) Time policy
Each profile/container needs a time policy:
- device time (default)
- explicit timezone selection
- proxy/network-derived time as an optional informational or policy source when technically available
- never pretend that changing browser timezone changes the Android system clock
- keep language/locale/timezone behavior explicit and internally consistent

F) Same-URL profile/container switching
Core UX requirement:
- while viewing URL U in profile/container A, user can switch to B
- the current URL U is retained
- the active WebView/profile context is switched
- U is reloaded under B's cookies/storage/UA/proxy/privacy state
- switching must not mutate A's state into B's state
- preserve back/forward semantics only where the selected profile has a valid history stack
- switching should be fast and visually light

Design this as a controlled navigation/reload operation, not as mutation of an existing WebView into another identity unless the underlying WebView API explicitly supports it safely.

G) Import/export hierarchy
Provide two levels:
1. Profile/container export/import
2. Whole-browser export/import

Profile/container bundle should define a versioned manifest and include, according to the isolation contract:
- profile metadata/settings
- app-owned tabs/session metadata
- history/bookmarks where scoped
- whitelist data
- cookies/session data where supported
- UA preset/custom UA
- proxy configuration with credentials protected or separately encrypted
- timezone/time policy
- other profile-owned settings

Whole-browser export/import should include all profile/container bundles plus global browser settings and a versioned manifest.

Never export secrets by accident. Sensitive fields need an explicit opt-in and protected representation. Imports must validate schema/version, reject malformed entries safely, and avoid partial destructive replacement by default.

H) Modern lightweight UI
UI goals:
- modern, compact, responsive
- low memory and low view-hierarchy overhead
- profile/container switcher always easy to reach
- current profile identity visible without consuming excessive space
- quick switcher supports search/favorite/pinned profiles where useful
- clear active-state indicator
- no large framework added solely for cosmetic UI
- preserve current browser's stable interaction patterns unless a measured UX problem justifies change

I) Development/workflow requirements
For every new subsystem:
- define an explicit contract before implementation
- create dependency-free JVM policy tests first where practical
- implement the smallest vertical slice
- verify source and tests
- run CI before considering it complete
- review diff against the clean checkpoint and reject unrelated churn
- update workflow state after every completed bounded slice
- document unsupported/partial behavior instead of silently approximating it
- no repeated APK installs; reserve Android runtime for consolidated final validation

PARALLEL WORK RULE:
Work independent bounded streams in parallel where safe:
1. profile/container data model + isolation contract
2. cookie/session import/export format and policy
3. UA catalog/policy
4. proxy capability audit/architecture
5. time policy
6. profile switch/reload UX contract
7. import/export manifest contract
8. lightweight UI information architecture

Serialize all dependent branch/file mutations.
Do not start a large architectural rewrite until the capability matrix and contracts are verified.

YAGNI:
Do not add QR scanner, PWA, Reader Mode, true tab hierarchy, extensions/uBlock, DoH, broad fingerprinting defenses, full WebRTC privacy, per-container Tor, or other deferred features merely because they are technically interesting. Re-evaluate only when the current product contract requires them.

IMPORTANT SECURITY RULE:
Cookie/session material is equivalent to authentication material. Handle only data explicitly supplied by or owned by the user. Never implement credential theft, cross-app cookie extraction, hidden exfiltration, or logging of authentication secrets.

When finished with a bounded slice:
- report exact files changed
- report source/test/CI status separately
- report exact commit SHA
- update docs/HEBLIBRE_WORKFLOW_STATE.md
- state exactly one next highest-value action
```

## Technical direction confirmed by current platform research

AndroidX WebKit provides a `Profile` abstraction representing a WebView browsing session with separate data, and `WebViewCompat.setProfile()` can associate a WebView with a named profile when the `MULTI_PROFILE` feature is supported. The profile owns profile-specific cookie manager, WebStorage, service-worker controller and related browsing state. This is the preferred foundation for true browser-profile isolation rather than continuing to emulate isolation only through app-owned tables. Source: Android Developers, WebView/Profile/WebViewCompat documentation.

The repository currently declares `androidx.webkit:webkit:1.3.0-alpha03`, so adopting the multi-profile APIs requires an explicit dependency-compatibility assessment rather than an immediate blind upgrade. AndroidX WebKit 1.15.0 adds newer cookie/request APIs but raises minSdk from 21 to 23; therefore a dependency target such as 1.14.x should be evaluated first if API 21 support remains a hard requirement.

Per-profile proxying is not provided by `ProxyController`: its `setProxyOverride()` configuration applies to all WebViews in the app and is process-specific. Therefore the product requirement must not be implemented as a pretend profile-local proxy. A real solution needs a verified isolation/tunneling architecture, or the UI must explicitly label proxy scope as process-wide.

User-Agent customization is directly supported by WebView, while newer AndroidX WebKit APIs can also override User-Agent metadata/client hints. UA presets therefore need both string-level and metadata-level consistency testing.

## Sources
- https://developer.android.com/reference/androidx/webkit/Profile
- https://developer.android.com/reference/androidx/webkit/WebViewCompat
- https://developer.android.com/reference/androidx/webkit/ProxyController
- https://developer.android.com/reference/androidx/webkit/ProxyConfig
- https://developer.android.com/jetpack/androidx/releases/webkit
- https://developer.android.com/reference/android/webkit/CookieManager
