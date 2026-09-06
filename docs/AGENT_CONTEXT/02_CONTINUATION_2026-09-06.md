# HebLibre Continuation Checkpoint — 2026-09-06

## Current branch
`genspark-dev`

## Latest application/source checkpoint
`086c7038faa5bbb8b9d2a24f392edfe9cce3a07e`

## Completed in this continuation
- Re-verified the GitHub repository and resumed from the interruption without relying on unstated local state.
- Confirmed current Unit Tests, Android Runtime Smoke, and Runner Probe runs terminate before the first runner step; no current APK was produced by those runs.
- Confirmed the Runtime Smoke workflow contains the earlier JDK 17 emulator-tooling correction from merged PR #2; no speculative CI rewrite was made.
- Downloaded the latest available historical x86_64 smoke APK through the GitHub Actions artifact API, independently verified its SHA-256 against the embedded checksum, and inspected its package structure. This artifact is historical and is not current release evidence.
- Strengthened active-profile deletion so the legacy global preference view immediately returns to the default profile and requests the existing restart behavior.
- Extended Android Runtime Smoke to exercise active-profile deletion/default-preference restoration.
- Added an 8 MiB maximum profile-transfer input size before structural parsing/decryption to limit oversized-file memory amplification.
- Added deterministic JVM coverage for oversized transfer rejection.
- Preserved the existing backward-compatible profile transfer format, AES-GCM encryption, transactionality, and curated profile-owned preference bridge.

## Verification classification
- SOURCE-VERIFIED: current source/test checkpoint `086c7038...` is present on `genspark-dev`.
- TEST-VERIFIED: current hosted test execution is blocked at runner startup; no fresh hosted pass exists for `086c...`.
- CI-VERIFIED: Unit, Runtime Smoke, and Runner Probe failures observed on the current sequence are pre-step runner allocation/startup failures (`steps: null`), not application test failures.
- ANDROID-RUNTIME-VERIFIED: historical baseline Runtime Smoke passed on an older checkpoint; current profile/preference additions remain unverified on a fresh emulator.
- ARTIFACT-VERIFIED: historical artifact `9977732103` was downloaded and checksum-verified locally; it must not be treated as the current build.
- DOCUMENTED: this file and `01_STATE.md` are synchronized with the current hardening wave.

## Current product boundary
Profile metadata/catalog, profile-scoped app-owned records, session restore, curated profile-owned browser/privacy preferences, transfer/import/export, encrypted transfer, transactional import/deletion, and debug-only smoke harnesses are implemented. UI-only preferences remain global by explicit boundary. Per-profile proxy routing, complete WebView data-directory isolation, and same-URL in-process switching are not claimed complete.

## Release gate still open
A current successful GitHub Actions runner is still required to produce the exact current x86_64 APK. After that: verify Unit + Runtime Smoke, download/checksum the exact artifact, execute one consolidated emulator smoke gate, then perform physical-device validation. Only that evidence chain permits release-ready classification.
