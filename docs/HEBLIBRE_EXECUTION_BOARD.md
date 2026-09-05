# HebLibre Persistent Execution Board

## Objective
A durable, agent-neutral task board. This file answers: what is done, what is pending, what is intentionally deferred, and what must happen next.

## Status keys
- `DONE-SOURCE`: source verified.
- `DONE-TEST`: deterministic tests verified.
- `DONE-CI`: CI verified against the relevant source checkpoint.
- `DONE-EMULATOR`: Android emulator runtime verified.
- `DONE-PHYSICAL`: physical target-device verified.
- `PARTIAL`: usable core exists but end-to-end feature is incomplete.
- `DEFERRED`: intentionally postponed.
- `POLICY-DECISION`: do not change until explicit contract.

## Completed bounded scope
| Area | Status | Evidence / notes |
|---|---|---|
| Tracking/query parameter cleanup | DONE-CI | P2.1; conservative `UrlTrackerCleaner`. |
| HTTPS-only | DONE-CI | P2.2. |
| Global Privacy Control | DONE-CI | P2.3. |
| Desktop Mode | DONE-CI | P2.4; deterministic UA policy. |
| Screenshot Protection | DONE-CI | P2.5; live `FLAG_SECURE` preference. |
| Search bangs | DONE-CI | P2.6. |
| Camera/microphone permission guard | DONE-CI | P2.7; bounded WebView permission policy. |
| Third-party cookie control | DONE-CI | P2.8; compatibility-preserving default. |
| Geolocation guard | DONE-CI | P2.9. |
| Save-Data | DONE-CI | P2.10. |
| Global settings search | DONE-CI | P2.11. |
| Download cookie privacy | DONE-CI | Both main download and Save As paths are policy-gated. |
| Profile-scoped whitelists | DONE-CI | Active and legacy whitelist transfer paths use the active normalized profile. |
| Tab reorder core | DONE-CI | `TabOrderPolicy` + `BrowserContainer.move()` + identity tests. |
| Remote-content default consistency | DONE-CI | `sp_remote` fallback aligned with declared default. |
| Emulator smoke | DONE-EMULATOR | GitHub-hosted emulator execution completed successfully on a CI checkpoint; not physical-device proof. |

## Current physical validation checklist
Status: `PENDING`

Validate together on the target Android phone:
- launch/relaunch and process restoration;
- normal navigation, external intents, back/forward, search and bangs;
- HTTPS-only and meaningful query parameter cleanup;
- GPC and Save-Data headers where applicable;
- desktop/custom User-Agent interaction;
- screenshot protection on/off;
- camera/microphone permission blocking and allowed-path behavior;
- geolocation permission behavior;
- third-party cookie behavior;
- download with cookies enabled/disabled and Save As;
- profile switching and whitelist export/import isolation;
- tab selection, close, overview, and reorder core behavior;
- settings search and setting persistence;
- history/bookmarks/home and clear-data flows;
- general regression, rendering, lifecycle, and crash behavior.

## Runtime defect protocol
When physical testing discovers defects:
1. Record the exact symptom and reproduction steps.
2. Map each symptom to the smallest source seam.
3. Batch related fixes rather than repeatedly installing APKs for isolated tweaks.
4. Add deterministic tests where possible.
5. Run CI on the consolidated fix checkpoint.
6. Repeat physical validation once after the consolidated fix set.

## Deferred backlog
### DEFERRED — MEDIUM
- QR scanner.
- PWA support.
- True tab hierarchy.
- Dedicated tab reorder UI that preserves long-click close semantics.
- Broader download-manager UX enhancements.
- Translation service integration.
- Full-page/Markdown export.

### DEFERRED — ARCHITECTURAL
- Complete profile/WebView storage isolation.
- Isolated tabs.
- Multi-window architecture.
- Per-container proxy/Tor.
- DNS over HTTPS.
- Broad fingerprinting defenses.
- Full WebRTC privacy/engine changes.
- Extensions/uBlock runtime.
- On-device AI.
- Broader tracking-protection engine.

### NOT TARGETED
- Reader Mode in the current P2 cycle; no bounded dependency-free extraction seam established.

## Policy/architecture decisions not to reopen silently
- SSL certificate override behavior.
- Application-level cleartext traffic.
- Automatic backup semantics for `Ninja4.db`.
- Coupling of file-origin access and DOM storage under `sp_remote`.

See `docs/HEBLIBRE_DECISION_LOG.md` for rationale and supersession rules.

## Current next executable step
**Physical target-device validation** after verifying that the active `genspark-dev` HEAD still corresponds to the documented source/CI checkpoint. Any runtime fixes become a consolidated follow-up cycle.
