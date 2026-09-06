# HebLibre Continuation Checkpoint — 2026-09-06

## Current branch
`genspark-dev`

## Latest application/source checkpoint
`086c7038faa5bbb8b9d2a24f392edfe9cce3a07e`

## Completed in this continuation
- Re-read the GitHub control-plane documents and active product scope.
- Re-verified that no application source changes were introduced during the Actions investigation.
- Compared HebLibre's last successful Runtime Smoke with its current pre-step failures.
- Re-ran the exact failed Unit job; the new attempt again failed before any step.
- Verified the temporary minimal `ubuntu-latest` probe also failed before any step.
- Compared the same-account WebLibre control case: WebLibre is public and its current Actions run successfully received a hosted runner and executed steps.
- Confirmed HebLibre is private while WebLibre is public.
- Researched current GitHub billing documentation: standard hosted runners are free for public repositories; private repositories consume the included Actions quota (2,000 minutes/month on GitHub Free), and usage is blocked after the allowance is exhausted when no valid payment method is configured.
- Updated GitHub Issue #3 to record private-repository quota/billing as the leading explanation, with backend Actions failure retained only as a secondary possibility because account usage telemetry is not exposed by the available connector.
- Removed the temporary runner probe and stopped speculative runner-label churn.

## Verification classification
- SOURCE-VERIFIED: application/test checkpoint `086c7038...` remains the release-candidate source basis.
- TEST-VERIFIED: no fresh hosted test pass exists after `086c...`.
- CI-VERIFIED: current HebLibre Unit/Smoke attempts fail before execution; the WebLibre control repository demonstrates the same account can execute hosted Actions.
- ANDROID-RUNTIME-VERIFIED: historical HebLibre baseline Runtime Smoke `33994758706` passed on an older checkpoint only.
- ARTIFACT-VERIFIED: historical HebLibre artifact `9977732103` was downloaded and checksum-verified; not current release evidence.
- DOCUMENTED: state files and GitHub Issue #3 contain the current diagnosis.

## Current diagnosis
The strongest current explanation is repository visibility / billing entitlement rather than application code:

- `WebLibre`: public repository; current hosted Actions job `101530796057` successfully allocated a runner and executed steps.
- `HebLibre`: private repository; current jobs fail before any step (`steps: null`).
- GitHub documents that public-repository standard runners are free, while private-repository standard runners consume the account's monthly Actions allowance. GitHub Free includes 2,000 minutes/month, and usage beyond the allowance is blocked when no valid payment method is present.
- Therefore the immediate external action is to inspect HebLibre owner's Actions usage/billing state. If the included private-repository quota is exhausted, restore paid/valid billing or wait for the allowance reset. If quota remains, the remaining suspect is a GitHub-side Actions repository/backend state defect.

The available connector cannot directly read private account billing/usage telemetry, and repository visibility must not be changed automatically because making HebLibre public would expose its source.

## Product boundary
Profile metadata/catalog, profile-scoped app-owned records, session restore, curated profile-owned browser/privacy preferences, transfer/import/export, encrypted transfer, transactional import/deletion, and debug-only smoke harnesses are implemented. UI-only preferences remain global by explicit boundary. Per-profile proxy routing, complete WebView data-directory isolation, and same-URL in-process switching remain intentionally deferred.

## Release gate
After Actions entitlement is restored, perform one consolidated chain: hosted Unit + Runtime Smoke → download exact current APK + checksum → emulator smoke → final physical-device validation. Only then classify release-ready.

## Next executable slice
Do not add more runner probes or mutate runner labels. Resolve/verify the private-repository Actions entitlement first; then consume one current runner allocation for the final test/build/smoke chain.