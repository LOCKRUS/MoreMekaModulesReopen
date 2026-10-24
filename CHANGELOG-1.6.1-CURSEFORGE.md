# MoreMekaModulesReopen 1.6.1

## Added

- Added the **Kinetic Stabilization Unit** module.
  - Installs into MekaSuit chest armor.
  - Supports up to four installed modules.
  - Each active module reduces incoming knockback by 25%.
- Added crafting recipes and runtime support for the Capacity Amplification Unit.
- Added the **Mendeleev Is Shocked** advancement for crafting Solipsismium.
- Added the **The End Has Come** goal advancement for crafting the Anti-Matter Strike module.
- Added the **Divine Collector** goal advancement for crafting all available Mekanism, MekaTool and MekaSuit modules.
- Added Chinese, Japanese and Ukrainian localization coverage alongside English and Russian.

## Changed

- Updated the Capacity Amplification Unit texture using the original module artwork.
- Updated the Otherworldly Alloy texture.
- Changed the burnt grass map color to dark gray.
- Updated Russian module names and capitalization:
  - `Модуль: Модуль кинетической стабилизации`
  - `Модуль: Рамки существ`
- Ultra Orb scorch behavior has been refined:
  - Logs and wood stems are converted into Charred Wood.
  - Wooden planks are destroyed without drops instead of being converted into Charred Wood.
  - Wooden slabs, stairs, fences, doors, trapdoors, buttons and pressure plates are destroyed without drops.
  - Leaves, flowers, crops, saplings and other plants are destroyed without drops.
  - Glass, glass panes and tinted glass are destroyed without drops.
  - Chests, crafting tables and other container blocks are destroyed without drops.
- Replaced the shield's dependence on vanilla `invulnerableTime` with a separate 20-tick persistent player cooldown.
- Ultra Orb direct entity collisions now deal exactly 1000 HP of armor-ignoring damage in Ultra mode.
- The Ultra Orb's pressure-wave damage and crowd-control effects remain separate from the direct collision damage.

## Fixed

- Fixed the Capacity Amplification Unit not being installable into MekaSuit and MekaTool equipment.
- Fixed the Capacity Amplification Unit not increasing the energy capacity of supported equipment.
- Fixed the Capacity Amplification Unit crafting recipe not loading due to an invalid Mekanism item ID.
- Fixed missing recipe loading caused by the incorrect `mekanism:module_energy` identifier. The recipe now uses `mekanism:module_energy_unit`.
- Fixed the Anti-Matter Strike module not being enabled in preconfigured MekaTool items from the mod creative tab.
- Fixed plants and other fragile blocks dropping items during Ultra Orb scorch conversion.
- Fixed missing translations for release modules, items, HUD values and advancements in all supported languages.

## Verification

- All available Gradle checks and build tasks completed successfully.
- All five locale files contain the complete 135-key localization set.
- Advancement and recipe JSON files passed parsing validation.
- NeoForge runtime server verification completed successfully.
- Minecraft version: **1.21.1**
- Mod version: **1.6.1**
