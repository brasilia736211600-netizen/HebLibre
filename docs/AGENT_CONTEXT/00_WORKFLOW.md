# HebLibre Agent Context — Workflow

GitHub is the sole source of truth. Ignore chat history, memory, and unstated local state.

Execution: READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → REVIEW → COMMIT → SAVE STATE.

Keep these verification levels separate: SOURCE-VERIFIED, TEST-VERIFIED, CI-VERIFIED, ANDROID-RUNTIME-VERIFIED, DOCUMENTED.

Use YAGNI. Prefer small, deterministic, compatibility-preserving changes with JVM tests. Parallelize only independent work; serialize dependent mutations. Do not invent missing APIs or claim tests/reviews/CI/runtime results without evidence.

Do not repeatedly build/install the APK. Finish source work, tests, CI, review, and documentation first; use one consolidated Android validation phase at the end.

Before changing an unresolved product/security policy, verify the existing contract and document the decision instead of making a speculative behavior change.

After every meaningful completed unit: verify the diff, commit it, and update this context directory with the new state and next actionable unit.