package moremekasuitmodules.common.recipe;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import moremekasuitmodules.common.MoreMekaModulesReopen;
import moremekasuitmodules.common.registries.MekaSuitMoreModulesItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Central source of truth for the radiation chamber's supported recipes. */
public final class RadiationChamberRecipes {
    public static final int STRANGLET_REACTION_DURATION_TICKS = 20 * 60;
    public static final int DEEPSLATE_REACTION_DURATION_TICKS = 20 * 30;
    public static final int GREEN_DYE_REACTION_DURATION_TICKS = 20 * 15;
    public static final int BLACK_DYE_REACTION_DURATION_TICKS = GREEN_DYE_REACTION_DURATION_TICKS;
    public static final int STRAPPEL_INGOT_REACTION_DURATION_TICKS = 20 * 90;
    public static final long FUEL_PER_TICK = 10;
    public static final long STRAPPEL_INGOT_FUEL_PER_TICK = 100;

    public static final List<RadiationChamberRecipe> ALL = List.of(
            new RadiationChamberRecipe(id("stranglet_to_solipsismium_296"),
                    MekaSuitMoreModulesItem.STRANGLET.get(), MekaSuitMoreModulesItem.SOLIPSISMIUM_296.get(),
                    MekaSuitMoreModulesItem.SOLIPSISMIUM_296.get(), 100,
                    STRANGLET_REACTION_DURATION_TICKS, FUEL_PER_TICK, false),
            new RadiationChamberRecipe(id("deepslate_to_reinforced_deepslate"),
                    Items.DEEPSLATE, Items.REINFORCED_DEEPSLATE, Items.REINFORCED_DEEPSLATE, 100,
                    DEEPSLATE_REACTION_DURATION_TICKS, FUEL_PER_TICK, false),
            new RadiationChamberRecipe(id("green_dye_to_slime_ball"),
                    Items.GREEN_DYE, Items.SLIME_BALL, Items.SLIME_BALL, 100,
                    GREEN_DYE_REACTION_DURATION_TICKS, FUEL_PER_TICK, false),
            new RadiationChamberRecipe(id("black_dye_to_coal"),
                    Items.BLACK_DYE, Items.COAL, Items.COAL, 100,
                    BLACK_DYE_REACTION_DURATION_TICKS, FUEL_PER_TICK, false),
            new RadiationChamberRecipe(id("encased_strappel_ingot_to_stranglet"),
                    MekaSuitMoreModulesItem.ENCASED_STRAPPEL_INGOT.get(),
                    MekaSuitMoreModulesItem.STRANGLET.get(),
                    MekaSuitMoreModulesItem.ENCASED_PURIFIED_OBSIDIAN_INGOT.get(),
                    50, STRAPPEL_INGOT_REACTION_DURATION_TICKS, STRAPPEL_INGOT_FUEL_PER_TICK, true)
    );

    private static final Map<ResourceLocation, RadiationChamberRecipe> BY_ID = ALL.stream()
            .collect(Collectors.toUnmodifiableMap(RadiationChamberRecipe::id, Function.identity()));

    private RadiationChamberRecipes() {
    }

    public static Optional<RadiationChamberRecipe> find(ItemStack input) {
        if (input.isEmpty()) {
            return Optional.empty();
        }
        return ALL.stream().filter(recipe -> recipe.matches(input)).findFirst();
    }

    public static Optional<RadiationChamberRecipe> byId(ResourceLocation id) {
        return Optional.ofNullable(BY_ID.get(id));
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MoreMekaModulesReopen.MODID, path);
    }
}
