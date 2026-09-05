# HebLibre Feature Synthesis Matrix

## Purpose
This document is a feature/design reference, not the continuity authority. It now reflects the 2026 HebLibre product-scope reset. Current source evidence always overrides this planning matrix.

## Core rule
Use competitor products as requirement evidence, not as implementation templates. Adopt only features that provide legitimate privacy/productivity value, have a viable Android/WebView seam, and satisfy TDD, YAGNI, compatibility, and performance constraints.

## Completed HebLibre capabilities
Tracking/query cleanup, HTTPS-only, Global Privacy Control, Desktop Mode, screenshot protection, search bangs, bounded camera/microphone permission guard, third-party cookie control, geolocation guard, Save-Data, global settings search, download-cookie privacy, profile-aware whitelist transfer, tab reorder core, and remote-content default consistency are already implemented at the recorded source/test/CI levels.

## Social Browser feature-source findings
The public Social Browser GitHub release repository advertises multi-tabs/multi-users, autofill, profile management, custom User-Agent and proxy settings, data import/export, password protection, multi-user site access, password manager, keyboard shortcuts, cloud sync, video downloading, translation, zoom/sound control, PDF export, page edit mode, PDF reader, proxy manager, User-Agent manager, download manager, developer tools, session sharing, and resource blocking. Its README also describes ad/popup/redirect blocking, safety mode, and script-manager support. The project is proprietary, so HebLibre must not copy its implementation. Source: https://github.com/absunstar/Social-Browser-Releases

## 2026 leading anti-detect market pattern synthesis
Market references repeatedly identify Multilogin, GoLogin, AdsPower, Dolphin Anty, Kameleo, Octo Browser, Incogniton and additional profile-oriented products as leading solutions. Recurring capabilities include isolated reusable profiles, profile metadata and grouping, cookies/session state, proxy association, local/cloud storage choices, synchronization, configuration management, and automation APIs.

The product scope intentionally does not copy anti-fraud evasion functions. It extracts legitimate privacy and profile-isolation concepts.

## Candidate roadmap

| Candidate | Status | Implementation direction |
|---|---|---|
| Reusable named profiles | PROMOTE / P1 | Build on existing profile identity; make storage ownership explicit. |
| Profile groups/tags/notes | PROMOTE / P1 | Lightweight metadata layer. |
| Profile-local settings | PROMOTE / P1 | Partition settings that can be safely scoped. |
| Profile-local cookies/login state | PROMOTE / P1/ARCH | Requires real WebView storage separation; do not fake isolation with whitelist state. |
| Profile-local WebView storage | ARCHITECTURAL | Investigate WebView data-directory support and lifecycle limits before coding. |
| Profile-local history/bookmarks | PROMOTE / P1 | Define data model and migration semantics. |
| Profile session restore | PROMOTE / P1 | Reconcile tab/session state with profile ownership. |
| Profile import/export | PROMOTE / P1 | Explicit security and compatibility contract; sensitive data must be protected. |
| Optional encrypted profile state | PROMOTE / P1 | Use platform cryptography; measure storage/runtime cost. |
| Per-profile proxy | ARCHITECTURAL | Android/WebView feasibility must be established before implementation. |
| Proxy manager/status | P2 candidate | Only after per-profile routing contract exists. |
| Consistent profile UA/language configuration | P1 candidate | Implement only controls that are truly profile-local on Android. |
| Site permissions editor | P2 candidate | Build on WebView callbacks and persisted policy state. |
| Resource-type blocking | P2 candidate | Extend existing interception only where deterministic and measurable. |
| Popup/redirect controls | P2 candidate | Add bounded rules with tests; preserve legitimate navigation. |
| Developer/page diagnostics | P2 candidate | Prefer lightweight diagnostics over a permanent heavy devtools runtime. |
| Page translation | P2 candidate | Avoid mandatory online service dependency for core browsing. |
| Download manager improvements | P2 candidate | Preserve existing privacy control and keep UI/storage lightweight. |
| Tab groups/reorder UI | P2 candidate | Add a dedicated non-long-press affordance; preserve close gesture. |
| Script manager | P3 / ARCH | Android WebView does not provide a full extension-script runtime comparable to desktop Chromium. |
| Extensions | P3 / ARCH | Requires a real compatible engine/runtime; do not add speculative framework weight. |
| Cloud sync | P3 / OPTIONAL | Local-first core; synchronization must be additive and explicitly secured. |
| Session sharing | DEFERRED / SECURITY REVIEW | Sharing cookies/storage can create account leakage; never enable implicitly. |
| Automation API | P3 / OPTIONAL | Evaluate only for legitimate testing/accessibility/development workflows. |
| Password protection | P2 candidate | Protect app/profile management without inventing a custom weak crypto layer. |
| QR/barcode scanning | DEFERRED | Requires camera/decoder integration; not core to current lightweight target. |
| PWA | DEFERRED / ARCH | Requires lifecycle and storage design. |
| Reader Mode | DEFERRED | Requires safe extraction architecture. |
| Multi-window | DEFERRED / ARCH | Lifecycle/state ownership change. |
| DoH | DEFERRED / ARCH | Network resolver architecture. |
| Full WebRTC privacy | DEFERRED / ARCH | Engine-level work; current media guard remains bounded. |

## Anti-detect-derived privacy boundary
HebLibre may reduce unwanted tracking and cross-profile correlation by isolating state, minimizing unnecessary persistent identifiers, keeping profile configuration internally consistent, and exposing user-controlled privacy settings.

HebLibre must not add mechanisms whose primary purpose is to evade fraud detection, defeat identity verification, circumvent bans, disguise automated behavior as a real user, or bypass platform security controls.

## Lightweight performance gate
A candidate feature is rejected or deferred when its memory, startup, storage, battery, dependency, or lifecycle cost is disproportionate to its user value. Large browser engines, mandatory cloud services, permanently running background agents, or heavy libraries require explicit architecture approval.

## Current phase
`2026 Product Scope Reset → P0/P1 architecture and bounded implementation planning`

## Next selection rule
Select the smallest high-value candidate with the clearest Android seam. Prefer deterministic JVM policy extraction first. Architectural candidates must first produce a feasibility/design record with performance and isolation constraints before source implementation.

## Last synchronized
2026-09-06 — converted the old WebLibre comparison matrix into the active 2026 feature-synthesis matrix and added the Social Browser plus leading anti-detect market research boundary.
