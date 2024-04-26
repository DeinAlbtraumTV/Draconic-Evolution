package com.brandon3055.draconicevolution.entity;

import com.brandon3055.brandonscore.handlers.ProcessHandler;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.client.render.particle.ChaosImplosionParticle;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Created by brandon3055 on 3/10/2015.
 */
public class ChaosImplosionEntity extends Entity {
    protected static final EntityDataAccessor<Integer> TICKS = SynchedEntityData.defineId(ChaosImplosionEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<BlockPos> posLock = SynchedEntityData.defineId(ChaosImplosionEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<String> dimLock = SynchedEntityData.defineId(ChaosImplosionEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> positionLocked = SynchedEntityData.defineId(ChaosImplosionEntity.class, EntityDataSerializers.BOOLEAN);

    public ChaosImplosionEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.noCulling = true;
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(TICKS, 0);
        this.entityData.define(posLock, blockPosition());
        this.entityData.define(dimLock, level.dimension().location().toString());
        this.entityData.define(positionLocked, false);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    //    public EntityChaosImplosion(World world) {
//        super(world);
//        this.noClip = true;
//        this.setSize(0F, 0F);
//    }

    @Override
    public boolean shouldRenderAtSqrDistance(double p_70112_1_) {
        return true;
    }

//    @Override
//    protected void entityInit() {
//        dataManager.register(TICKS, ticksExisted);
//    }


    @Override
    public void tick() {
        if (!level.isClientSide) {
            entityData.set(TICKS, tickCount);

            if (!entityData.get(positionLocked)) {
                entityData.set(positionLocked, true);
                entityData.set(posLock, blockPosition());
                entityData.set(dimLock, level.dimension().location().toString());
            }

            //In case we get moved, teleported or something else, discard.
            //Don't want to implode a base because someone moved it via AE2 Spacial or something
            //TODO: Currently always removes the explosion...
            if (entityData.get(positionLocked) && hasBeenMoved() && false) {
                DraconicEvolution.LOGGER.info("Discarding Implosion: We ain't got the moves");
                discard();
            }
        }

//        Vec3D pos = new Vec3D(getPosX(), getPosY(), getPosZ());

        //TODO Overhaul Visuals
        //TODO Add visuals for last 100 ticks

        if (tickCount < 30 && tickCount % 5 == 0 && level.isClientSide) {
            //TODO Particles
//            BCEffectHandler.spawnFX(DEParticles.CHAOS_IMPLOSION, world, pos, pos, 1024D, 1);
//            DraconicEvolution.proxy.spawnParticle(new Particles.ChaosExpansionParticle(world, posX, posY, posZ, false), 512);
        }
        if (tickCount >= 100 && tickCount < 130 && tickCount % 5 == 0 && level.isClientSide) {
//            BCEffectHandler.spawnFX(DEParticles.CHAOS_IMPLOSION, world, pos, pos, 1024D, 2);
//            DraconicEvolution.proxy.spawnParticle(new Particles.ChaosExpansionParticle(world, posX, posY, posZ, true), 512);
        }

        if (tickCount % ChaosImplosionParticle.getLifetimeFromPhase(tickCount / 100) == 0) {
            level.addParticle(DEParticles.chaos_implosion, this.getX(), this.getY(), this.getZ(), tickCount / 100, 0, 0);
        }

        if (tickCount < 100) {
            return;
        }

        if (tickCount < 600) {
            for (int i = 0; i < 10; i++) {
                double x = getX() - 18 + random.nextDouble() * 36;
                double y = getY() - 8 + random.nextDouble() * 16;
                double z = getZ() - 18 + random.nextDouble() * 36;
                if (level.isClientSide) {
//                    level.addParticle(DEParticles.chaos_implosion, this.getX(), this.getY(), this.getZ(), tickCount / 100, 0, 0);
//                    BCEffectHandler.spawnFX(DEParticles.CHAOS_IMPLOSION, world, new Vec3D(x, y, z), pos, 512D, 0);
                }
            }
        }

        if (tickCount > 130 && level.isClientSide && tickCount % 2 == 0) {
            shakeScreen();
        }

        if (tickCount >= 700 && level.isClientSide) {
//            level.addParticle(DEParticles.chaos_implosion, this.getX(), this.getY(), this.getZ(), tickCount % 100, 0, 0);
//            BCEffectHandler.spawnFX(DEParticles.CHAOS_IMPLOSION, world, pos, pos, 1024D, 5);
        }

        if (tickCount == 700 && !level.isClientSide) {
            ProcessHandler.addProcess(new ProcessChaosImplosion(level, (int) getX(), (int) getY(), (int) getZ()));
            DraconicNetwork.sendExplosionEffect(level.dimension(), blockPosition(), 200 * 4, true);
        }

        if (tickCount > 720) {
            discard();
        }
    }


    private void shakeScreen() {
        float intensity = (tickCount - 130) / 100F;
        if (intensity > 1F) intensity = 1F;

        @SuppressWarnings("unchecked") List<Player> players = level.getEntitiesOfClass(Player.class, getBoundingBox().inflate(200, 200, 200));

        for (Player player : players) {
            float x = (random.nextFloat() - 0.5F) * 2 * intensity;
            float z = (random.nextFloat() - 0.5F) * 2 * intensity;
            player.move(MoverType.SELF, new Vec3(x / 5D, 0, z / 5D));
            player.setYRot(player.getYRot() - (x * 2));
            player.setXRot(player.getXRot() - (z * 2));
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }

    private boolean hasBeenMoved() {
        return entityData.get(posLock).asLong() != blockPosition().asLong() || entityData.get(dimLock).equals(level.dimension().location().toString());
    }
}
