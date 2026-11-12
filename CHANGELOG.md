# Changelog

## 1.6.5

### Added and changed

- Increased D-T fuel consumption tenfold for every radiation-chamber recipe: 10 mB per active tick for the standard recipes and 100 mB per active tick for the Strappel-ingot recipe.
- Added a radiation-chamber recipe for the encased Strappel ingot: 90 seconds (180,000 mB total), with a 50% chance to produce a Stranglet and a 50% chance to produce an encased purified obsidian ingot.
- Added a black dye-to-coal reaction, matching green dye-to-slime ball at 15 seconds and 10 mB of D-T fuel per active tick (3,000 mB total).
- The Strappel-ingot reaction requires clear daylight for the full duration. Night or rain resets its progress; the input remains in the chamber, while fuel already consumed is not refunded.
- Removed the legacy right-click block activation for the encased Strappel ingot. Removed its now-unnecessary tooltip; the hazard warning belongs to the Stranglet block tooltip.
- JEI now cycles through both possible outcomes and describes their chances and the clear-daylight requirement. Added translations for the new hints in all five supported languages.
- Increased the D-T visual in JEI to a full 1,000 mB indicator so the fuel is easier to distinguish; the tooltip continues to show the exact per-tick and total recipe consumption.

### Validation

- Final `./gradlew clean test build` completed successfully with JDK 21; all 7 unit tests passed.
- Compilation reports two existing deprecation warnings for Mekanism's `ChemicalBuilder.gaseous()` calls in `MoreMekaSuitModulesChemicals.java`.

## 1.6.5-beta1 (Pre-release)

### Added

- Added an optional JEI recipe category for the radiation chamber, using the supplied 256×256 texture and its 176×90 visible interface region.
- Hovering the D-T fuel input shows use per active tick, total recipe fuel (1,200 mB for Stranglet, 600 mB for deepslate, 300 mB for green dye), and active reaction time. The Stranglet recipe needs refueling because its total exceeds the 1,000 mB tank capacity.

### Fixed

- Corrected shield-recharge energy debits so small provider shares are not lost to integer division.
- Fixed the MekaTool flamethrower handler so a missing or disabled module does not intercept ordinary right-click behavior.
- Re-encoded the radiation-chamber JEI background as a real PNG and made only the edge-connected pure-white background transparent, preserving non-white colors and isolated white details.

### Updated

- Revised 14 module descriptions in all five included languages to better match implemented behavior, settings, costs and timings.

### Validation and compatibility

- Clean JDK 21 Gradle test/build completed; all 7 unit tests passed.
- JEI is an optional client integration (minimum API version 19.8.5); Mekanism and Mekanism Generators remain required.

## 1.6.4

### Fixed

- Corrected Last Stand shield energy allocation. Energy is now distributed proportionally across charged shield providers, each provider is charged no more than it holds, and the total debit is capped at the available energy.
- Made Last Stand's total-charge threshold check saturate instead of overflowing when multiple providers collectively exceed `Long.MAX_VALUE`.
- Registered the existing energized-item Mixin so the MekaTool lava-storage tooltip is applied.
- Replaced the radiation chamber's text-only Jade D-T fuel line with a progress gauge showing its fill level and amount out of 1,000 mB when the structure is formed.
- Added a radiation chamber reaction that converts deepslate into reinforced deepslate in 600 active ticks (30 seconds), consuming 1 mB of D-T fuel per tick; the existing Stranglet reaction remains unchanged.
- Added a radiation chamber reaction that converts green dye into a slime ball in 300 active ticks (15 seconds), consuming 1 mB of D-T fuel per tick.
- Preserved radiation-reaction progress while the laser, fuel, or formed structure is temporarily unavailable; progress resumes when operation can continue.
- Unified the radiation chamber's contained-content rendering so Stranglet, deepslate and item inputs/outputs use the same centered fixed-display scale.
- Added a second Jade gauge beneath the D-T fuel bar to show the active radiation recipe's completion percentage; it resets when the input becomes its output.
- Removed colons from the Jade fuel and reaction-progress gauge labels.
- Corrected two English localization typos and completed the missing Japanese and Simplified Chinese biome name.
- Added the missing Ukrainian Strange Biome translation.

### Build and validation

- Added focused regression tests for proportional shield energy allocation, including single-provider, multi-provider, low-energy, large-value, and overflow-safe threshold totals.
- Updated GitHub Actions checkout and Java setup actions.

### Compatibility

- Minecraft and NeoForge target versions are unchanged from 1.6.3.
- Mekanism and Mekanism Generators remain required runtime dependencies.
