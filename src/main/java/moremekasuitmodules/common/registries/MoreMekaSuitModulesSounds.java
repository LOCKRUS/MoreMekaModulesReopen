package moremekasuitmodules.common.registries;

import moremekasuitmodules.common.MoreMekaModulesReopen;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MoreMekaSuitModulesSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, MoreMekaModulesReopen.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> ANTIMATTER_STRIKE_FIRE = SOUND_EVENTS.register(
            "antimatter_strike_fire",
            () -> SoundEvent.createVariableRangeEvent(MoreMekaModulesReopen.rl("antimatter_strike_fire")));

    public static final DeferredHolder<SoundEvent, SoundEvent> ANTIMATTER_CHARGE_START = SOUND_EVENTS.register(
            "antimatter_charge_start",
            () -> SoundEvent.createVariableRangeEvent(MoreMekaModulesReopen.rl("antimatter_charge_start")));

    public static final DeferredHolder<SoundEvent, SoundEvent> RADIATION_CHAMBER_LOOP = SOUND_EVENTS.register(
            "radiation_chamber_loop",
            () -> SoundEvent.createVariableRangeEvent(MoreMekaModulesReopen.rl("radiation_chamber_loop")));

    private MoreMekaSuitModulesSounds() {
    }
}
