# Changelog

## 1.6.6

### Updated

- Aligned Radiation Chamber JEI ingredients to the visible 16×16 cell areas; the larger item-input frame is centered independently.
- Replaced the opaque Dragon Breath charge plume with a translucent purple spiral that remains small, appears less often, and now emits just above the MekaTool muzzle.
- Updated the JEI category to the user's final 256×256 PNG; ingredient slot coordinates remain unchanged.
- Smoothed the charging orb's render-time position while the player moves.

### Optimized

- Reduced Radiation Chamber structure scans to every other server tick, with immediate validation before manual input or extraction.
- Reduced nearby Stranglet exposure checks for living entities to every five ticks and removed redundant player-inventory stream scans; Stranglet item cleanup remains immediate.
- Capped Radiation Chamber bolt-effect updates at 40 Hz per chamber instead of rebuilding effects on every render frame.

### Fixed

- Fully reworked the Radiation Chamber's JEI recipe previews to match the final supplied texture, with 16×16 ingredients aligned to the visible cell boundaries.
- Added self-drop loot tables for the Reaction Block, Radiation Chamber, Burnt Grass, and Charred Wood.

## 1.6.5

### Added

- Added an optional JEI recipe category for the Radiation Chamber. It uses the supplied 256×256 texture with a 176×90 visible area. The D-T fuel input shows a full 1,000 mB visual indicator; hovering it gives the exact per-tick use, total fuel and reaction time.
- Added these Radiation Chamber recipes (all values below are final):
  - Stranglet → Solipsismium-296: 1,200 ticks (60 seconds), 10 mB/t, 12,000 mB total; replenish fuel during the reaction because this exceeds the chamber's 1,000 mB tank capacity.
  - Deepslate → Reinforced Deepslate: 600 ticks (30 seconds), 10 mB/t, 6,000 mB total.
  - Green Dye → Slime Ball: 300 ticks (15 seconds), 10 mB/t, 3,000 mB total.
  - Black Dye → Coal: 300 ticks (15 seconds), 10 mB/t, 3,000 mB total.
  - Encased Strappel Ingot → Stranglet or Encased Purified Obsidian Ingot: 1,800 ticks (90 seconds), 100 mB/t, 180,000 mB total, with a 50% chance for each outcome.
- The Strappel-ingot reaction requires clear daylight throughout. Night or rain resets its progress; the input remains in the chamber, but fuel already consumed is not refunded. JEI shows both outcomes, their chances and the environmental requirements.

### Fixed and updated

- Corrected shield-recharge energy debits so small provider shares are not lost to integer division.
- Fixed the MekaTool flamethrower handler so a missing or disabled module does not intercept ordinary right-click behavior.
- Removed the old right-click block activation for the Encased Strappel Ingot and its now-unneeded item tooltip. The danger warning is shown on the Stranglet block instead.
- Corrected the Radiation Chamber JEI background to a real PNG and made only the edge-connected white background transparent, preserving isolated white details.
- Adjusted the D-T fuel and item-input positions in the JEI category by one pixel for alignment.
- Revised 14 module descriptions and added the new Radiation Chamber hints in all five included languages (English, Russian, Japanese, Ukrainian and Chinese).

### Compatibility and validation

- Mekanism and Mekanism Generators are required. JEI is optional (minimum API version 19.8.5).
- Clean JDK 21 `./gradlew clean test build` completed successfully; all 7 unit tests passed. The build reports two existing deprecation warnings for Mekanism's `ChemicalBuilder.gaseous()` calls in `MoreMekaSuitModulesChemicals.java`.

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
