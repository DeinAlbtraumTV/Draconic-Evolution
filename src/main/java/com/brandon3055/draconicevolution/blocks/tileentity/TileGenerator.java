package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.api.power.OPStorage;
import com.brandon3055.brandonscore.blocks.TileBCore;

import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.client.utils.SimpleAnimHandler;
import com.brandon3055.brandonscore.inventory.ItemHandlerIOControl;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.brandonscore.lib.IRSSwitchable;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedEnum;
import com.brandon3055.brandonscore.lib.datamanager.ManagedInt;
import com.brandon3055.brandonscore.utils.EnergyUtils;

import com.brandon3055.draconicevolution.DEContent;
import com.brandon3055.draconicevolution.DraconicEvolution;

import com.brandon3055.draconicevolution.blocks.machines.Generator;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.client.sound.GeneratorSoundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;


import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.*;

public class TileGenerator extends TileBCore implements ITickableTileEntity, IRSSwitchable {

    private final SimpleAnimHandler animHandler;
    @OnlyIn(Dist.CLIENT)
    private GeneratorSoundHandler sound = new GeneratorSoundHandler(this);

    /**
     * The fuel value of the last item that was consumed.
     */
    public final ManagedInt fuelValue = register(new ManagedInt("fuel_value", 1, SAVE_BOTH_SYNC_CONTAINER));
    /**
     * The remaining fuel value from the last item that was consumed.
     */
    public final ManagedInt fuelRemaining = register(new ManagedInt("fuel_remaining", 0, SAVE_BOTH_SYNC_CONTAINER));
    public final ManagedEnum<Direction> facing = register(new ManagedEnum<>("facing", Direction.NORTH, SAVE_NBT_SYNC_TILE, TRIGGER_UPDATE));
    public final ManagedInt productionRate = register(new ManagedInt("prod_rate", 0, SYNC_CONTAINER));
    public final ManagedEnum<Mode> mode = register(new ManagedEnum<>("mode", Mode.NORMAL, SAVE_BOTH_SYNC_TILE, CLIENT_CONTROL));
    public final ManagedBool active = register(new ManagedBool("active", false, SAVE_BOTH_SYNC_TILE, TRIGGER_UPDATE));

    //These are buffers for floating point to integer conversion.
    //I dont bother saving these because worst case you loose 0.99OP or 0.99fuel
    private double consumptionBuffer = 0;
    private double productionBuffer = 0;

    public TileItemStackHandler itemHandler = new TileItemStackHandler(4);
    public OPStorage opStorage = new OPStorage(100000, 0, 32000);

