# Tracelog

## 2026-10-26 — 2.1.0-modded / Minecraft 1.21.1

### Implementation

- Store chalk modifiers as a persistent, network-synchronized integer data component. The mark codec stores Waxed, Permanent and Shining with defaults for compatibility with existing saved marks.
- Resolve drawing properties from the selected chalk, including chalk carried inside a Chalk Box.
- Add a shapeless custom crafting serializer for dyeing, upgrades, repair and calcite/redstone crafting. Preserve the input stack's components; reject duplicate upgrades and invalid ingredients. Honey and dragon's breath return glass bottles.
- Resolve vanilla/mod DyeItem colors and Dyenamics' public `getDyeColor().getColorComponentValue()` API. Other compatible dye APIs and tagged dyes carrying a dyed_color component are supported without mandatory dependencies.
- Exclude chalk from the armor-dyeing tag so armor-coloring recipes cannot override the fixed Redstone Chalk color. Set its drawing/tint color independently of dyed_color.
- Add waterlogging to marks and preserve water when removing the last underwater mark. Remove non-waxed marks when flooded. Compute block light across all marks in a block, with Shining taking priority at level 15.
- Send continuous mining attempts for Permanent marks; the server checks the same player, position, face and mark across game ticks. Repeated packets in one tick do not speed removal up. Creative players keep immediate removal.
- Remove advancement resources and advancement hooks. Neutralize legacy symbol requirements on load and stop the bundled Mortaar supporter-list startup query.

### Build and publication

- Required toolchain: full JDK 21, repository Gradle wrapper (9.6.1). The original cloud image provided a JRE, so a complete JDK was installed outside the checkout with Debian signature/checksum verification.
- `:neoforge:build` passed for 2.1.0-modded. ZIP packaging checks verified the final platform JAR, custom recipe, modifier implementation and absence of Chalk advancement resources.
- NeoForge datagen executed the crafting regression checks successfully: both Glow ingredients, stacked modifiers, recoloring, component-preserving repair, Redstone Chalk crafting/repair/color lock, invalid/duplicate ingredients, bottle returns and ItemStack serialization.
- The development datagen process left idle non-daemon executor threads alive after all providers finished and required termination. This is distinct from a clean runner exit. CI builds do not use this datagen runner as a passing test gate.
- Datagen also reported errors in optional development tooling (Embeddium fingerprint and missing client asset index); the crafting checks and providers completed. Graphical/gameplay validation, including visual underwater behavior and timed mining, remains unperformed. Dyenamics compatibility was checked against its public source API, not a running modpack.
- The initial Litterbox upload failed with HTTP 500. The NeoForge ZIP was subsequently uploaded to storage.to and its public file page verified. Temporary file-host links are not used as release infrastructure.

### GitHub Actions

- `.github/workflows/build-release.yml` uses a Fabric/NeoForge matrix, JDK 21 and the wrapper. Third-party actions are pinned to immutable commits. PR jobs receive read-only repository permissions; release write permission is confined to the publication job.
- Pushes and pull requests retain per-loader JAR and ZIP artifacts for 14 days. Tags matching `v<mod_version>` publish both loaders together after both builds succeed. `workflow_dispatch` builds by default; enabling `publish` creates/updates the matching GitHub Release.
- Release preparation verifies Minecraft version, JAR mod metadata, matching ZIP contents, the latest changelog version and existing tag provenance. The workflow refuses to reuse a version tag pointing at another commit.
- Release notes come from the latest section of `Changelog.md`. The existing uppercase changelog was renamed and the Gradle publishing helper updated to read it. `Tracelog.md` records implementation and validation limits.
- Publication uses the job's `GITHUB_TOKEN`; no new secrets are required. This workflow publishes to GitHub Releases. CurseForge/Modrinth publishing tasks are not invoked, since their upstream project IDs are not publication targets authorized for this fork.

### Validation for this change

- `:fabric:build :neoforge:build` passed: 23 tasks, 8 executed and 15 up-to-date. Neither loader has unit-test sources, so NO-SOURCE test tasks are not counted as tests.
- Both platform JARs passed the packaging helper's metadata/class checks; both ZIPs contain their exact matching JAR. Local release preparation passed for `v2.1.0-modded`.
- `actionlint` 1.7.12 validated the workflow without errors; `git diff --check` passed.
- The first [hosted Actions run](https://github.com/DeInfernal/ChalkAIModded/actions/runs/40323682951) completed successfully for both Fabric and NeoForge, including packaging and artifact upload. The publication job was correctly skipped for a branch push.
- GitHub Release publication itself has not been executed; it requires a version tag or a manual run with `publish` enabled.
