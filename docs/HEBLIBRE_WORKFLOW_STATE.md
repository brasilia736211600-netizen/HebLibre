# HebLibre Workflow State

## Canonical continuity
GitHub is the source of truth. Chat history, model memory, plugin memory, and unstated local state are non-authoritative.
Repository: `brasilia736211600-netizen/HebLibre`
Branch: `genspark-dev`
Default branch: `l10n_crowdin`

## Communication language
All human-facing conversation about HebLibre is in Arabic unless the user explicitly requests another language. Repository source code, identifiers, commit messages, CI output, and filenames remain in their native/project language.

## Mandatory continuity documents
- `docs/HEBLIBRE_AI_AGENT_CONTRACT.md`
- `docs/HEBLIBRE_MASTER_PROJECT_MAP.md`
- `docs/HEBLIBRE_EXECUTION_BOARD.md`
- `docs/HEBLIBRE_DECISION_LOG.md`
- `docs/HEBLIBRE_RESUME_COMMAND.md`
- `docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md`

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
- Live `genspark-dev` HEAD: `72550bbaa5dfd18d0bc8345b03f3bc0015bf1835`.
- Latest verified application-source fix: `247768c4e2e442fcb9b42d299d8cf00d3c24b81b` — whitelist import/export resolves the active normalized profile instead of hard-coding the default profile.
- Consolidated Unit Tests run `33985143542` passed for that source checkpoint.
- Current HEAD is 9 commits beyond Android emulator checkpoint `48300a4ad2c366e2987cea9949de8e722089c962`; GitHub compare reports those 9 commits changed documentation/control-plane files only. No application-source file changed after the emulator checkpoint.
- Android Runtime Smoke run `33988773661` (run #15) completed successfully on `48300a4ad2c366e2987cea9949de8e722089c962`; because the current HEAD has no application-source changes after that checkpoint, the current application source retains that emulator runtime evidence.
- Latest current-HEAD Unit Tests run `33990897505` completed successfully.
- No unverified third-party-cookie behavior change remains; the compatibility-preserving default is retained.

## Engineering checkpoint
The bounded P2 implementation is complete at the source/test/CI level. Implemented areas include P2.1–P2.11, download-cookie privacy, profile-aware whitelist transfer, tab reorder core, and remote-content default consistency.

The tab overview remains `ScrollView` + `LinearLayout`. `AlbumItem` uses normal click for selection and long-click for tab close. Tab reorder core uses `TabOrderPolicy` + `BrowserContainer.move()` and preserves controller/WebView identity; dedicated reorder UI remains deferred so long-click close behavior is not broken.

Whitelist transfer is profile-aware in both the active settings route and legacy `BrowserUnit` helpers. Bookmark transfer remains unchanged.

Download cookie privacy is enforced in both main download and Save As paths with a compatibility-preserving enabled default.

`sp_remote` declared/default behavior is consistent across preference initialization and navigation.

## Security/architecture decisions
Do not silently modify:
- SSL certificate-error override behavior.
- Application-level `android:usesCleartextTraffic` policy.
- Automatic Android backup semantics for `Ninja4.db`.
- Coupling of file-origin access and DOM storage under `sp_remote`.
- Complete profile/WebView storage isolation.
- DoH, per-container proxy/Tor, broad fingerprinting defenses, full WebRTC privacy, extensions/uBlock, and on-device AI.

See `docs/HEBLIBRE_DECISION_LOG.md` and `docs/HEBLIBRE_SECURITY_AUDIT_2026-09-03.md`.

## Android validation
### Completed
- ARM debug APK packaging: CI-verified.
- GitHub-hosted Android emulator smoke: successful on `48300a4ad2c366e2987cea9949de8e722089c962`.
- Current HEAD has no application-source changes after that emulator checkpoint.

### Final gate
Physical target-device validation remains the only remaining release-confidence gate for the bounded scope.

The consolidated pass should cover: launch/relaunch and restoration; navigation/external intents/back-forward; search/bangs; tracking cleanup; HTTPS-only; GPC; Save-Data; custom/desktop UA interaction; screenshot protection; camera/microphone and geolocation permissions; third-party cookies; download/Save As; profile switching + whitelist import/export isolation; tabs/close/overview/reorder core; settings search/persistence; history/bookmarks/home/clear-data; lifecycle/rendering/crash regression.

Any runtime defect follows one consolidated cycle: reproduce → smallest seam → batch related fixes → deterministic tests → CI → physical recheck.

## Deferred work
QR scanner, PWA, true tab hierarchy, multi-window, broader tracking protection, DoH, broad fingerprinting defenses, full WebRTC privacy, complete profile storage isolation, isolated tabs, per-container proxy/Tor, extensions/uBlock, on-device AI, Reader Mode, dedicated tab-reorder UI, broader download-manager UX, translation-service integration, and full-page/Markdown export remain deferred by design.

## Session handoff rule
Every meaningful session ends with this file synchronized to the live HEAD, exact evidence, unresolved decisions, and one next executable step.

## Last updated
2026-09-06 — reconciled live HEAD `72550...` with source checkpoint `247768...`, current Unit Tests run `33990897505`, and successful Android emulator smoke run `33988773661`; confirmed the post-emulator HEAD delta is documentation/control-plane only and reduced the project to the final physical-device validation gate.
