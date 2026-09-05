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
2. docs/HEBLIBRE_PRODUCT_SCOPE_2026-09-06.md
3. docs/HEBLIBRE_SECURITY_AUDIT_2026-09-03.md
4. docs/HEBLIBRE_RESUME_COMMAND_2026-09-06.md

Then VERIFY directly from GitHub:
- current branch HEAD
- current diff against clean implementation checkpoint 5fffd65e80e2a616a5273befe7cdca6309441490
- last commits
- relevant CI runs and their exact head SHAs
- current application-source checkpoint
- no unverified runtime change was introduced

Use this execution protocol exactly:
READ -> VERIFY -> RECONCILE -> PLAN -> EXECUTE -> TEST -> DIFF -> REVIEW -> COMMIT -> SAVE STATE

Never conflate verification levels:
SOURCE-VERIFIED
TEST-VERIFIED
CI-VERIFIED
ANDROID-RUNTIME-VERIFIED
DOCUMENTED

CURRENT BASELINE:
- Clean implementation checkpoint: 5fffd65e80e2a616a5273befe7cdca6309441490
- Current branch may contain later application-source and documentation commits; determine the exact live HEAD from GitHub every time.
- Existing verified work includes P2.1-P2.11, download-cookie privacy, profile-aware whitelist transfer, tab reorder core, remote-content default consistency, and other recorded bounded changes.
- Android runtime validation remains consolidated near the final coherent product checkpoint.

ACTIVE PRODUCT REQUIREMENTS:

1) TRUE PROFILES / CONTAINERS
Each named profile/container is a reusable browsing identity with explicit separation of:
- cookies/session state
- WebView storage and service-worker state where technically supported
- profile-owned history/bookmarks/tabs/session state according to the defined contract
- whitelist state
- profile privacy/security settings
- user-agent configuration
- proxy configuration only when true profile scope can be guaranteed
- timezone/time policy
- metadata: name, color/icon, notes, tags, group

Prefer AndroidX WebKit MULTI_PROFILE where supported. Never simulate full storage isolation using only app-owned database rows and then label it as complete browser isolation.

2) LOGIN / COOKIE / SESSION MANAGEMENT
The browser must support user-controlled per-profile session portability.
- import cookies/session data into the selected profile
- export the selected profile's cookie/session data with an explicit confirmation
- capture/backup cookie state through supported WebView/profile APIs
- secure local handling; never log, upload, or silently exfiltrate authentication material
- clearly disclose fields that cannot be exported/replayed exactly
- use a versioned portable cookie format and validate imports before mutation
- cookie handling must respect SameSite, Secure, HttpOnly, partitioned-cookie and expiry semantics where exposed by platform APIs
- do not implement cross-app cookie theft or hidden credential extraction

3) PROXY
Required UI model: proxy belongs to a profile/container.
However, AndroidX WebKit ProxyController is process-specific and applies to all WebViews in the app. Therefore:
- do not fake per-profile proxying with one process-global ProxyController
- perform a capability/architecture study first
- evaluate process-level isolation, network tunneling/VPN-style routing, or another verifiable architecture
- expose scope honestly when only process-wide routing is available
- support HTTP/HTTPS/SOCKS forms, bypass rules, direct mode, credential protection, validation state where supported

4) PROFESSIONAL USER-AGENT
Each profile/container supports:
- system/default UA
- custom UA
- curated real presets for major browser families and operating systems/versions
- logically consistent UA metadata/client hints where supported
- editable preset copies
- validation against contradictory browser/OS/version combinations

Do not create impossible or obsolete-looking combinations just to inflate the preset catalog.

5) TIMEZONE / TIME POLICY
Per profile/container:
- device timezone default
- explicit timezone selection
- optional proxy/network-derived time information only when technically observable and reliable
- never claim that browser timezone changes the Android system clock
- keep locale/language/timezone internally consistent

6) SAME-URL PROFILE SWITCH
Core UX:
- current URL U remains selected while the user changes from profile A to profile B
- B becomes active
- B's cookies/storage/UA/proxy/privacy state applies
- U is reloaded under B
- A is left untouched
- do not corrupt back/forward history semantics
- switch should be fast and visually lightweight

This should be implemented as a controlled profile/context switch plus reload, not as unsafe mutation of one WebView identity unless the platform explicitly supports it.

7) PROFILE + WHOLE-BROWSER IMPORT/EXPORT
Profile/container bundle:
- versioned manifest
- profile metadata/settings
- scoped tabs/session state
- history/bookmarks according to scope
- whitelist data
- cookies/session material where supported
- UA settings
- proxy settings with protected secret material
- timezone/time policy

Whole-browser bundle:
- all profile/container bundles
- global browser settings
- global metadata
- versioned top-level manifest

