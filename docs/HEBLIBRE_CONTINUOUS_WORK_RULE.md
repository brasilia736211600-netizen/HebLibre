# HebLibre Continuous Work Rule

This file is a canonical reminder for new agents/chats.

When the user says `استمر` / `continue`, keep working internally for as long as useful. Do not send interim progress messages. Investigate, verify, diagnose, prioritize, implement, test, review, reconcile CI, and fix discovered problems before reporting.

Use parallel execution only for genuinely independent work units. Serialize dependent branch mutations. Before any user-facing progress/update message, complete at least **10 minutes of productive project work** in the current continuation cycle whenever tool/runtime conditions permit.

User-facing checkpoints must be short and contain exactly: completed work, current blocker/problem or explicitly none, overall project position, and one next execution step.

Do not build/install/test the Android APK after each feature. Source verification, deterministic JVM tests, CI, review, and documentation come first. Android build/install/runtime verification is the **final consolidated validation phase** as far as reasonably possible; collect runtime regressions there, fix them together, and rerun only as necessary.

GitHub is the source of truth. Preserve distinct verification levels: SOURCE-VERIFIED, TEST-VERIFIED, CI-VERIFIED, ANDROID-RUNTIME-VERIFIED, DOCUMENTED.
