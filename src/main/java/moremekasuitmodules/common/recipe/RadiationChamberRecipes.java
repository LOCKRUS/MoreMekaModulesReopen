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
    public static final long FUEL_PER_TICK = 1;

    public static final List<RadiationChamberRecipe> ALL = List.of(
            new RadiationChamberRecipe(id("stranglet_to_solipsismium_296"),
                    MekaSuitMoreModulesItem.STRANGLET.get(), MekaSuitMoreModulesItem.SOLIPSISMIUM_296.get(),
                    STRANGLET_REACTION_DURATION_TICKS, FUEL_PER_TICK),
            new RadiationChamberRecipe(id("deepslate_to_reinforced_deepslate"),
                    Items.DEEPSLATE, Items.REINFORCED_DEEPSLATE,
                    DEEPSLATE_REACTION_DURATION_TICKS, FUEL_PER_TICK),
            new RadiationChamberRecipe(id("green_dye_to_slime_ball"),
                    Items.GREEN_DYE, Items.SLIME_BALL,
                    GREEN_DYE_REACTION_DURATION_TICKS, FUEL_PER_TICK)
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
