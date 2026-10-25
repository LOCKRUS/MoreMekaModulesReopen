package moremekasuitmodules.common;

import moremekasuitmodules.common.registries.MoreMekaSuitModulesBlocks;
import moremekasuitmodules.common.registries.MoreMekaSuitModulesEffects;
import moremekasuitmodules.common.registries.MekaSuitMoreModulesItem;
import moremekasuitmodules.common.block.UnstableAirBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/** Applies Atomic Fission near Stranglet/Unstable Air and to living carriers. */
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
            if (!item.getItem().is(MekaSuitMoreModulesItem.STRANGLET.get()) && isNearStranglet(item)) {
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
        return containsBlock(entity, MoreMekaSuitModulesBlocks.STRANGLET.get(), entity.getBoundingBox().inflate(1.0D));
    }

    private static int getNearbyFissionAmplifier(LivingEntity entity) {
        AABB contactBox = entity.getBoundingBox().inflate(0.05D);
        if (containsBlock(entity, MoreMekaSuitModulesBlocks.STRANGLET.get(), contactBox)) {
            return STRANGLET_AMPLIFIER;
        }
        if (containsActiveUnstableAir(entity, contactBox)) {
            return UNSTABLE_AIR_AMPLIFIER;
        }
        return -1;
    }

    private static boolean containsActiveUnstableAir(net.minecraft.world.entity.Entity entity, AABB box) {
        return BlockPos.betweenClosedStream(box).anyMatch(pos -> {
            BlockState state = entity.level().getBlockState(pos);
            return state.is(MoreMekaSuitModulesBlocks.UNSTABLE_AIR.get()) && state.getValue(UnstableAirBlock.ACTIVE);
        });
    }

    private static boolean containsBlock(net.minecraft.world.entity.Entity entity, Block block, AABB box) {
        return BlockPos.betweenClosedStream(box)
                .anyMatch(pos -> entity.level().getBlockState(pos).is(block));
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