Security rules:
- secrets are opt-in for export
- sensitive fields are encrypted/protected where feasible
- imports are schema/version validated
- malformed input is rejected safely
- default import mode must not destructively overwrite existing data without explicit confirmation
- partial-failure semantics must be defined and tested

8) LIGHTWEIGHT MODERN UI
UI must remain fast and dependable on constrained Android devices:
- compact profile/container switcher
- current identity clearly visible
- quick switch with search/filter
- easy create/duplicate/delete/rename
- clear active profile indicator
- modern but low-overhead layouts
- no large framework for cosmetic purposes only
- preserve proven browser interactions, especially existing tab close behavior

9) PERFORMANCE / RELIABILITY
Profile switching and creation must not become a reason to keep every profile renderer permanently active.
Measure memory/startup implications before adopting concurrent live WebViews or multiple processes.
Prefer lazy activation and bounded lifecycle management.

10) WORKFLOW / DELIVERY
For every bounded subsystem:
- contract first
- capability audit first when platform limitations exist
- dependency-free JVM tests first where practical
- minimal implementation
- tests
- GitHub diff review
- CI
- documentation/state update

Parallelize only independent streams; serialize dependent file/ref mutations.
Never claim a feature is complete solely because the UI exists.

PARALLEL STREAMS TO USE WHEN INDEPENDENT:
A. Profile metadata/model and ownership contract
B. Real WebView multi-profile feasibility + dependency compatibility
C. Cookie/session portable format and secure import/export contract
D. UA catalog/policy
E. Proxy architecture feasibility
F. Timezone policy
G. Same-URL switching lifecycle design
H. Profile/whole-browser bundle manifest and migration rules
I. Lightweight switcher UI information architecture
J. Performance/memory instrumentation plan

Do not start a broad architectural rewrite until B/E/J are resolved enough to define safe boundaries.

YAGNI / EXCLUSIONS:
QR scanner, PWA, Reader Mode, extensions/uBlock, DoH, full WebRTC privacy, per-container Tor, broad anti-fingerprinting/evasion machinery, stealth automation, fraud/security-control bypass, identity-verification bypass, covert session sharing, and credential theft remain excluded or deferred unless the active product scope explicitly changes.

SECURITY BOUNDARY:
Only handle authentication/session data explicitly owned or supplied by the user. Never steal cookies from other apps, bypass platform protections to obtain credentials, exfiltrate sessions, or log secrets.

SESSION CLOSEOUT:
After each meaningful step:
- record exact changed files
- record SOURCE/TEST/CI/ANDROID-RUNTIME status separately
- record exact application-source checkpoint and live HEAD
- record design decisions and rejected approaches
- update docs/HEBLIBRE_WORKFLOW_STATE.md
- state exactly one executable next action
```

## Platform research baseline

AndroidX WebKit `Profile` represents a WebView browsing session and supports multiple profiles with isolated profile data. `WebViewCompat.setProfile()` associates a WebView with a named profile when `MULTI_PROFILE` is supported. Profile APIs expose profile-specific cookie management, web storage, service-worker control and related browsing state. This is the preferred technical foundation for true profile isolation.

The repository currently declares `androidx.webkit:webkit:1.3.0-alpha03` and `minSdkVersion 21`. Multi-profile APIs start in AndroidX WebKit 1.9.0. WebKit 1.15.0 adds newer cookie interception APIs but raises minSdk from 21 to 23, so dependency migration must be evaluated deliberately. A 1.14.x candidate is preferable to assess first if API 21 compatibility remains mandatory; verify the actual build and runtime support before committing to it.

`ProxyController.setProxyOverride()` is process-specific and applies to all WebViews in the app. It must therefore not be presented as a profile-local proxy. True per-profile proxy routing requires a separate verified architecture or an explicitly documented limitation.

WebView supports direct User-Agent customization through `setUserAgentString`; newer AndroidX WebKit APIs also expose User-Agent metadata/client-hint control. UA presets should therefore validate both string and metadata consistency where supported.

Cookie/session export must use browser-owned/profile-owned APIs and data. Do not bypass another app's sandbox or security model to obtain cookies.

## External technical sources reviewed
- Android Developers — Profile: https://developer.android.com/reference/androidx/webkit/Profile
- Android Developers — WebViewCompat: https://developer.android.com/reference/androidx/webkit/WebViewCompat
- Android Developers — ProxyController: https://developer.android.com/reference/androidx/webkit/ProxyController
- Android Developers — ProxyConfig: https://developer.android.com/reference/androidx/webkit/ProxyConfig
- Android Developers — AndroidX WebKit release notes: https://developer.android.com/jetpack/androidx/releases/webkit
- Android Developers — WebView: https://developer.android.com/reference/android/webkit/WebView
- Android Developers — WebView security guidance: https://developer.android.com/privacy-and-security/security-tips
