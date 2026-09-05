# HebLibre Persistent Decision Log

This is the durable record of important project decisions. A future agent must read this before reopening a deliberately deferred or rejected path.

## D-001 — GitHub is the continuity authority
**Decision:** GitHub repository state is authoritative; chat/model/plugin/local memory is not.
**Reason:** Sessions and agents can change, but repository state must remain reproducible.
**Implication:** Every meaningful task ends with commit + workflow-state update.

## D-002 — One canonical execution loop
**Decision:** Use `READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → REVIEW → COMMIT → SAVE STATE`.
**Reason:** Prevents silent assumptions and incomplete handoffs.

## D-003 — Verification levels remain distinct
**Decision:** SOURCE, TEST, CI, Android emulator/physical runtime, and DOCUMENTED are separate evidence classes.
**Reason:** A source check or CI pass cannot prove physical-device behavior.

## D-004 — YAGNI and compatibility first
**Decision:** Prefer the smallest change that solves a demonstrated problem, and avoid compatibility-breaking behavior unless explicitly adopted.
**Reason:** HebLibre is a mature FOSS Browser-derived application with existing user-facing behavior.

## D-005 — Android testing is consolidated
**Decision:** Do not repeatedly build/install/test during feature development.
**Reason:** User wants development work completed before repeated phone testing.
**Implication:** source + JVM + CI + review + docs first, then consolidated Android validation.

## D-006 — Third-party cookie default is not changed opportunistically
**Decision:** Do not silently switch the runtime default to blocking third-party cookies.
**Reason:** Current runtime fallback preserves compatibility; resource copy was stronger than actual behavior and requires explicit product decision.

## D-007 — SSL certificate override remains a policy decision
**Decision:** Do not replace the current interactive `handler.proceed()` path with unconditional cancellation without a deliberate security/product contract.
**Reason:** Hardening is desirable but behavior-changing.

## D-008 — Application cleartext capability remains explicit
**Decision:** Do not remove `android:usesCleartextTraffic="true"` merely because HTTPS-only exists.
**Reason:** HTTPS-only is opt-in and global removal would be compatibility-breaking.

## D-009 — `sp_remote` coupling is not split opportunistically
**Decision:** Do not separate file-origin access and DOM-storage controls until a proper product/architecture contract exists.
**Reason:** They currently share one legacy preference and splitting them changes configuration semantics.

## D-010 — Android backup of `Ninja4.db` is an architecture/product decision
**Decision:** Do not change backup semantics opportunistically.
**Reason:** `Ninja4.db` contains privacy-relevant browser state and backup behavior affects restore expectations.

## D-011 — Tab reorder core is safe; UI remains partial
**Decision:** Keep `TabOrderPolicy` + `BrowserContainer.move()` as the verified core; do not repurpose long-press because it currently closes tabs.
**Reason:** Preserves established UX and avoids incomplete UI/controller mutation paths.

## D-012 — Download cookie control preserves compatibility
**Decision:** The download cookie policy defaults to enabled but allows users to disable sending WebView cookies with downloads.
**Reason:** Avoids breaking authenticated downloads while providing an explicit privacy control.

## D-013 — Profile isolation boundary is intentionally limited
**Decision:** Profile-aware whitelist persistence and active profile identity are implemented, but WebView/Chromium storage, CookieManager, history, and bookmarks are not treated as fully partitioned profiles.
**Reason:** Complete storage/process isolation is architectural work and must not be implied by whitelist isolation.

## D-014 — Reader Mode is not targeted in current P2
**Decision:** Do not add speculative HTML/JS extraction logic without a bounded seam.
**Reason:** Current native WebView architecture did not expose a safe dependency-free reader-extraction contract.

## D-015 — QR scanner, PWA, hierarchy, multi-window are deferred
**Decision:** Preserve these as documented backlog items.
**Reason:** Each requires additional camera/lifecycle/model/architecture work beyond the current bounded P2 scope.

## D-016 — Rejected experiments are not features
**Decision:** A reverted or incomplete experiment is not counted as implemented, even if its temporary CI run passed.
**Reason:** Prevents stale claims and accidental resurrection of unsafe code.

## D-017 — Physical device is the final runtime authority for release confidence
**Decision:** GitHub-hosted emulator smoke is useful evidence but does not replace physical target-device validation.
**Reason:** Device-specific WebView, Android, rendering, storage, and lifecycle behavior can differ.

## D-018 — 2026 product scope supersedes older chat-only feature requests
**Decision:** The active product direction is defined by `docs/HEBLIBRE_PRODUCT_SCOPE_2026-09-06.md`.
**Reason:** The feature backlog had accumulated across conversations and needed one durable, explicit scope.
**Implication:** Older chat-only feature lists are historical context, not active requirements.

## D-019 — Anti-detect inspiration is bounded by legitimate privacy use
**Decision:** HebLibre may adopt legitimate profile-management, privacy, session, configuration, and isolation patterns observed in leading anti-detect and social/multi-profile browsers, but must not implement functionality whose primary purpose is defeating fraud systems, security controls, bans, identity verification, or platform enforcement.
**Reason:** Preserve legitimate privacy and multi-profile value without turning the browser into a security-evasion tool.

## D-020 — Lightweight performance is a first-class product constraint
**Decision:** Every new feature must justify memory, startup, storage, dependency, and lifecycle cost before implementation.
**Reason:** The product target is a lightweight, fast, reliable Android browser.
**Implication:** Large frameworks, always-on services, cloud dependencies, and speculative abstractions require explicit justification.

## D-021 — Durable continuity is a release requirement
**Decision:** Every material step must be recoverable from GitHub without chat context.
**Reason:** Sessions can terminate, freeze, lose network, or otherwise interrupt work.
**Implication:** Persist material decisions, blockers, test conclusions, source checkpoints, CI evidence, and the next executable step in the durable control plane.

## Change control
To supersede any decision above, create a new dated decision entry explaining the new contract, evidence, affected files, tests/CI plan, and migration/compatibility impact. Do not silently overwrite historical rationale.
