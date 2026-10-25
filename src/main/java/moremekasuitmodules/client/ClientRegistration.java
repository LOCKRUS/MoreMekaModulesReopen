package moremekasuitmodules.client;

import moremekasuitmodules.client.render.hud.MoreMekaSuitModulesHUD;
import moremekasuitmodules.client.render.entity.AntimatterExplosiveOrbModel;
import moremekasuitmodules.client.render.entity.AntimatterExplosiveOrbRenderer;
import moremekasuitmodules.client.render.entity.AntimatterChargeRenderer;
import moremekasuitmodules.client.render.block.RadiationChamberRenderer;
import moremekasuitmodules.client.sound.RadiationChamberSoundController;
import moremekasuitmodules.common.MoreMekaModulesReopen;
import moremekasuitmodules.common.block.entity.RadiationChamberBlockEntity;
import moremekasuitmodules.common.content.gear.mekanism.mekatool.MekaToolLavaHandler;
import moremekasuitmodules.common.registries.MoreMekaSuitModulesEntities;
import moremekasuitmodules.common.registries.MoreMekaSuitModulesBlockEntities;
import moremekasuitmodules.common.registries.MekaSuitMoreModulesItem;
import mekanism.common.item.gear.ItemMekaTool;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;


@EventBusSubscriber(modid = MoreMekaModulesReopen.MODID, value = Dist.CLIENT)
public class ClientRegistration {

    private ClientRegistration() {
    }

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        RadiationChamberBlockEntity.installClientTickHandler(RadiationChamberSoundController::tick);
        NeoForge.EVENT_BUS.register(new ClientTickHandler());
    }


    @SubscribeEvent
    public static void registerOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, MoreMekaModulesReopen.rl("shield_hud"), MoreMekaSuitModulesHUD.INSTANCE);
    }

    @SubscribeEvent
    public static void addMekaToolLavaTooltip(ItemTooltipEvent event) {
        if (!(event.getItemStack().getItem() instanceof ItemMekaTool)) {
            return;
        }
        IFluidHandlerItem tank = MekaToolLavaHandler.create(event.getItemStack());
        if (tank != null && tank.getTanks() > 0) {
            event.getToolTip().add(Component.translatable("tooltip.moremekasuitmodules.lava_storage",
                    tank.getFluidInTank(0).getAmount(), tank.getTankCapacity(0))
                    .withStyle(style -> style.withColor(TextColor.fromRgb(0xE67E22))));
        }
    }

    @SubscribeEvent
    public static void addSpecialItemTooltips(ItemTooltipEvent event) {
        var stack = event.getItemStack();
        if (stack.is(MekaSuitMoreModulesItem.RADIATION_CHAMBER.get())) {
            if (Screen.hasShiftDown()) {
                event.getToolTip().add(Component.translatable("tooltip.moremekasuitmodules.radiation_chamber.structure")
                        .withStyle(ChatFormatting.GRAY));
                event.getToolTip().add(Component.translatable("tooltip.moremekasuitmodules.radiation_chamber.ring")
                        .withStyle(ChatFormatting.GRAY));
                event.getToolTip().add(Component.translatable("tooltip.moremekasuitmodules.radiation_chamber.rear")
                        .withStyle(ChatFormatting.GRAY));
                event.getToolTip().add(Component.translatable("tooltip.moremekasuitmodules.radiation_chamber.front")
                        .withStyle(ChatFormatting.GRAY));
            } else {
                event.getToolTip().add(Component.translatable("tooltip.moremekasuitmodules.hold_shift")
                        .withStyle(ChatFormatting.GRAY));
            }
            return;
        }

        String descriptionKey;
        boolean danger;
        if (stack.is(MekaSuitMoreModulesItem.ENCASED_STRAPPEL_INGOT.get())) {
            descriptionKey = "tooltip.moremekasuitmodules.encased_strappel_ingot";
            danger = true;
        } else if (stack.is(MekaSuitMoreModulesItem.STRANGLET.get())) {
            descriptionKey = "tooltip.moremekasuitmodules.stranglet";
            danger = false;
        } else {
            return;
        }

        if (Screen.hasShiftDown()) {
            var description = Component.translatable(descriptionKey);
            event.getToolTip().add(danger ? description.withStyle(style -> style.withColor(TextColor.fromRgb(0xFF0000))) : description);
        } else {
            event.getToolTip().add(Component.translatable("tooltip.moremekasuitmodules.hold_shift")
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    @SubscribeEvent
    public static void registerOrbLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(AntimatterExplosiveOrbModel.LAYER_LOCATION,
                AntimatterExplosiveOrbModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerOrbRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(MoreMekaSuitModulesEntities.ANTIMATTER_EXPLOSIVE_ORB.get(),
                AntimatterExplosiveOrbRenderer::new);
        event.registerBlockEntityRenderer(MoreMekaSuitModulesBlockEntities.RADIATION_CHAMBER.get(),
                RadiationChamberRenderer::new);
    }

    @SubscribeEvent
    public static void renderAntimatterCharge(RenderLevelStageEvent event) {
        AntimatterChargeRenderer.render(event);
    }
}
