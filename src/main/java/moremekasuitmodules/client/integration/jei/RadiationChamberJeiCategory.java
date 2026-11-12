package moremekasuitmodules.client.integration.jei;

import com.mojang.serialization.Codec;
import java.util.List;
import mekanism.client.recipe_viewer.jei.MekanismJEI;
import mekanism.generators.common.registries.GeneratorsChemicals;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeManager;
import moremekasuitmodules.common.MoreMekaModulesReopen;
import moremekasuitmodules.common.recipe.RadiationChamberRecipe;
import moremekasuitmodules.common.recipe.RadiationChamberRecipes;
import moremekasuitmodules.common.registries.MoreMekaSuitModulesBlocks;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class RadiationChamberJeiCategory implements IRecipeCategory<RadiationChamberRecipe> {
    private static final int WIDTH = 176;
    private static final int HEIGHT = 90;
    private static final int TEXTURE_SIZE = 256;
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            MoreMekaModulesReopen.MODID, "textures/gui/jei/radiation_chamber.png");
    private static final ResourceLocation TYPE_ID = ResourceLocation.fromNamespaceAndPath(
            MoreMekaModulesReopen.MODID, "radiation_chamber");

    public static final RecipeType<RadiationChamberRecipe> RECIPE_TYPE =
            new RecipeType<>(TYPE_ID, RadiationChamberRecipe.class);

    private static final Codec<RadiationChamberRecipe> RECIPE_CODEC = Codec.STRING.xmap(
            id -> RadiationChamberRecipes.byId(ResourceLocation.parse(id))
                    .orElseThrow(() -> new IllegalArgumentException("Unknown radiation chamber recipe " + id)),
            recipe -> recipe.id().toString()
    );

    private static final int FUEL_X = 28;
    private static final int FUEL_Y = 34;
    private static final long FUEL_ICON_AMOUNT = 1_000;
    private static final int INPUT_X = 54;
    private static final int INPUT_Y = 35;
    private static final int OUTPUT_X = 126;
    private static final int OUTPUT_Y = 35;

    private final IDrawable background;
    private final IDrawable icon;

    public RadiationChamberJeiCategory(IGuiHelper guiHelper) {
        background = guiHelper.drawableBuilder(TEXTURE, 0, 0, WIDTH, HEIGHT)
                .setTextureSize(TEXTURE_SIZE, TEXTURE_SIZE)
                .build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                MoreMekaSuitModulesBlocks.RADIATION_CHAMBER.get().asItem().getDefaultInstance());
    }

    @Override
    public RecipeType<RadiationChamberRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(MoreMekaSuitModulesBlocks.RADIATION_CHAMBER.get().getDescriptionId());
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RadiationChamberRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, FUEL_X, FUEL_Y)
                .addIngredients(MekanismJEI.TYPE_CHEMICAL,
                        List.of(GeneratorsChemicals.FUSION_FUEL.asStack(FUEL_ICON_AMOUNT)))
                .addRichTooltipCallback((slot, tooltip) -> {
                    tooltip.add(Component.translatable(
                            "jei.moremekasuitmodules.radiation_chamber.fuel_per_tick", recipe.fuelPerTick()));
                    tooltip.add(Component.translatable(
                            "jei.moremekasuitmodules.radiation_chamber.total_fuel", recipe.totalFuel()));
                    tooltip.add(Component.translatable(
                            "jei.moremekasuitmodules.radiation_chamber.reaction_time", recipe.durationSeconds()));
                });

        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, INPUT_Y)
                .addItemStack(recipe.createInputStack())
                .addRichTooltipCallback((slot, tooltip) -> {
                    if (recipe.requiresClearDaylight()) {
                        tooltip.add(Component.translatable(
                                "jei.moremekasuitmodules.radiation_chamber.clear_daylight_title")
                                .withStyle(ChatFormatting.GOLD));
                        tooltip.add(Component.translatable(
                                "jei.moremekasuitmodules.radiation_chamber.clear_daylight_requirement")
                                .withStyle(ChatFormatting.AQUA));
                        tooltip.add(Component.translatable(
                                "jei.moremekasuitmodules.radiation_chamber.interruption_resets")
                                .withStyle(ChatFormatting.RED));
                    }
                });
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
                .addItemStacks(recipe.createPossibleOutputStacks())
                .addRichTooltipCallback((slot, tooltip) -> {
                    if (recipe.hasMultipleOutcomes()) {
                        tooltip.add(Component.translatable(
                                "jei.moremekasuitmodules.radiation_chamber.outcome_title")
                                .withStyle(ChatFormatting.GOLD));
                        tooltip.add(Component.translatable(
                                "jei.moremekasuitmodules.radiation_chamber.success_chance",
                                recipe.successChancePercent()).withStyle(ChatFormatting.GREEN));
                        tooltip.add(Component.translatable(
                                "jei.moremekasuitmodules.radiation_chamber.failure_chance",
                                100 - recipe.successChancePercent()).withStyle(ChatFormatting.RED));
                        if (recipe.requiresClearDaylight()) {
                            tooltip.add(Component.translatable(
                                    "jei.moremekasuitmodules.radiation_chamber.clear_daylight_requirement")
                                    .withStyle(ChatFormatting.AQUA));
                            tooltip.add(Component.translatable(
                                    "jei.moremekasuitmodules.radiation_chamber.interruption_resets")
                                    .withStyle(ChatFormatting.RED));
                        }
                    }
                });
    }

    @Override
    public void draw(RadiationChamberRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
                     double mouseX, double mouseY) {
        background.draw(guiGraphics, 0, 0);
    }

    @Override
    public boolean needsRecipeBorder() {
        return false;
    }

    @Override
    public ResourceLocation getRegistryName(RadiationChamberRecipe recipe) {
        return recipe.id();
    }

    @Override
    public Codec<RadiationChamberRecipe> getCodec(ICodecHelper codecHelper, IRecipeManager recipeManager) {
        return RECIPE_CODEC;
    }
}
