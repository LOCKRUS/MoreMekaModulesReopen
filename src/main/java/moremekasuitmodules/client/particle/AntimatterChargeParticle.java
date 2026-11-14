package moremekasuitmodules.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public final class AntimatterChargeParticle extends TextureSheetParticle {
    private static final float MAX_ALPHA = 0.56F;
    private static final float MIN_SCALE = 0.12F;
    private static final float MAX_SCALE = 0.14F;
    private static final int LIFETIME_TICKS = 18;

    private final SpriteSet sprites;
    private final double originX;
    private final double originY;
    private final double originZ;
    private final double phase;
    private final double angularSpeed;
    private final double baseRadius;
    private final double risePerTick;
    private final float initialScale;
    private final float initialAlpha;

    private AntimatterChargeParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.originX = x;
        this.originY = y;
        this.originZ = z;
        this.phase = this.random.nextDouble() * Math.PI * 2.0D;
        this.angularSpeed = 0.34D + this.random.nextDouble() * 0.10D;
        this.baseRadius = 0.045D + this.random.nextDouble() * 0.012D;
        this.risePerTick = 0.022D + this.random.nextDouble() * 0.006D;
        this.initialScale = MIN_SCALE + this.random.nextFloat() * (MAX_SCALE - MIN_SCALE);
        this.initialAlpha = MAX_ALPHA * (0.9F + this.random.nextFloat() * 0.1F);
        this.quadSize = this.initialScale;
        this.alpha = this.initialAlpha;
        this.lifetime = LIFETIME_TICKS;
        this.hasPhysics = false;
        this.setColor(0.68F, 0.38F, 0.93F);
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        double progress = this.age / (double) this.lifetime;
        double angle = this.phase + this.age * this.angularSpeed;
        double radius = this.baseRadius + progress * 0.04D;
        this.setPos(
                this.originX + Math.cos(angle) * radius,
                this.originY + this.age * this.risePerTick,
                this.originZ + Math.sin(angle) * radius);
        this.alpha = this.initialAlpha * (1.0F - (float) progress);
        this.quadSize = this.initialScale * (1.0F - 0.12F * (float) progress);
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new AntimatterChargeParticle(level, x, y, z, this.sprites);
        }
    }
}