    public TileGenerator() {
        super(DEContent.tile_generator);

        //Power Cap
        capManager.setManaged("energy", CapabilityOP.OP, opStorage).saveBoth().syncContainer();

        //Inventory Cap
        capManager.setInternalManaged("inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemHandler).saveBoth();
        itemHandler.setStackValidator((slot, stack) -> slot > 2 || ForgeHooks.getBurnTime(stack) > 0);
        setupPowerSlot(itemHandler, 3, opStorage, true);
        capManager.set(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, new ItemHandlerIOControl(itemHandler).setExtractCheck(this::canExtractItem));
        installIOTracker(opStorage);


        animHandler = new SimpleAnimHandler(new ResourceLocation(DraconicEvolution.MODID, "asms/block/generator.json"));
    }

    @Override
    public void tick() {
        super.tick();
        if (world.isRemote) {
            animHandler.setSpeed(active.get() ? mode.get().animFanSpeed : 0F, 0.3F);
            animHandler.updateAnimation();
            updateSoundAndFX();
            return;
        }

        //Update active State
        active.set(fuelRemaining.get() > 0 && opStorage.getOPStored() < opStorage.getMaxOPStored() && isTileEnabled());

        if (active.get()) {
            double genRate = 1F - ((double) opStorage.getOPStored() / (double) opStorage.getMaxOPStored());
            genRate = Math.min(1F, genRate * 4F);
            double energy = Math.max(1, genRate * mode.get().powerOutput);
            double fuel = energy / mode.get().energyPerFuelUnit;
            consumptionBuffer += fuel;

            if (fuelRemaining.get() < consumptionBuffer) {
                tryRefuel();
                if (fuelRemaining.get() < consumptionBuffer) {
                    consumptionBuffer = fuelRemaining.get();
                    energy = consumptionBuffer * mode.get().energyPerFuelUnit;
                }
            }

            productionRate.set((int) energy);

            productionBuffer += energy;
            if (consumptionBuffer >= 1) {
                fuelRemaining.subtract((int) consumptionBuffer);
                consumptionBuffer = consumptionBuffer % 1D;
            }
            if (productionBuffer >= 1) {
                opStorage.modifyEnergyStored((int) productionBuffer);
                productionBuffer = productionBuffer % 1D;
            }
        } else {
            productionRate.set(0);
        }

        if (isTileEnabled() && fuelRemaining.get() <= 0) {
            tryRefuel();
        }

        opStorage.modifyEnergyStored(-sendEnergyToAll(opStorage.getMaxExtract(), opStorage.getOPStored()));
    }

    public void tryRefuel() {
        for (int i = 0; i < 3; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                int itemBurnTime = ForgeHooks.getBurnTime(stack);

                if (itemBurnTime > 0) {
                    if (stack.getCount() == 1) {
                        stack = stack.getItem().getContainerItem(stack);
                    } else {
                        stack.shrink(1);
                    }
                    itemHandler.setStackInSlot(i, stack);
                    fuelValue.set(itemBurnTime);
                    fuelRemaining.add(itemBurnTime);
                    return;
                }
            }
        }
    }

    private boolean canExtractItem(int slot, ItemStack stack) {
        return slot == 3 && EnergyUtils.isFullyOrInvalid(stack);
    }

    //Render Stuff

