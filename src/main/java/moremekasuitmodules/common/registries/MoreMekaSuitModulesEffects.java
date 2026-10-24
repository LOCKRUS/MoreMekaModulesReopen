package moremekasuitmodules.common.registries;

import moremekasuitmodules.common.MoreMekaModulesReopen;
import moremekasuitmodules.common.effect.AtomicFissionEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MoreMekaSuitModulesEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, MoreMekaModulesReopen.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> ATOMIC_FISSION = MOB_EFFECTS.register(
            "atomic_fission", AtomicFissionEffect::new);

    private MoreMekaSuitModulesEffects() {
    }
}
