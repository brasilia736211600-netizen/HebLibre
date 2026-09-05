# HebLibre Workflow State

## Canonical continuity
GitHub is the source of truth. Chat history, model memory, plugin memory, and unstated local state are non-authoritative.
Repository: `brasilia736211600-netizen/HebLibre`
Branch: `genspark-dev`
Default branch: `l10n_crowdin`

## Communication language
All human-facing conversation about HebLibre is in Arabic unless the user explicitly requests another language. Durable project-control documents are written in clear English. Source code, identifiers, commit messages, CI output, and filenames remain in their native/project language.

## Mandatory bootstrap
Before any implementation, inspect the current `docs/` control-plane area, reconcile it with live GitHub source, verify the actual branch/HEAD, verify relevant evidence, and identify one executable next step.

## Execution protocol
`READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → REVIEW → COMMIT → SAVE STATE`

## Verification levels
- `SOURCE-VERIFIED`: current source inspected directly.
- `TEST-VERIFIED`: deterministic automated tests passed.
- `CI-VERIFIED`: GitHub Actions passed for the relevant source checkpoint.
- `ANDROID-RUNTIME-VERIFIED`: application executed on Android; emulator and physical-device evidence are distinct.
- `DOCUMENTED`: intent, decision, or result persisted in repository documentation.

Never conflate these levels.

## Live checkpoint
- Live `genspark-dev` HEAD: `134325502235ef838554f0a69f019586d26d81de`.
- Latest verified application-source fix: `247768c4e2e442fcb9b42d299d8cf00d3c24b81b` — whitelist import/export resolves the active normalized profile.
- Consolidated Unit Tests run `33985143542` passed for that application-source checkpoint.
- Current branch HEAD is documentation/control-plane work after the application-source checkpoint; no new runtime feature has been introduced by the scope reset.
- Current-HEAD Unit Tests previously passed at run `33990897505`.
- Android emulator smoke previously passed at run `33988773661` on checkpoint `48300a4ad2c366e2987cea9949de8e722089c962`; this remains emulator evidence only.
- Physical target-device validation is not complete for the new product scope.

## Current product direction
The 2026 product direction is defined by `docs/HEBLIBRE_PRODUCT_SCOPE_2026-09-06.md`.

Target: a lightweight, fast, reliable Android browser combining high-value profile-management and privacy patterns from mature social/multi-profile browsers with legitimate privacy lessons from leading anti-detect browsers.

Anti-detect is bounded to legitimate privacy, profile isolation, session continuity, configuration consistency, and user-controlled data separation. Primary-purpose fraud/security detection bypass, identity-verification bypass, ban evasion, covert stealth automation, credential theft, and covert session sharing are excluded.

Older chat-only feature requests are superseded by the active product-scope document.

## Durable continuity rule
Every material step must be recoverable from GitHub without chat context. Persist material decisions, architecture conclusions, rejected approaches, scope changes, blockers, test conclusions, exact source checkpoints, CI evidence, and the next executable step before they can be lost.

Do not commit trivial thoughts. Do not allow a material conclusion to exist only in chat.

## Completed foundation
P2.1–P2.11, download-cookie privacy, profile-aware whitelist transfer, tab reorder core, and remote-content default consistency remain implemented at the recorded source/test/CI levels. The bounded existing feature set is not being reimplemented.

## New roadmap status
`P0/P1 planning and bounded implementation wave` is now active.

Priority next areas:
1. Profile metadata/groups/tags/notes.
2. Profile-local history/bookmarks/session ownership.
3. Feasibility of real WebView storage partitioning.
4. Profile-local cookies/login state.
5. Secure profile import/export.
6. Optional encrypted sensitive profile state.
7. Profile-local settings where Android truly permits it.
8. Per-profile proxy feasibility audit.
9. Site permission/resource controls.
10. Tab grouping/reorder UI without breaking long-click close semantics.
11. Performance/startup/memory instrumentation before heavy architectural additions.

Architectural candidates such as WebView storage partitioning, per-profile network routing, DoH, full WebRTC privacy, extension runtime, PWA, multi-window, QR scanning, and Reader Mode require feasibility/design checkpoints before implementation.

## Security decisions not silently reopened
- SSL certificate-error override behavior.
- Application-level `android:usesCleartextTraffic` policy.
- Automatic Android backup semantics for `Ninja4.db`.
- Coupling of file-origin access and DOM storage under `sp_remote`.
- Complete profile/WebView storage isolation until its architecture is explicitly designed.

## Android validation policy
Do not repeatedly build/install APKs during feature work. Finish source review, deterministic tests, CI, review, and documentation first. Physical validation is consolidated near a coherent product checkpoint.

## Current blockers
No implementation blocker for starting the first bounded P1 profile-management audit. Architectural blockers remain for true per-profile WebView storage and per-profile network routing; these must be investigated before code is promised.

## Current next executable step
Run a bounded source audit of the profile model and `RecordAction`/`RecordHelper` boundaries, then select the smallest TDD-first profile-management seam. In parallel, perform a feasibility audit of WebView storage partitioning and measure the likely performance/memory cost before introducing any heavy architecture.

## Session handoff rule
At the end of every meaningful session, update this file with the exact live HEAD, current application-source checkpoint, work completed, evidence, material reasoning/decisions, unresolved items, and one next executable step.

## Last updated
2026-09-06 — adopted the 2026 product-scope reset, synchronized the durable control plane with live branch state, replaced the old deferred backlog with a P0/P1 implementation wave, and recorded the legitimate-privacy boundary for anti-detect-derived ideas.
