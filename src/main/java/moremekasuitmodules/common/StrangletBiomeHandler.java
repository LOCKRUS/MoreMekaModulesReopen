package moremekasuitmodules.common;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.level.BlockGrowFeatureEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/** Gameplay rules for areas whose biome has been converted by Stranglet. */
public final class StrangletBiomeHandler {
    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            StrangletBiomeManager.onServerTick(level);
        }
    }

    @SubscribeEvent
    public void onCropGrow(CropGrowEvent.Pre event) {
        if (StrangletBiomeManager.isStrangeBiome(event.getLevel(), event.getPos())) {
            event.setResult(CropGrowEvent.Pre.Result.DO_NOT_GROW);
        }
    }

    @SubscribeEvent
    public void onPlantFeatureGrow(BlockGrowFeatureEvent event) {
        if (StrangletBiomeManager.isStrangeBiome(event.getLevel(), event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onMobDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Player)
                && StrangletBiomeManager.isStrangeBiome(event.getEntity().level(), event.getEntity().blockPosition())) {
            event.setCanceled(true);
            event.getDrops().clear();
        }
    }

    @SubscribeEvent
    public void onMobExperience(LivingExperienceDropEvent event) {
        if (!(event.getEntity() instanceof Player)
                && StrangletBiomeManager.isStrangeBiome(event.getEntity().level(), event.getEntity().blockPosition())) {
            event.setCanceled(true);
            event.setDroppedExperience(0);
        }
    }
}
