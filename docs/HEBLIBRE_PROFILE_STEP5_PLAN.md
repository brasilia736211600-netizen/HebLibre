# P1 Step 5 — Profile Selection Plan

Status: planning only; no production profile UI has been implemented yet.

## Evidence-backed constraint
Existing profile-aware whitelist infrastructure accepts arbitrary profile ids, but current production constructors still use the default profile. The browser also still has global SharedPreferences, process-wide CookieManager, shared Chromium WebView storage, and unpartitioned history/bookmarks. Therefore a profile selector at this stage can honestly control only the profile-scoped application whitelist state; it must not be presented as full browser storage/cookie identity isolation.

## Smallest useful contract
A profile-selection feature should have exactly one durable application-level source of truth for the current profile id, defaulting to `RecordUnit.DEFAULT_PROFILE_ID`, and should pass that id to the already profile-aware whitelist construction path. The contract must be independently testable without Android runtime.

## Acceptance criteria
1. Default behavior remains profile id `default`.
2. A non-default profile id can be selected and persisted.
3. Newly created whitelist objects use the selected id.
4. Persisted whitelist reads/writes use the selected id.
5. Switching profiles does not claim to isolate cookies, WebView disk storage, history, bookmarks, or SharedPreferences.
6. No multi-process architecture, account system, fingerprinting controls, proxy work, or unrelated dependency changes are introduced.

## Next implementation rule
Before touching production UI code, add the smallest pure-Java failing test for the identity preference/normalization contract. Only after that contract is green should UI wiring begin.