    @OnlyIn(Dist.CLIENT)
    private void updateSoundAndFX() {
        sound.update();
        if (!active.get() || pos.distanceSq(Minecraft.getInstance().player.getPosition()) > 16 * 16) {
            return;
        }
        Random rand = world.rand;

        double p = 0.0625;
        if (rand.nextInt(17 - (mode.get().index * 4)) == 0) {
            Direction enumfacing = getBlockState().get(Generator.FACING);

            double pgx = (p * 7.5D) + (rand.nextInt(6) * (p));
            double pgy = 0.3D;
            double pgz = 0.5D;
            double outOffset = 0.48D;

            switch (enumfacing) {
                case WEST:
                    spawnGrillParticle(rand, pos.getX() + pgz - outOffset, pos.getY() + pgy, pos.getZ() + pgx);
                    break;
                case EAST:
                    spawnGrillParticle(rand, pos.getX() + pgz + outOffset, pos.getY() + pgy, pos.getZ() + (1D - pgx));
                    break;
                case NORTH:
                    spawnGrillParticle(rand, pos.getX() + (1D - pgx), pos.getY() + pgy, pos.getZ() + pgz - outOffset);
                    break;
                case SOUTH:
                    spawnGrillParticle(rand, pos.getX() + pgx, pos.getY() + pgy, pos.getZ() + pgz + outOffset);
            }
        }
        if (rand.nextInt(5 - mode.get().index) == 0) {
            Direction enumfacing = getBlockState().get(Generator.FACING);

            double pex = (p * 3D) + (rand.nextInt(5) * p);
            double pey = p * 6.5;
            double pez = 0.5D;
            double exhaustOffset = 0.5D - ((mode.get().index / 4D) * 0.1D);
            double exhaustVelocity = (0.08 + (rand.nextDouble() * 0.02)) * (mode.get().index / 4D);

            switch (enumfacing) {
                case WEST:
                    spawnExhaustParticle(rand, pos.getX() + (1D - pex), pos.getY() + pey, pos.getZ() + pez - exhaustOffset, new Vec3D(0, 0, -exhaustVelocity));
                    break;
                case EAST:
                    spawnExhaustParticle(rand, pos.getX() + pex, pos.getY() + pey, pos.getZ() + pez + exhaustOffset, new Vec3D(0, 0, exhaustVelocity));
                    break;
                case NORTH:
                    spawnExhaustParticle(rand, pos.getX() + pez + exhaustOffset, pos.getY() + pey, pos.getZ() + (1D - pex), new Vec3D(exhaustVelocity, 0, 0));
                    break;
                case SOUTH:
                    spawnExhaustParticle(rand, pos.getX() + pez - exhaustOffset, pos.getY() + pey, pos.getZ() + pex, new Vec3D(-exhaustVelocity, 0, 0));
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnGrillParticle(Random rand, double x, double y, double z) {
        world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
        if (mode.get() == Mode.PERFORMANCE_PLUS && rand.nextInt(8) == 0) {
            //ToDo
//            BCEffectHandler.spawnFX(DEParticles.FLAME, world, new Vec3D(x, y, z), new Vec3D(), 127);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnExhaustParticle(Random rand, double x, double y, double z, Vec3D velocity) {
        if (rand.nextBoolean()) {
            world.addParticle(ParticleTypes.SMOKE, x, y, z, velocity.x, velocity.y, velocity.z);
            world.addParticle(ParticleTypes.SMOKE, x, y, z, velocity.x, velocity.y, velocity.z);
        } else {
//            BCEffectHandler.spawnFX(DEParticles.FLAME, world, new Vec3D(x, y, z), velocity, 32, (int) ((0.8 + (rand.nextDouble() * 0.2)) * 255));
        }
    }

    @Override
    public boolean hasFastRenderer() {
        return true;
    }

//    @Nonnull
//    @Override
//    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
//        if (capability == CapabilityAnimation.ANIMATION_CAPABILITY) {
//            return CapabilityAnimation.ANIMATION_CAPABILITY.cast(animHandler.asm);
//        }
//        return super.getCapability(capability, side);
//    }


    public enum Mode {
        ECO_PLUS(0, 50, 5, 0.3F),
        ECO(1, 15, 20, 0.4F),
        NORMAL(2, 10, 40, 0.6F),
        PERFORMANCE(3, 8, 80, 0.8F),
        PERFORMANCE_PLUS(4, 5, 300, 1.0F);

        public final int index;
        public final int energyPerFuelUnit;
        public final int powerOutput;
        private float animFanSpeed;

        Mode(int index, int energyPerFuelUnit, int powerOutput, float animFanSpeed) {
            this.index = index;
            this.energyPerFuelUnit = energyPerFuelUnit;
            this.powerOutput = powerOutput;
            this.animFanSpeed = animFanSpeed;
        }

        public Mode next(boolean prev) {
            if (prev) {
                return values()[index - 1 < 0 ? values().length - 1 : index - 1];
            }
            return values()[index + 1 == values().length ? 0 : index + 1];
        }

        public int getEfficiency() {
            return (int) ((energyPerFuelUnit / 10F) * 100F);
        }

        public String unlocalizedName() {
            return "gui.de.generator.mode." + name().toLowerCase();
        }

    }
}

/*
 * Items.STICK = 100 fuel value
 *
 * Old DE            100 -> 1428 (1 > 14.28)
 * Stirling          100 -> 1200 (1 > 12)
 * Stirling 2        100 -> 1520 (1 > 15.5)
 * Stirling 3        100 -> 1800 (1 > 18)
 * Survival          100 -> 5000 (1 > 50)
 * Furnace Generator 100 -> 1000 (1 > 10)
 * Steam Dynamo      300 -> 3000 (1 > 10)
 * Steam Dynamo Eff  300 -> 4800 (1 > 16)
 *
 *
 * 1 fuel = 10 energy
 * 10 / 10
 *
 *
 *
 * */