# HebLibre Workflow State

## Canonical continuity
GitHub is the source of truth. Chat history, agent memory, plugin memory, and unstated local state are non-authoritative.
Repository: `brasilia736211600-netizen/HebLibre`
Branch: `genspark-dev`
Default branch: `l10n_crowdin`

## Mandatory continuity documents
- `docs/HEBLIBRE_AI_AGENT_CONTRACT.md` — durable rules for every AI/human agent.
- `docs/HEBLIBRE_MASTER_PROJECT_MAP.md` — scope, architecture boundary, completed work, and deferred backlog.
- `docs/HEBLIBRE_EXECUTION_BOARD.md` — operational task board and final validation checklist.
- `docs/HEBLIBRE_DECISION_LOG.md` — durable rationale for important decisions and rejected/deferred paths.
- `docs/HEBLIBRE_RESUME_COMMAND.md` — copy/paste bootstrap for a new chat/session/agent.
- `docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md` — separate WebLibre feature/design comparison; not continuity authority.

## Execution protocol
`READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → REVIEW → COMMIT → SAVE STATE`

## Verification levels
- `SOURCE-VERIFIED`: current source inspected directly.
- `TEST-VERIFIED`: deterministic automated tests passed.
- `CI-VERIFIED`: GitHub Actions passed for the relevant source checkpoint.
- `ANDROID-RUNTIME-VERIFIED`: application executed on Android; emulator and physical-device evidence are distinct.
- `DOCUMENTED`: decision/result persisted in repository documentation.

Never conflate these levels.

## Current repository state
- Latest verified application-source fix: `247768c4e2e442fcb9b42d299d8cf00d3c24b81b` — whitelist import/export resolves the active normalized profile instead of hard-coding the default profile.
- Consolidated Unit Tests run `33985143542` passed for that source checkpoint.
- Subsequent commits are documentation/CI hardening only; no unverified third-party-cookie behavior change remains.
- Current branch HEAD at the time of this checkpoint update is the documentation/CI sequence headed by `69921edf7ec741eafe51956a7c51c89e317bb20b`; always verify the live branch ref before execution.
- Android emulator smoke has completed successfully in GitHub Actions on the current validation sequence. This is emulator evidence, not physical target-device proof.
- P2.1–P2.11, download-cookie privacy, tab reorder core, remote-content default consistency, and whitelist profile consistency are recorded as SOURCE/TEST/CI verified.

## Engineering checkpoint
The tab overview remains `ScrollView` + `LinearLayout`. `AlbumItem` uses normal click for selection and long-click for tab close. Tab reorder core is implemented via `TabOrderPolicy` + `BrowserContainer.move()` and is tested; dedicated reorder UI remains deferred so the established long-click close behavior is not broken.

Whitelist transfer is profile-aware in both the active settings path and legacy `BrowserUnit` helpers. Bookmark transfer remains unchanged.

Download cookie privacy is enforced in both the main download and Save As paths with a compatibility-preserving enabled default.

`sp_remote` declared/default behavior is consistent across preference initialization and navigation.

## Security/architecture decisions
Do not silently modify the following:
- SSL certificate-error override behavior.
- Application-level `android:usesCleartextTraffic` policy.
- Automatic Android backup semantics for `Ninja4.db`.
- Coupling of file-origin access and DOM storage under `sp_remote`.
- Complete profile/WebView storage isolation.
- Architectural networking/privacy features such as DoH, per-container proxy/Tor, broad fingerprinting defenses, full WebRTC privacy, extensions/uBlock, and on-device AI.

See `docs/HEBLIBRE_DECISION_LOG.md` for rationale.

## Android validation
### Completed
- ARM debug APK packaging was validated by CI.
- GitHub-hosted Android emulator smoke executed successfully, including app execution checks.

### Pending
Physical target-device validation remains the final release-confidence gate for the current bounded scope.

The consolidated device pass should cover navigation/search, tracking cleanup, HTTPS-only, GPC, Save-Data, desktop/custom UA interaction, screenshot protection, media/geolocation permissions, third-party cookies, downloads/Save As, profile/whitelist transfer, tabs/close/reorder core, settings search, history/bookmarks/home/clear-data, lifecycle, rendering, and crash/regression behavior.

If runtime defects are found: record reproduction → map to smallest seam → batch related fixes → add tests where possible → CI → one final physical-device recheck.

## Deferred work
QR scanner, PWA, true tab hierarchy, multi-window, broader tracking protection, DoH, broad fingerprinting defenses, full WebRTC privacy, complete profile storage isolation, isolated tabs, per-container proxy/Tor, extensions/uBlock, on-device AI, Reader Mode, and dedicated tab-reorder UI remain explicitly deferred.

## Session handoff rule
At the end of every meaningful session, update this file with the live HEAD, exact changes, test/CI/runtime evidence, unresolved decisions, and one next executable step. Update the master map and execution board when scope changes.

## Last updated
2026-09-05 — installed the durable AI-agent continuity contract, persistent decision log, execution board, and strengthened resume bootstrap so future chats/agents can recover from GitHub without relying on conversational memory.
