package moremekasuitmodules.common;

import moremekasuitmodules.common.registries.MoreMekaSuitModulesBlocks;
import moremekasuitmodules.common.registries.MoreMekaSuitModulesEffects;
import moremekasuitmodules.common.registries.MekaSuitMoreModulesItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/** Removes dropped items and applies Atomic Fission near Stranglet/Unstable Air and to living carriers. */
public final class StrangletExposureHandler {
    private static final int EFFECT_DURATION = 40;
    private static final int STRANGLET_AMPLIFIER = 1;
    private static final int UNSTABLE_AIR_AMPLIFIER = 0;

    @SubscribeEvent
    public void onEntityTick(EntityTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        if (event.getEntity() instanceof ItemEntity item) {
            if (isNearStranglet(item)) {
                item.discard();
            }
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity living)) {
            return;
        }

        int amplifier = getNearbyFissionAmplifier(living);
        if (carriesStranglet(living)) {
            amplifier = Math.max(amplifier, STRANGLET_AMPLIFIER);
        }
        if (amplifier < 0) {
            return;
        }

        living.addEffect(new MobEffectInstance(
                MoreMekaSuitModulesEffects.ATOMIC_FISSION,
                EFFECT_DURATION,
                amplifier,
                true,
                true,
                true));
    }

    private static boolean isNearStranglet(net.minecraft.world.entity.Entity entity) {
        AABB searchBox = entity.getBoundingBox().inflate(1.0D);
        return BlockPos.betweenClosedStream(searchBox)
                .anyMatch(pos -> entity.level().getBlockState(pos).is(MoreMekaSuitModulesBlocks.STRANGLET.get()));
    }

    private static int getNearbyFissionAmplifier(net.minecraft.world.entity.Entity entity) {
        AABB searchBox = entity.getBoundingBox().inflate(1.0D);
        return BlockPos.betweenClosedStream(searchBox)
                .mapToInt(pos -> {
                    var state = entity.level().getBlockState(pos);
                    if (state.is(MoreMekaSuitModulesBlocks.STRANGLET.get())) {
                        return STRANGLET_AMPLIFIER;
                    }
                    if (state.is(MoreMekaSuitModulesBlocks.UNSTABLE_AIR.get())) {
                        return UNSTABLE_AIR_AMPLIFIER;
                    }
                    return -1;
                })
                .max()
                .orElse(-1);
    }

    private static boolean carriesStranglet(LivingEntity entity) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (entity.getItemBySlot(slot).is(MekaSuitMoreModulesItem.STRANGLET.get())) {
                return true;
            }
        }
        if (entity instanceof Player player) {
            return player.getInventory().items.stream().anyMatch(stack -> stack.is(MekaSuitMoreModulesItem.STRANGLET.get()))
                    || player.getInventory().armor.stream().anyMatch(stack -> stack.is(MekaSuitMoreModulesItem.STRANGLET.get()))
                    || player.getInventory().offhand.stream().anyMatch(stack -> stack.is(MekaSuitMoreModulesItem.STRANGLET.get()));
        }
        return false;
    }

}
