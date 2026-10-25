package moremekasuitmodules.common;

import moremekasuitmodules.common.block.entity.RadiationChamberBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class RadiationChamberInteractionHandler {
    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND
                || !(event.getLevel().getBlockEntity(event.getPos()) instanceof RadiationChamberBlockEntity chamber)) {
            return;
        }

        if (!chamber.isStructureFormed()) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            if (!event.getLevel().isClientSide()) {
                event.getEntity().displayClientMessage(
                        Component.translatable("message.moremekasuitmodules.radiation_chamber_unformed"), true);
            }
            return;
        }

        ItemStack held = event.getItemStack();
        if (RadiationChamberBlockEntity.isSupportedInput(held)) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            if (!event.getLevel().isClientSide()) {
                chamber.tryInsertInput(event.getEntity(), held);
            }
            return;
        }

        if (!held.isEmpty() || chamber.getContentsForRender().isEmpty()) {
            return;
        }
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
        if (!event.getLevel().isClientSide()) {
            ItemStack extracted = chamber.takeContents();
            if (!extracted.isEmpty() && !event.getEntity().getInventory().add(extracted)) {
                event.getEntity().drop(extracted, false);
            }
        }
    }
}
