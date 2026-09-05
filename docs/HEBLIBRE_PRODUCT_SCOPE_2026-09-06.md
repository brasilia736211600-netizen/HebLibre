# HebLibre Product Scope — 2026-09-06

## Purpose
This document supersedes remembered feature requests and older chat-only scope statements. It is the durable product direction for the next HebLibre phase.

## Product objective
Build a lightweight, fast, reliable Android browser that combines useful profile-management and privacy capabilities observed in mature multi-profile/social browsers with the strongest legitimate privacy-oriented capabilities observed across leading anti-detect browsers.

The goal is profile separation, privacy, consistency, portability, and reliability. The goal is not to defeat fraud systems, platform enforcement, account bans, identity verification, or other security controls.

## Research basis
The GitHub Social Browser release repository advertises multi-profile management, proxy settings, data import/export, password protection, multi-user site access, password management, autofill, cloud sync, translation, zoom/sound control, PDF export, page editing, proxy/user-agent managers, session sharing, developer tools, and resource blocking. Its repository README also describes multi-tab and multi-user operation, ad/popup/redirect blocking, safety mode, and script-manager support. The repository is proprietary, so its implementation is treated only as a feature reference, not as a source to copy. See: https://github.com/absunstar/Social-Browser-Releases

Current 2026 market comparisons consistently identify Multilogin, GoLogin, AdsPower, Dolphin Anty, Kameleo, Octo Browser, Incogniton and other profile-oriented browsers as leading products. Their recurring capabilities include isolated reusable profiles, proxy association, profile metadata/organization, cookie/session handling, API or automation integrations, cloud/local profile storage, fingerprint configuration, and team/profile management. See:
- https://www.conbersa.ai/blog/top-antidetect-browsers
- https://www.morelogin.com/blog/11-best-antidetect-browsers
- https://hidemyacc.com/best-antidetect-browser

MoreLogin documentation emphasizes reusable profiles, separate cookies/storage/login state/proxy settings, profile groups/tags/notes, synchronization, and profile management. Undetectable documentation emphasizes local/cloud profiles, import/export, cookies, extensions, profile organization, default profile settings, and profile-specific configuration. Nstbrowser documentation emphasizes profile-bound proxies, session isolation, and configurable browser environments. These patterns are useful architectural references for legitimate profile isolation and privacy, but anti-fraud evasion claims are explicitly excluded from HebLibre scope.

## Feature inclusion policy
Every candidate feature must pass all of these gates:
1. Clear user value for privacy, productivity, reliability, or legitimate multi-profile use.
2. A concrete Android/WebView implementation seam exists or a justified architectural phase is created.
3. The smallest viable implementation can be tested deterministically where practical.
4. The feature does not primarily exist to bypass platform security, fraud detection, or enforcement controls.
5. Performance and memory cost remain acceptable for a lightweight Android browser.

## Priority roadmap

### P0 — Release quality and continuity
- Persistent GitHub-controlled workflow state.
- Deterministic tests and CI for every bounded change.
- Crash/lifecycle/regression hardening.
- Fast startup, low memory overhead, and minimal dependency growth.
- Consolidated physical-device validation only after source/test/CI work is complete.

### P1 — Real profile isolation
- Reusable named profiles.
- Profile metadata: name, color/icon, notes, tags, group.
- Profile-local browser settings.
- Profile-local cookies and login state.
- Profile-local WebView storage where technically supported.
- Profile-local browsing/session restoration.
- Profile-local history and bookmarks policy.
- Explicit profile export/import with clear security semantics.
- Optional encrypted local profile data for sensitive profile state.
- Safe migration from the current default profile.

### P1 — Profile networking and privacy
- Explicit per-profile proxy configuration where Android/WebView architecture safely supports it.
- Proxy status/validation without hidden network behavior.
- Consistent language/locale/user-agent settings per profile where the platform permits profile-local control.
- Privacy controls that reduce cross-profile state leakage.
- Clear indication of which controls are truly isolated and which remain process-global.

### P1 — Browser privacy baseline
- Continue and maintain existing tracking-parameter cleanup, HTTPS-only, GPC, Save-Data, screenshot protection, media permission guard, geolocation guard, cookie control, and profile-aware whitelists.
- Strengthen popup/redirect/resource controls only where measurable and testable.
- Provide transparent per-site permissions and storage controls where supported.
- Preserve Safe Browsing and normal WebView security defaults unless a deliberate contract says otherwise.

### P2 — Profile productivity
- Fast profile switcher.
- Profile search/filter/sort.
- Duplicate profile creation with explicit data-copy semantics.
- Session restore controls.
- Better bookmark/history/profile transfer tooling.
- Download manager improvements that remain lightweight.
- Page translation and selection utilities only when they can be implemented without heavy always-on services.
- Tab grouping/reordering UI that does not break current tab-close behavior.

### P2 — Advanced privacy controls
- Per-profile storage diagnostics.
- Site-data viewer/clearer.
- Permission editor.
- Resource-type blocking controls for clearly defined categories.
- Optional script/resource controls with strict performance bounds.
- Privacy status page explaining active protections and their evidence level.

### P3 — Architectural candidates
These are not immediate implementation tasks and require an explicit design checkpoint:
- Complete Chromium/WebView storage partitioning.
- Per-profile proxy routing if WebView limitations require a network-layer architecture.
- DoH or another resolver architecture.
- Full WebRTC privacy architecture.
- Extension runtime, only if a real Android-compatible engine/runtime is selected.
- Multi-window architecture.
- PWA lifecycle support.
- QR/barcode scanning.
- Reader Mode with a safe extraction architecture.

## Explicit exclusions
Do not implement fingerprint or behavior manipulation whose primary purpose is to make automated activity appear to be a different real user to defeat detection or enforcement. Do not implement credential/session theft, covert session sharing, anti-fraud bypass logic, or stealth automation intended to evade security controls.

Legitimate privacy mechanisms such as profile isolation, reducing unwanted tracking, controlling permissions, minimizing unnecessary fingerprint entropy, and keeping settings internally consistent may be evaluated where technically sound.

## Lightweight engineering constraints
- Prefer existing Android/WebView APIs over new native frameworks.
- Avoid adding large dependencies for features that can be implemented with small deterministic seams.
- No background daemons or permanently running services without a demonstrated requirement.
- No cloud service is required for core browsing or core profile functionality.
- Local-first operation is preferred; optional synchronization must be additive and off by default unless explicitly justified.
- Measure feature cost before adopting broad libraries.

## TDD and YAGNI contract
For every new bounded feature:
`WRITE/REFINE TESTS → MINIMAL IMPLEMENTATION → TEST → DIFF → REVIEW → CI → SAVE STATE`

Do not build speculative abstractions merely because a competitor has a feature. Competitor capability is evidence of a possible requirement, not proof that HebLibre needs the same implementation.

## Scope change rule
This document is the active product-scope baseline. A scope change must be recorded in the decision log and reflected in the master map and execution board before implementation begins.

## Synchronization rule
At the beginning of a new session, the agent must read the repository control-plane documents and this product-scope document before planning work. At the end of each meaningful step, the current executable checkpoint must be persisted to the workflow state.

## Last updated
2026-09-06 — new product direction established from current HebLibre source plus external 2026 market research; older chat-only feature requests are superseded by this scope.
