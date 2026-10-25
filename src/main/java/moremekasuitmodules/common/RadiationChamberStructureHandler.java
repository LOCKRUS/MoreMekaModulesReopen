package moremekasuitmodules.common;

import moremekasuitmodules.common.block.entity.RadiationChamberBlockEntity;
import moremekasuitmodules.common.registries.MoreMekaSuitModulesBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public final class RadiationChamberStructureHandler {
    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!event.getPlacedBlock().is(MoreMekaSuitModulesBlocks.REACTION_BLOCK.get())
                || !(event.getLevel() instanceof ServerLevel level)
                || !(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        for (BlockPos candidate : BlockPos.withinManhattan(event.getPos(), 2, 2, 2)) {
            if (level.getBlockEntity(candidate) instanceof RadiationChamberBlockEntity chamber) {
                chamber.updateStructureState(player);
            }
        }
    }
}
