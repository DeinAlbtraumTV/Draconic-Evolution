package com.brandon3055.draconicevolution.client.render.particle;

import com.brandon3055.draconicevolution.DraconicEvolution;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChaosImplosionParticle extends TextureSheetParticle {
    private int animPhase;

    private ChaosImplosionParticle(ClientLevel world, double x, double y, double z, int animPhase) {
        super(world, x, y, z, 0, 0, 0);
        this.x = x;
        this.y = y;
        this.z = z;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;

        this.animPhase = animPhase;

        this.lifetime = getLifetimeFromPhase(this.animPhase);
    }

    @Override
    public void tick() {
        super.tick();

        switch (animPhase) {
            case 0,1,2,3,4,5,6 -> {
                this.quadSize = 100 - (age * (100f / lifetime));
            }
            case 7 -> {
                this.quadSize = 4*age;
            }
        }

        if (this.age++ >= this.lifetime) {
            remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static int getLifetimeFromPhase(int phase) {
        return switch (phase) {
            case 0 -> 50;
            case 1, 7 -> 40;
            case 2 -> 30;
            case 3, 4, 5, 6 -> 20;
            //Prevent particle from spawning if phase is out of expected range
            default -> 0;
        };
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Factory(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double animPhase, double ySpeed, double zSpeed) {
            ChaosImplosionParticle implosionParticle = new ChaosImplosionParticle(level, x, y, z, (int) animPhase);
            implosionParticle.pickSprite(sprites);
            return implosionParticle;
        }
    }
}
