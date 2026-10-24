package moremekasuitmodules.mixin.minecraft;

import moremekasuitmodules.common.registries.MoreMekaSuitModulesEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.CombatTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer {
    @Redirect(
            method = "die",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/CombatTracker;getDeathMessage()Lnet/minecraft/network/chat/Component;"))
    private Component moreMekaModules$atomicFissionDeathMessage(CombatTracker tracker) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if (player.hasEffect(MoreMekaSuitModulesEffects.ATOMIC_FISSION)) {
            String key = player.getRandom().nextBoolean()
                    ? "death.moremekasuitmodules.atomic_fission.split"
                    : "death.moremekasuitmodules.atomic_fission.disintegrated";
            return Component.translatable(key, player.getDisplayName());
        }
        return tracker.getDeathMessage();
    }
}
