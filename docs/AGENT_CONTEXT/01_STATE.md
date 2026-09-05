# HebLibre Agent Context — Current State

## Repository
`brasilia736211600-netizen/HebLibre` — active branch `genspark-dev`.

## Clean code checkpoint
`5fffd65e80e2a616a5273befe7cdca6309441490`.

Current work after that checkpoint is documentation/audit only unless a later context update explicitly records a verified source change.

## Completed verified scope
P2.1–P2.11: tracking-parameter cleanup, HTTPS-only, GPC, desktop mode, screenshot protection, search bangs, WebRTC camera/mic permission guard, third-party-cookie policy, geolocation guard, Save-Data, global settings search.

Also completed/verified: profile-scoped whitelist persistence/in-memory isolation, download-cookie privacy control, tab reorder core, remote-content default consistency.

## Current bounded candidate
Whitelist import/export is still hard-coded to the default profile instead of using the active profile identity. This is the highest-value small correctness candidate.

## Explicitly deferred / policy-gated
Do not change SSL certificate override semantics, Android backup of browser DB, application cleartext policy, or the coupling of file-origin access with DOM storage without an explicit product/architecture decision.

Deferred larger features include QR scanner, PWA, true tab hierarchy, multi-window, broader tracking protection, DoH, broad fingerprinting defenses, full WebRTC privacy, complete profile storage isolation, isolated tabs, per-container proxy/Tor, extensions/uBlock, on-device AI, and Reader Mode.

## UX constraint
Tab overview remains the existing LinearLayout path. Normal click selects a tab; long-click closes it. Reorder UI is not wired until a non-breaking complete mutation path exists.

## Validation
Prefer JVM tests and CI. Android runtime validation is reserved for the final consolidated device pass.