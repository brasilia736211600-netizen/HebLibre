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

For new sessions, read in this order:
1. `docs/HEBLIBRE_WORKFLOW_STATE.md`
2. `docs/HEBLIBRE_PRODUCT_SCOPE_2026-09-06.md`
3. `docs/HEBLIBRE_SECURITY_AUDIT_2026-09-03.md`
4. `docs/HEBLIBRE_RESUME_COMMAND_2026-09-06.md`

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
- Live `genspark-dev` HEAD after the latest control-plane synchronization: `564ad75209811555599ae58183489e8cba7623f2`.
- Latest verified application-source checkpoint remains `247768c4e2e442fcb9b42d299d8cf00d3c24b81b` (whitelist import/export resolves the active normalized profile).
- Consolidated Unit Tests run `33985143542` passed for that application-source checkpoint.
- Current branch HEAD after that source checkpoint contains control-plane/product-scope documentation work; no new runtime feature is claimed by the latest documentation commits.
- Current-HEAD Unit Tests previously passed at run `33990897505` for the then-current source checkpoint; verify any newer source checkpoint before treating that evidence as current.
- Android emulator smoke previously passed at run `33988773661` on checkpoint `48300a4ad2c366e2987cea9949de8e722089c962`; this remains emulator evidence only.
- Physical target-device validation is not complete for the expanded product scope.

## Current product direction
The active product direction is defined by `docs/HEBLIBRE_PRODUCT_SCOPE_2026-09-06.md`, with the detailed restart/implementation contract in `docs/HEBLIBRE_RESUME_COMMAND_2026-09-06.md`.

Target: a lightweight, fast, reliable Android browser with legitimate privacy-oriented multi-profile/container management.

Required profile/container direction now explicitly includes:
- reusable named profiles/containers with metadata (name, color/icon, notes, tags, groups)
- truly isolated profile cookies/session state and WebView storage where technically supported
- profile-local app-owned history/bookmarks/tabs/session data according to a defined contract
- profile-local privacy/security settings
- per-profile User-Agent configuration with coherent real-world presets and metadata where supported
- per-profile timezone/time policy without pretending to change the Android system clock
- profile/container import/export plus whole-browser import/export using versioned manifests
- secure handling of authentication/session material with explicit export confirmation and no secret logging/exfiltration
- same-URL profile switching: retain the current URL and reload it under the selected profile's state
- lightweight modern profile switcher with clear active identity
- performance/memory constraints and lazy lifecycle management

Anti-detect-derived requirements are bounded to legitimate privacy, profile isolation, session continuity, configuration consistency, portability, and user-controlled data separation. Primary-purpose fraud/security detection bypass, identity-verification bypass, ban evasion, covert stealth automation, credential theft, and covert session sharing are excluded.

## Current platform findings that shape architecture
- The current repository uses `androidx.webkit:webkit:1.3.0-alpha03` with `minSdkVersion 21`.
- Modern AndroidX WebKit provides multi-profile support beginning with 1.9.0; the `Profile` API represents separate WebView browsing sessions with profile-owned cookies/storage and related state.
- WebKit 1.15.0 adds newer cookie/request APIs but raises minSdk to 23, so dependency migration must be evaluated rather than blindly upgraded.
- `ProxyController` is process-specific and applies its proxy configuration to all WebViews in the app; it cannot be presented as a genuinely profile-local proxy. True per-profile proxying needs another verified architecture or an explicit process-wide scope.
- User-Agent strings are directly configurable in WebView, and newer AndroidX WebKit APIs expose User-Agent metadata/client-hint controls.

## Completed foundation
P2.1–P2.11, download-cookie privacy, profile-aware whitelist transfer, tab reorder core, and remote-content default consistency remain implemented at the recorded source/test/CI levels. The bounded existing feature set is not being reimplemented.

## Active implementation wave
`P0/P1 planning and bounded implementation wave` remains active.

First bounded implementation targets, ordered by dependency:
1. Profile identity/metadata and profile ownership contract.
2. WebView multi-profile feasibility and dependency compatibility (must precede claims of true browser-data isolation).
3. Profile-local cookies/session handling and secure portable format.
4. Profile settings registry including UA/time policy.
5. Same-URL profile switch lifecycle.
6. Profile/container import/export manifest and migration semantics.
7. Whole-browser import/export built on the profile bundle model.
8. Proxy architecture/feasibility; do not fake per-profile scope.
9. Lightweight profile switcher UI.
10. Performance/memory instrumentation around multiple profiles and lifecycle.

Independent workstreams may be investigated in parallel, but dependent source/ref mutations must remain serialized.

## Security decisions not silently reopened
- SSL certificate-error override behavior.
- Application-level `android:usesCleartextTraffic` policy.
- Automatic Android backup semantics for `Ninja4.db`.
- Coupling of file-origin access and DOM storage under `sp_remote`.
- Complete WebView/profile storage isolation until its architecture is explicitly designed and verified.
- Any cookie/session handling that would require cross-app credential extraction or hidden exfiltration.

## Android validation policy
Do not repeatedly build/install APKs during feature work. Finish source review, deterministic tests, CI, review, and documentation first. Physical validation is consolidated near a coherent product checkpoint.

## Current blockers
The main architecture blockers are now explicit:
- current WebKit dependency is too old for the preferred modern multi-profile API surface
- `minSdkVersion 21` constrains which modern WebKit version can be adopted
- WebView proxy override is process-wide, not profile-local
- complete cookie/session portability has platform/security edge cases that must be represented honestly

These are feasibility boundaries, not reasons to stop. Resolve them with small capability audits and contracts before implementing the dependent features.

## Current next executable step
Perform the WebView multi-profile dependency/capability spike first: compare the current `1.3.0-alpha03` dependency against a candidate stable version that preserves the required Android support floor, verify `MULTI_PROFILE` availability and the lifecycle needed for `WebViewCompat.setProfile()`, and define the smallest testable profile-binding seam. In parallel, define the versioned cookie/profile export schema and its secure-field rules without integrating it into runtime yet.

## Session handoff rule
At the end of every meaningful session, update this file with the exact live HEAD, current application-source checkpoint, work completed, evidence, material reasoning/decisions, unresolved items, and one next executable step.

## Last updated
2026-09-06 — synchronized the durable workflow with the new profile/container product requirements, same-URL switching requirement, cookie/session portability requirements, professional UA/time policies, proxy-scope constraint, lightweight UI constraint, and the reusable resume command.
