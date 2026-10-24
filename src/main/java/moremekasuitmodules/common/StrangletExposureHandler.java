package moremekasuitmodules.common;

import mekanism.common.item.gear.ItemMekaSuitArmor;
import moremekasuitmodules.common.registries.MoreMekaSuitModulesBlocks;
import moremekasuitmodules.common.registries.MoreMekaSuitModulesEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/** Applies Atomic Fission II to living entities exposed to a Stranglet block. */
public final class StrangletExposureHandler {
    private static final int EFFECT_DURATION = 40;
    private static final int EFFECT_AMPLIFIER = 1;

    @SubscribeEvent
    public void onLivingTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }
        if (entity.level().isClientSide() || entity.isDeadOrDying()
                || wearsFullMekaSuit(entity) || !isNearStranglet(entity)) {
            return;
        }

        entity.addEffect(new MobEffectInstance(
                MoreMekaSuitModulesEffects.ATOMIC_FISSION,
                EFFECT_DURATION,
                EFFECT_AMPLIFIER,
                true,
                true,
                true));
    }

    private static boolean isNearStranglet(LivingEntity entity) {
        AABB searchBox = entity.getBoundingBox().inflate(1.0D);
        return BlockPos.betweenClosedStream(searchBox)
                .anyMatch(pos -> entity.level().getBlockState(pos).is(MoreMekaSuitModulesBlocks.STRANGLET.get()));
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
