package com.brandon3055.draconicevolution.blocks.reactor;

import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorComponent;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorInjector;
import com.brandon3055.draconicevolution.client.render.effect.ReactorBeamFX;
import com.brandon3055.draconicevolution.client.sound.ReactorSound;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by brandon3055 on 11/02/2017.
 */
public class ReactorEffectHandler {

    private TileReactorCore reactor;
    @OnlyIn(Dist.CLIENT)
    private ReactorBeamFX[] effects;

    @OnlyIn(Dist.CLIENT)
    private ReactorSound reactorSound;

    public ReactorEffectHandler(TileReactorCore reactor) {
        this.reactor = reactor;
    }

    @OnlyIn(Dist.CLIENT)
    public void updateEffects() {
        if (effects == null) {
            effects = new ReactorBeamFX[6];
        }

//        reactorSound.donePlaying = true;
//        reactorSound = null;;
        if ((reactorSound == null || reactorSound.isDonePlaying() || !Minecraft.getInstance().getSoundHandler().isPlaying(reactorSound)) && reactor.reactorState.get().isShieldActive() && reactor.shieldCharge.get() > 0) {
            reactorSound = new ReactorSound(reactor);
            Minecraft.getInstance().getSoundHandler().play(reactorSound);
        }
        else if (reactorSound != null && (!reactor.reactorState.get().isShieldActive() || reactor.shieldCharge.get() <= 0)) {
            reactorSound.donePlaying = true;
        }

        if (reactor.reactorState.get() == TileReactorCore.ReactorState.INVALID || reactor.shieldAnimationState <= 0) {
            return;
        }

        for (Direction facing : Direction.values()) {
            int index = facing.getIndex();
            TileReactorComponent component = reactor.getComponent(facing);

            if (component == null) {
                if (effects[index] != null) {
                    effects[index].setExpired();
                    effects[index] = null;
                }
                continue;
            }

            if (effects[index] != null && effects[index].isAlive()) {
                effects[index].updateFX((float) reactor.shieldAnimationState, 1);//todo Power Stat
                continue;
            }

            ReactorBeamFX beamFX = new ReactorBeamFX(reactor.getWorld(), Vec3D.getCenter(component.getPos()), component.facing.get(), reactor, component instanceof TileReactorInjector);
            effects[index] = beamFX;
            //TODO particles
//            BCEffectHandler.spawnGLParticle(ReactorBeamFX.FX_HANDLER, beamFX);
        }
    }

}
