package moremekasuitmodules.client.integration.jei;

import mekanism.client.recipe_viewer.jei.MekanismJEI;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import moremekasuitmodules.common.MoreMekaModulesReopen;
import moremekasuitmodules.common.recipe.RadiationChamberRecipes;
import moremekasuitmodules.common.registries.MoreMekaSuitModulesBlocks;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public final class RadiationChamberJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(
            MoreMekaModulesReopen.MODID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        if (MekanismJEI.shouldLoad()) {
            registration.addRecipeCategories(new RadiationChamberJeiCategory(
                    registration.getJeiHelpers().getGuiHelper()));
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (MekanismJEI.shouldLoad()) {
            registration.addRecipes(RadiationChamberJeiCategory.RECIPE_TYPE, RadiationChamberRecipes.ALL);
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        if (MekanismJEI.shouldLoad()) {
            registration.addRecipeCatalyst(MoreMekaSuitModulesBlocks.RADIATION_CHAMBER.get(),
                    RadiationChamberJeiCategory.RECIPE_TYPE);
        }
    }
}
