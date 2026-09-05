# HebLibre Persistent Execution Board

## Objective
A durable, agent-neutral task board. This file answers: what is done, what is pending, what is intentionally deferred, and what must happen next.

## Status keys
- `DONE-SOURCE`: source verified.
- `DONE-TEST`: deterministic tests verified.
- `DONE-CI`: CI verified against the relevant source checkpoint.
- `DONE-EMULATOR`: Android emulator runtime verified.
- `DONE-PHYSICAL`: physical target-device verified.
- `PARTIAL`: usable core exists but end-to-end feature is incomplete.
- `RESEARCH`: researched but not yet implemented.
- `ARCHITECTURAL`: requires design/architecture checkpoint first.
- `DEFERRED`: intentionally postponed.
- `POLICY-DECISION`: do not change until explicit contract.
- `EXCLUDED`: outside product safety/scope boundary.

## Current phase
`2026 Product Scope Reset → P0/P1 architecture and bounded implementation planning`

## Completed verified foundation
Tracking/query cleanup, HTTPS-only, Global Privacy Control, Desktop Mode, screenshot protection, search bangs, bounded camera/microphone permission guard, third-party cookie control, geolocation guard, Save-Data, global settings search, download-cookie privacy, profile-aware whitelist transfer, tab reorder core, and remote-content default consistency remain implemented at the recorded source/test/CI levels. GitHub-hosted Android emulator smoke also passed on its documented application-source checkpoint. Physical-device validation has not been performed for the new product scope.

## New product-direction work
| Workstream | Status | Next action |
|---|---|---|
| Durable continuity/control plane | DONE-SOURCE | Enforce docs-first bootstrap and material-step persistence. |
| Product scope reset | DONE-SOURCE | Use the 2026 scope document as active direction. |
| Competitor feature synthesis | RESEARCH | Translate research into bounded Android-native requirements. |
| Profile metadata/groups/tags/notes | RESEARCH | Audit current data model; propose smallest deterministic seam. |
| True profile-local WebView storage | ARCHITECTURAL | Perform feasibility audit before code. |
| Profile-local history/bookmarks | RESEARCH | Audit RecordAction/RecordHelper boundaries and migration cost. |
| Profile-local cookies/login state | ARCHITECTURAL | Determine CookieManager/WebView storage constraints. |
| Profile import/export | RESEARCH | Define security/compatibility contract before implementation. |
| Optional encrypted profile state | RESEARCH | Evaluate platform crypto primitives and data boundary. |
| Per-profile proxy | ARCHITECTURAL | Establish Android/WebView routing feasibility first. |
| Profile-local UA/language settings | RESEARCH | Implement only settings proven to be profile-local. |
| Site permission editor | RESEARCH | Map existing WebView permission callbacks and persistence. |
| Resource blocking | RESEARCH | Extend current interception with measurable bounded rules. |
| Popup/redirect controls | RESEARCH | Identify navigation seams and preserve legitimate flows. |
| Tab groups/reorder UI | PARTIAL | Design dedicated non-long-press affordance; keep close gesture. |
| Password/app protection | RESEARCH | Audit existing settings/data flows and use platform security. |
| Session synchronization | DEFERRED / OPTIONAL | Local-first; requires explicit secure synchronization contract. |
| Script manager / extensions | ARCHITECTURAL | Requires compatible Android engine/runtime; no speculative dependency. |
| Automation API | DEFERRED / OPTIONAL | Consider only for legitimate testing/development workflows. |
| DoH | ARCHITECTURAL | Requires resolver/network design. |
| Full WebRTC privacy | ARCHITECTURAL | Requires engine-level design beyond current permission guard. |
| PWA | DEFERRED / ARCHITECTURAL | Lifecycle/storage design required. |
| QR/barcode scanner | DEFERRED | Not core to lightweight browser target. |
| Reader Mode | DEFERRED | Safe extraction architecture required. |

## Explicitly excluded
- Primary-purpose fraud/security detection bypass.
- Identity-verification bypass or ban evasion.
- Covert stealth automation intended to defeat platform enforcement.
- Credential theft or covert session sharing.
- Heavy frameworks or always-on services without demonstrated value and performance justification.

## Performance gate
Every candidate must document expected startup, memory, storage, battery, dependency, and lifecycle impact before implementation. A feature that materially degrades the lightweight target without proportional value is deferred or rejected.

## TDD gate
For bounded work with a deterministic seam:
`WRITE/REFINE TESTS → MINIMAL IMPLEMENTATION → TEST → DIFF → REVIEW → CI → SAVE STATE`

## Physical-device validation
Status: `PENDING`

The physical-device pass is performed after the new product-scope implementation wave reaches a coherent checkpoint. Do not use repeated APK installation as the development loop.

## Runtime defect protocol
`reproduce → smallest seam → batch related fixes → deterministic tests → CI → physical recheck`

## Continuity checkpoint requirement
After every meaningful step, synchronize the operational workflow state with the current exact HEAD, evidence, material reasoning/decisions, unresolved items, and next executable step.

## Last synchronized
2026-09-06 — reset the execution board around the new lightweight profile/privacy product direction and the legitimate-privacy boundary for anti-detect-derived capabilities.
