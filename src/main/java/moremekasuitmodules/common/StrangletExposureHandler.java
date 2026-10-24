package moremekasuitmodules.common;

import mekanism.common.item.gear.ItemMekaSuitArmor;
import moremekasuitmodules.common.registries.MoreMekaSuitModulesBlocks;
import moremekasuitmodules.common.registries.MoreMekaSuitModulesEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/** Removes dropped items and applies Atomic Fission to living entities exposed to Stranglet. */
public final class StrangletExposureHandler {
    private static final int EFFECT_DURATION = 40;
    private static final int EFFECT_AMPLIFIER = 1;

    @SubscribeEvent
    public void onEntityTick(EntityTickEvent.Post event) {
        if (event.getEntity().level().isClientSide() || !isNearStranglet(event.getEntity())) {
            return;
        }

        if (event.getEntity() instanceof ItemEntity item) {
            item.discard();
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity living) || isProtectedPlayer(living)) {
            return;
        }

        living.addEffect(new MobEffectInstance(
                MoreMekaSuitModulesEffects.ATOMIC_FISSION,
                EFFECT_DURATION,
                EFFECT_AMPLIFIER,
                true,
                true,
                true));
    }

    private static boolean isNearStranglet(net.minecraft.world.entity.Entity entity) {
        AABB searchBox = entity.getBoundingBox().inflate(1.0D);
        return BlockPos.betweenClosedStream(searchBox)
                .anyMatch(pos -> entity.level().getBlockState(pos).is(MoreMekaSuitModulesBlocks.STRANGLET.get()));
    }

    private static boolean isProtectedPlayer(LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return false;
        }
        return player.isSpectator()
                || player.getAbilities().instabuild
                || wearsFullMekaSuit(player);
    }

    private static boolean wearsFullMekaSuit(LivingEntity entity) {
        return isMekaSuitPiece(entity, EquipmentSlot.HEAD)
                && isMekaSuitPiece(entity, EquipmentSlot.CHEST)
                && isMekaSuitPiece(entity, EquipmentSlot.LEGS)
                && isMekaSuitPiece(entity, EquipmentSlot.FEET);
    }

    private static boolean isMekaSuitPiece(LivingEntity entity, EquipmentSlot slot) {
        return entity.getItemBySlot(slot).getItem() instanceof ItemMekaSuitArmor;
    }
}
