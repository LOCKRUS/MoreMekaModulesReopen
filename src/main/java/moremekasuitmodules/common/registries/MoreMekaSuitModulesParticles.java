package moremekasuitmodules.common.registries;

import moremekasuitmodules.common.MoreMekaModulesReopen;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MoreMekaSuitModulesParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, MoreMekaModulesReopen.MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ANTIMATTER_CHARGE =
            PARTICLE_TYPES.register("antimatter_charge", () -> new SimpleParticleType(false));

    private MoreMekaSuitModulesParticles() {
    }
}
