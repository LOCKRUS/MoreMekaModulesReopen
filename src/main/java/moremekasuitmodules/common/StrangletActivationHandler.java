package moremekasuitmodules.common;

import moremekasuitmodules.common.registries.MekaSuitMoreModulesItem;
import moremekasuitmodules.common.registries.MoreMekaSuitModulesBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/** Turns a clicked finite-hardness block into the first Stranglet colony. */
public final class StrangletActivationHandler {
    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (level.isClientSide()) {
            return;
        }

        ItemStack stack = event.getItemStack();
        if (!stack.is(MekaSuitMoreModulesItem.ENCASED_STRAPPEL_INGOT.get())) {
            return;
        }

        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!isValidTarget(level, pos, state)) {
            return;
        }

        level.setBlock(pos, MoreMekaSuitModulesBlocks.STRANGLET.get().defaultBlockState(), 3);
        level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (!event.getEntity().getAbilities().instabuild) {
            stack.shrink(1);
        }
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    private static boolean isValidTarget(Level level, BlockPos pos, BlockState state) {
        return state.getDestroySpeed(level, pos) >= 0.0F
                && !state.is(MoreMekaSuitModulesBlocks.STRANGLET.get());
    }
}
