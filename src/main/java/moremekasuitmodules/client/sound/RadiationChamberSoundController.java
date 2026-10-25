package moremekasuitmodules.client.sound;

import java.util.HashMap;
import java.util.Map;
import moremekasuitmodules.common.block.entity.RadiationChamberBlockEntity;
import moremekasuitmodules.common.registries.MoreMekaSuitModulesSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/** Client-only controller for the chamber's quiet, positional reaction loop. */
public final class RadiationChamberSoundController {
    private static final float PLAYBACK_VOLUME = 0.24F;
    private static final Map<BlockPos, ChamberLoopSound> ACTIVE_SOUNDS = new HashMap<>();

    private RadiationChamberSoundController() {
    }

    public static void tick(RadiationChamberBlockEntity chamber) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = chamber.getLevel();
        BlockPos pos = chamber.getBlockPos().immutable();
        ChamberLoopSound sound = ACTIVE_SOUNDS.get(pos);
        boolean shouldPlay = level != null && level.isClientSide && !chamber.isRemoved()
                && minecraft.level == level && chamber.isReactionActive();

        if (!shouldPlay) {
            if (sound != null) {
                sound.stopFromController();
                ACTIVE_SOUNDS.remove(pos, sound);
            }
            return;
        }

        if (sound != null && sound.chamber != chamber) {
            sound.stopFromController();
            ACTIVE_SOUNDS.remove(pos, sound);
            sound = null;
        }
        if (sound == null || sound.isStopped() || !minecraft.getSoundManager().isActive(sound)) {
            sound = new ChamberLoopSound(chamber, pos);
            ACTIVE_SOUNDS.put(pos, sound);
            minecraft.getSoundManager().play(sound);
        }
    }

    private static final class ChamberLoopSound extends AbstractTickableSoundInstance {
        private final RadiationChamberBlockEntity chamber;
        private final BlockPos pos;

        private ChamberLoopSound(RadiationChamberBlockEntity chamber, BlockPos pos) {
            super(MoreMekaSuitModulesSounds.RADIATION_CHAMBER_LOOP.get(), SoundSource.BLOCKS, RandomSource.create());
            this.chamber = chamber;
            this.pos = pos;
            this.looping = true;
            this.delay = 0;
            this.volume = PLAYBACK_VOLUME;
            this.pitch = 1.0F;
            this.attenuation = SoundInstance.Attenuation.LINEAR;
            updatePosition();
        }

        @Override
        public void tick() {
            Level level = chamber.getLevel();
            if (chamber.isRemoved() || level == null || !level.isClientSide || !chamber.isReactionActive()
                    || Minecraft.getInstance().level != level) {
                stop();
                ACTIVE_SOUNDS.remove(pos, this);
                return;
            }
            updatePosition();
        }

        private void updatePosition() {
            this.x = pos.getX() + 0.5D;
            this.y = pos.getY() + 0.5D;
            this.z = pos.getZ() + 0.5D;
        }

        private void stopFromController() {
            stop();
        }
    }
}
