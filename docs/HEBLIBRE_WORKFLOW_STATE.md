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
- This HEAD contains completed P2 Step 1 tracking cleanup, completed P2 Step 2 HTTPS-only navigation policy, and the current P2 Step 3 GPC implementation through deterministic policy tests and request-header wiring.
- No CI workflow run is currently associated with HEAD `e96298cbb2c0d0f3d9813ddc913bcbb98b348e2f`; therefore the current GPC change is not yet CI-VERIFIED.
- Canonical continuity files: `docs/HEBLIBRE_WORKFLOW_STATE.md`, `docs/HEBLIBRE_MASTER_PROJECT_MAP.md`, `docs/HEBLIBRE_RESUME_COMMAND.md`, `docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md`.
- Genspark credits are exhausted; all work continues through available GitHub/local capabilities.

## Completed engineering
1. Build/toolchain recovery: Gradle 5.4.1 / AGP 3.5.2 baseline recovered with JDK 11, compile SDK 29, build-tools 28.0.3; debug build verified locally. Baseline `e11562fd42639d5b12e5468c14d461523aa96529`.
2. Minimal JUnit4 harness: `BrowserUnit.isURL` characterization; 4 tests green. `6211fc57cc5589f24106df0c1aa2a4e7b6ae2058`.
3. CI workflow: JDK 17 for SDK tooling, JDK 11 for Gradle. Successful run `33459477325`.
4. P1 Steps 1–6: profile/whitelist identity groundwork complete; Step 6 CI-VERIFIED by run `33643362798`.
5. P1 Step 7: lifecycle/test-seam review completed; no dependency-free end-to-end seam exists, so no synthetic framework/test was added.
6. P1 Step 8 runtime validation: DEFERRED because no Android runtime is available; not a development blocker.
7. P1 Step 8A: WebLibre/HebLibre feature gap matrix completed and persisted.
8. P2 Step 1: conservative tracking/query-parameter cleanup completed.
9. P2 Step 2: local HTTPS-only navigation policy completed and CI-VERIFIED.
10. P2 Step 3: GPC request signal implementation completed through deterministic unit contract and existing WebView request-header wiring; CI verification is the remaining checkpoint.

## P2 Step 3 — Global Privacy Control (GPC)
### Source verification
The existing `NinjaWebView.getRequestHeaders()` path already carries local navigation headers such as `DNT` and `Save-Data`, so GPC can be added without a networking stack. The current source imports `GpcPolicy` and adds `Sec-GPC: 1` when the `gpc_enabled` preference is true. Direct navigation in `NinjaWebView.loadUrl()` continues to use the existing request-header path, and intercepted link navigation in `NinjaWebViewClient.handleUri()` also passes the same header map when loading HTTP(S) URLs.

### TDD / implementation
- TDD contract: `app/src/test/java/de/baumann/browser/unit/GpcPolicyTest.java`.
- Policy: `app/src/main/java/de/baumann/browser/unit/GpcPolicy.java`.
- Header name: `Sec-GPC`.
- Enabled value: `1`.
- Disabled state: no GPC header value.
- Existing preference key: `gpc_enabled`.
- No networking architecture, interceptor, dependency, or new transport layer was introduced.

Commit sequence:
- `a764a4e3cdcea92aa4a1ef483257f314205b32b9` — GPC setting labels.
- `966c578f7f6a790b2e2980245adac242a70f89f0` — TDD contract.
- `e96298cbb2c0d0f3d9813ddc913bcbb98b348e2f` — GPC policy implementation.
- The preceding branch commits also wired the GPC signal into navigation headers.

### Verification
- SOURCE-VERIFIED: complete for the policy and current request-header wiring.
- TEST-VERIFIED: complete for the deterministic `GpcPolicyTest` contract.
- CI-VERIFIED: **pending** for current HEAD `e96298cbb2c0d0f3d9813ddc913bcbb98b348e2f`; no associated Actions run was returned when checked.
- ANDROID-RUNTIME-VERIFIED: not performed; no Android runtime is available.
- DOCUMENTED: this state record and the master map/resume command are synchronized in the next documentation checkpoint commit.

## Architecture boundary
The P1 profile mechanism still does not isolate process-wide `CookieManager`, Chromium WebView disk storage, the default SharedPreferences store as a whole, history, or bookmarks. No full browser-storage isolation is claimed.

No multi-process architecture, WebView data-directory switching, extension runtime, proxy/Tor stack, WebRTC subsystem, DNS-over-HTTPS stack, fingerprinting subsystem, or AI runtime has been added.

## Next execution step
**CI-verify the current GPC implementation at HEAD `e96298cbb2c0d0f3d9813ddc913bcbb98b348e2f`; inspect the resulting unit-test workflow before selecting P2 Step 4.**

## Last updated
2026-09-02 — workflow/tooling protocol updated; current HEAD `e96298cbb2c0d0f3d9813ddc913bcbb98b348e2f`; GPC source/test complete, CI verification pending, Android runtime deferred.
