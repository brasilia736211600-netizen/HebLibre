# HebLibre Workflow State

## Canonical continuity
GitHub is the source of truth. Chat history, agent memory, and unstated local state are non-authoritative.
Repository: `brasilia736211600-netizen/HebLibre`
Branch: `genspark-dev`
Default branch: `l10n_crowdin`

Execution protocol:
`READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → REVIEW → COMMIT → SAVE STATE`

Verification levels: SOURCE-VERIFIED, TEST-VERIFIED, CI-VERIFIED, ANDROID-RUNTIME-VERIFIED, DOCUMENTED. Never conflate them.

## Tool-assisted workflow
- **GitHub** is the operational source of truth and the primary execution surface.
- **Codex Engineering Guardrails**: apply YAGNI, scope control, verification-level discipline, and no unsupported claims.
- **Codex Process Jobs**: use only when a task has genuinely independent work units; do not decompose small tasks unnecessarily.
- **Codex Coordinator**: use when multiple workstreams are active or dependencies must be coordinated; do not add coordination overhead to a single local change.
- **CodeRabbit**: use for substantive diff/PR review and security/code-quality review after implementation; do not substitute it for tests or source verification. Current environment lacks a local repository/terminal surface for the required CLI workflow, so no CodeRabbit result is claimed for the current changes.
- **Codex Advisor**: use only at non-trivial engineering decision points.
- **AI DevKit / Develoop**: optional accelerators when they provide a concrete capability not already available through GitHub/Codex; do not make them mandatory layers.
- **Plugin Autopilot**: use only when selecting/combining external plugin capabilities is itself the task.
- **Yaps Memory**: optional convenience only; never authoritative over GitHub continuity files.
- **Prompt Optimizer**: not part of the normal development loop; optimize prompts only when a real prompt-quality bottleneck is demonstrated.

The objective is minimum user intervention: the user can say `استمر` / `continue`, and the agent should resume from GitHub state, choose the smallest valid next action, execute, verify, save state, and continue without asking for unnecessary manual steps.

## Current repository state
- Exact current branch HEAD: `0b299bdee4ffb0c74c52234ba9e74ddedd5021b0`.
- P2 Step 4 Desktop Mode is implemented and CI-VERIFIED by Actions run `33677771905` on feature HEAD `c5c9e77abe8df400bc902099a7877ec6a3d1fc51`.
- A follow-up documentation checkpoint changed the branch HEAD to `0b299bdee4ffb0c74c52234ba9e74ddedd5021b0`; therefore the documentation commit itself remains the latest branch tip and the feature implementation remains CI-VERIFIED at its exact feature HEAD.
- Android runtime verification remains deferred because no Android runtime is available.
- Canonical continuity files: `docs/HEBLIBRE_WORKFLOW_STATE.md`, `docs/HEBLIBRE_MASTER_PROJECT_MAP.md`, `docs/HEBLIBRE_RESUME_COMMAND.md`, `docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md`.
- Genspark credits are exhausted; all work continues through available GitHub/local capabilities.

## Completed engineering
1. Build/toolchain recovery: Gradle 5.4.1 / AGP 3.5.2 baseline recovered with JDK 11, compile SDK 29, build-tools 28.0.3; debug build verified locally.
2. Minimal JUnit4 harness: `BrowserUnit.isURL` characterization; 4 tests green.
3. CI workflow: JDK 17 for SDK tooling, JDK 11 for Gradle. Successful CI established.
4. P1 profile/identity groundwork: profile-scoped whitelist state and persisted `PROFILE_ID` boundary complete; runtime validation deferred.
5. P1 lifecycle/test-seam review: no dependency-free end-to-end seam justified; no synthetic framework added.
6. P1 WebLibre/HebLibre feature gap matrix completed and persisted.
7. P2 Step 1: conservative tracking/query-parameter cleanup completed.
8. P2 Step 2: HTTPS-only navigation policy completed and CI-VERIFIED.
9. P2 Step 3: Global Privacy Control completed and CI-VERIFIED.
10. P2 Step 4: Desktop Mode completed with deterministic JVM coverage and CI verification.

## P2 Step 4 — Desktop Mode (COMPLETE)
### Source verification
The existing WebView already owns `WebSettings` and already supports a `userAgent` preference. No networking stack or engine replacement is required. The current `NinjaWebView` path can choose the effective user-agent before navigation.

### TDD / implementation
- TDD contract: `app/src/test/java/de/baumann/browser/unit/DesktopModePolicyTest.java`.
- Policy: `app/src/main/java/de/baumann/browser/unit/DesktopModePolicy.java`.
- Preference: `desktop_mode` (default off).
- Enabled behavior: a single stable desktop user-agent is used, overriding the custom mobile/user agent for that navigation.
- Disabled behavior: explicit custom user-agent remains authoritative; blank custom value falls back to the WebView default user-agent.
- `NinjaWebView` captures the WebView default UA, applies the policy during preference initialization, and reapplies it immediately before navigation so toggling the preference does not require a new architecture.
- No new dependency, transport, or storage layer was introduced.

### Verification
- SOURCE-VERIFIED: complete.
- TEST-VERIFIED: complete — `DesktopModePolicyTest` committed before implementation.
- CI-VERIFIED: complete — Actions run `33677771905`, commit `c5c9e77abe8df400bc902099a7877ec6a3d1fc51`, conclusion `success`.
- ANDROID-RUNTIME-VERIFIED: not performed; no Android runtime available.
- CODE REVIEW: CodeRabbit not run in this environment because its documented review flow requires a local git repository and CLI surface.
- DOCUMENTED: complete in continuity files.

## Corrected existing-feature findings
- **Clear-on-exit** is already implemented: `sp_clear_quit` exists in the clear settings and `BrowserActivity.onDestroy()` starts `ClearService` when enabled. It is not a migration target.
- **OLED/AMOLED pure-black theme** already exists as `AppTheme_amoled` with black background/navigation colors and white primary/secondary text. It is not a migration target.

## Architecture boundary
The P1 profile mechanism still does not isolate process-wide `CookieManager`, Chromium WebView disk storage, the default SharedPreferences store as a whole, history, or bookmarks. No full browser-storage isolation is claimed.

No multi-process architecture, WebView data-directory switching, extension runtime, proxy/Tor stack, WebRTC subsystem, DNS-over-HTTPS stack, fingerprinting subsystem, or AI runtime has been added.

## P2 Step 5 selection rule
Select the smallest remaining high-value feature with a strong product/privacy payoff and bounded implementation scope. Prefer deterministic JVM seams. For Android-window or UI-only features without a meaningful pure-Java seam, perform a bounded source trace and document the decision before implementation. Do not start architectural features merely because they appear in the WebLibre feature pool.

## Next execution step
**Source-verify Screenshot Protection as the next high-value bounded feature; confirm there is no existing `FLAG_SECURE`/equivalent path, define the smallest preference + activity-window implementation, and choose TDD only if a meaningful dependency-free seam exists.**

## Last updated
2026-09-02 — Desktop Mode implemented and CI-VERIFIED; existing Clear-on-exit and AMOLED support source-verified; P2 Step 5 is now Screenshot Protection source verification.
