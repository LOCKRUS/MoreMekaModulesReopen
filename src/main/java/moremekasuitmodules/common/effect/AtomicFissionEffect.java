package moremekasuitmodules.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import mekanism.common.item.gear.ItemMekaSuitArmor;

/** Damage-over-time effect emitted by Stranglet and antimatter events. */
public final class AtomicFissionEffect extends MobEffect {
    private static final float BASE_DAMAGE = 1.0F;
    private static final float DAMAGE_PER_AMPLIFIER = 3.0F;
    private static final float BASE_EXHAUSTION = 0.5F;
    private static final float EXHAUSTION_PER_AMPLIFIER = 0.5F;

    public AtomicFissionEffect() {
        super(MobEffectCategory.HARMFUL, 0xD94DEB);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration > 0 && duration % 20 == 0;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (isProtectedPlayer(entity)) {
            return true;
        }
        float damage = BASE_DAMAGE + DAMAGE_PER_AMPLIFIER * amplifier;
        entity.hurt(entity.damageSources().fellOutOfWorld(), damage);
        if (entity instanceof Player player) {
            player.causeFoodExhaustion(BASE_EXHAUSTION + EXHAUSTION_PER_AMPLIFIER * amplifier);
        }
        return true;
    }

    private static boolean isProtectedPlayer(LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return false;
        }
        if (player.isSpectator() || player.getAbilities().instabuild) {
            return true;
        }
        return isMekaSuitPiece(player, EquipmentSlot.HEAD)
                && isMekaSuitPiece(player, EquipmentSlot.CHEST)
                && isMekaSuitPiece(player, EquipmentSlot.LEGS)
                && isMekaSuitPiece(player, EquipmentSlot.FEET);
    }

    private static boolean isMekaSuitPiece(LivingEntity entity, EquipmentSlot slot) {
        return entity.getItemBySlot(slot).getItem() instanceof ItemMekaSuitArmor;
    }
}
