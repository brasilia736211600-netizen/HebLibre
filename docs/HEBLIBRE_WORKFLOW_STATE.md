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
- **CodeRabbit**: use for substantive diff/PR review and security/code-quality review after implementation; do not substitute it for tests or source verification.
- **Codex Advisor**: use only at non-trivial engineering decision points.
- **AI DevKit / Develoop**: optional accelerators when they provide a concrete capability not already available through GitHub/Codex; do not make them mandatory layers.
- **Plugin Autopilot**: use only when selecting/combining external plugin capabilities is itself the task.
- **Yaps Memory**: optional convenience only; never authoritative over GitHub continuity files.
- **Prompt Optimizer**: not part of the normal development loop; optimize prompts only when a real prompt-quality bottleneck is demonstrated.

The objective is minimum user intervention: the user can say `استمر` / `continue`, and the agent should resume from GitHub state, choose the smallest valid next action, execute, verify, save state, and continue without asking for unnecessary manual steps.

## Current repository state
- Exact current branch HEAD: `e96298cbb2c0d0f3d9813ddc913bcbb98b348e2f`.
- P2 Step 3 GPC implementation is complete at source and test level and is **CI-VERIFIED** by Actions run `33668540065` (Unit Tests, push event, conclusion `success`).
- The immediately following documentation checkpoint run `33676112149` also completed successfully on commit `9ddec142c8ce8502eb73e93f46e900203056c875`.
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

## P2 Step 3 — Global Privacy Control (COMPLETE)
### Source verification
The existing `NinjaWebView.getRequestHeaders()` path already carries local navigation headers such as `DNT` and `Save-Data`. GPC was added without a networking stack. Direct navigation and intercepted HTTP(S) link navigation use the existing header path.

### TDD / implementation
- TDD contract: `app/src/test/java/de/baumann/browser/unit/GpcPolicyTest.java`.
- Policy: `app/src/main/java/de/baumann/browser/unit/GpcPolicy.java`.
- Header: `Sec-GPC: 1` when `gpc_enabled` is enabled.
- Disabled state: no GPC value.
- No networking architecture, interceptor, dependency, or new transport layer introduced.

### Verification
- SOURCE-VERIFIED: complete.
- TEST-VERIFIED: complete.
- CI-VERIFIED: complete — run `33668540065`, commit `e96298cbb2c0d0f3d9813ddc913bcbb98b348e2f`, conclusion `success`.
- ANDROID-RUNTIME-VERIFIED: not performed.
- DOCUMENTED: complete in continuity files.

## Architecture boundary
The P1 profile mechanism still does not isolate process-wide `CookieManager`, Chromium WebView disk storage, the default SharedPreferences store as a whole, history, or bookmarks. No full browser-storage isolation is claimed.

No multi-process architecture, WebView data-directory switching, extension runtime, proxy/Tor stack, WebRTC subsystem, DNS-over-HTTPS stack, fingerprinting subsystem, or AI runtime has been added.

## P2 Step 4 selection rule
After CI verification of each P2 feature, select the **smallest high-value feature** from `docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md`, preferring deterministic JVM seams and reuse of existing HebLibre infrastructure. Do not start architectural features merely because they appear in the WebLibre feature pool.

## Next execution step
**Read the current gap matrix and source-verify the top smallest high-value P2 candidate. If it is deterministic and dependency-free, write its TDD contract first; otherwise perform a bounded source trace and record the decision before implementation.**

## Last updated
2026-09-02 — GPC CI verification completed; workflow/tooling protocol synchronized; P2 Step 4 candidate selection is now the sole next action.
