# HebLibre Workflow State

## Canonical continuity
GitHub is the source of truth. Chat history, agent memory, and unstated local state are non-authoritative.
Repository: `brasilia736211600-netizen/HebLibre`
Branch: `genspark-dev`
Default branch: `l10n_crowdin`

Execution protocol:
`READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → COMMIT → SAVE STATE`

Verification levels: SOURCE-VERIFIED, TEST-VERIFIED, CI-VERIFIED, ANDROID-RUNTIME-VERIFIED, DOCUMENTED. Never conflate them.

## Current repository state
- Current HEAD: `883a57ebeb1354b1c7f7ac3fc3f455c00a290f29`
- Latest commit: `docs: add canonical HebLibre master project map`
- Working tree was clean before the documentation update; the update changed documentation only.
- `docs/HEBLIBRE_MASTER_PROJECT_MAP.md` now exists and is the canonical high-level project map.
- CI read/check access remains limited by the known GitHub App permission gap; do not claim CI status without direct evidence.

## Completed engineering
1. Build/toolchain recovery: Gradle 5.4.1 / AGP 3.5.2 baseline recovered with JDK 11, compile SDK 29, build-tools 28.0.3; debug build verified locally. Baseline `e11562fd42639d5b12e5468c14d461523aa96529`.
2. Minimal JUnit4 harness: `BrowserUnit.isURL` characterization; 4 tests green. `6211fc57cc5589f24106df0c1aa2a4e7b6ae2058`.
3. CI workflow: JDK 17 for SDK tooling, JDK 11 for Gradle; known successful run `33459477325` for `9aca596d6361104d7982563c3784512caab98cfa`.
4. P1 Step 1: pure-Java `UrlMatcher.containsAnyDomain`; 9/9 tests.
5. P1 Step 2: instance-scoped `BrowserContainer`; 11/11 tests.
6. P1 Step 3: `ProfileScopedWhitelist` keyed by profile id; 14/14 tests.
7. P1 Step 4: `Ninja4.db` whitelist `PROFILE_ID` migration v4→v5, profile-aware CRUD and callers; 17/17 JVM tests plus main-source compilation. Implementation `4f0f97b955ce1e8b3dd039b365f056e48b4c00de`; state was then recorded in `2a561adb40fad1408e369f3de03b4abdd375b2a8`.

## Architecture boundary
Profile-aware whitelist infrastructure now exists in memory and persistence, but production code still uses only the `default` profile. `BrowserActivity` is the process-lifetime root and `NinjaWebView` is the per-tab construction site. SharedPreferences, CookieManager, Chromium WebView disk storage, history, and bookmarks remain global/unpartitioned.

No multi-process architecture or `WebView.setDataDirectorySuffix` solution has been introduced. Do not add these, account systems, fingerprinting controls, proxy/WebRTC/UA spoofing, or unrelated dependencies/refactors without demonstrated need (YAGNI).

## Current phase
`P1 — Profile / Identity Isolation — implementation`

P1 Steps 1–4 are complete. No user-facing profile selection exists yet.

## Single next execution step
**P1 Step 5 — identity contract decision and smallest implementation seam.** First verify the live `genspark-dev` HEAD and inspect the exact current profile constructor/call-site graph. If threading an always-`default` value would be pure no-op plumbing, do not implement it merely for completeness. Prefer a small, deterministic, JVM-testable identity contract only if it has concrete downstream value. Do not begin profile UI/account management until the identity contract is justified.

## TDD / YAGNI rules
- Write a deterministic failing test before new behavior when feasible; do not manufacture RED for behavior that already exists.
- Android runtime testing is not required to unblock pure-JVM work, but never claim runtime verification without a device/emulator result.
- Do not touch unrelated files.
- Do not redo completed verified work.
- Genspark credits are exhausted. Continue with available GitHub/local capabilities and never claim Genspark execution.

## Save-state contract
After every substantive step: verify branch + exact HEAD; record tests, CI and runtime evidence; record diff scope; update this file; commit/push; verify local HEAD = remote HEAD; record exactly ONE next execution step.

## Last updated
2026-09-02 — canonical state and master map reconciled for credit-independent continuation. Latest HEAD `883a57ebeb1354b1c7f7ac3fc3f455c00a290f29`.
