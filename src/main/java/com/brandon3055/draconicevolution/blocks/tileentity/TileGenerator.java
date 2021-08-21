package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.api.power.OPStorage;
import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.client.particle.IntParticleType.IntParticleData;
import com.brandon3055.brandonscore.inventory.ContainerBCTile;
import com.brandon3055.brandonscore.inventory.ItemHandlerIOControl;
import com.brandon3055.brandonscore.inventory.TileItemStackHandler;
import com.brandon3055.brandonscore.lib.IActivatableTile;
import com.brandon3055.brandonscore.lib.IRSSwitchable;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedEnum;
import com.brandon3055.brandonscore.lib.datamanager.ManagedInt;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.blocks.machines.Generator;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.client.sound.GeneratorSoundHandler;
import com.brandon3055.draconicevolution.inventory.GuiLayoutFactories;
import com.brandon3055.draconicevolution.lib.ISidedTileHandler;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Random;

import static com.brandon3055.brandonscore.lib.datamanager.DataFlags.*;

public class TileGenerator extends TileBCore implements ITickableTileEntity, IRSSwitchable, INamedContainerProvider, IActivatableTile {

    private ISidedTileHandler soundHandler = DraconicEvolution.proxy.createGeneratorSoundHandler(this);

    /**
     * The fuel value of the last item that was consumed.
     */
    public final ManagedInt fuelValue = register(new ManagedInt("fuel_value", 1, SAVE_BOTH_SYNC_CONTAINER));
    /**
     * The remaining fuel value from the last item that was consumed.
     */
    public final ManagedInt fuelRemaining = register(new ManagedInt("fuel_remaining", 0, SAVE_BOTH_SYNC_CONTAINER));
    public final ManagedInt productionRate = register(new ManagedInt("prod_rate", 0, SYNC_CONTAINER));
    public final ManagedEnum<Mode> mode = register(new ManagedEnum<>("mode", Mode.NORMAL, SAVE_BOTH_SYNC_TILE, CLIENT_CONTROL));
    public final ManagedBool active = register(new ManagedBool("active", false, SAVE_BOTH_SYNC_TILE, TRIGGER_UPDATE));

    //These are buffers for floating point to integer conversion.
    //I dont bother saving these because worst case you loose 0.99OP or 0.99fuel
    private double consumptionBuffer = 0;
    private double productionBuffer = 0;
    public float rotation = 0;
    public float rotationSpeed = 0;

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


//        animHandler = new SimpleAnimHandler(new ResourceLocation(DraconicEvolution.MODID, "asms/block/generator.json"));
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide) {
            rotationSpeed = (active.get() ? mode.get().animFanSpeed : 0F);
            rotation += rotationSpeed;
            updateSoundAndFX();
            return;
        }

        //Update active State
        boolean last = active.get();
        active.set(fuelRemaining.get() > 0 && opStorage.getOPStored() < opStorage.getMaxOPStored() && isTileEnabled());
        if (active.get() != last) {
            level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(Generator.ACTIVE, active.get()));
        }

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
        soundHandler.tick();
        if (!active.get() || worldPosition.distSqr(Minecraft.getInstance().player.blockPosition()) > 16 * 16) {
            return;
        }
        Random rand = level.random;

        double p = 0.0625;
        if (rand.nextInt(17 - (mode.get().index * 4)) == 0) {
            Direction enumfacing = getBlockState().getValue(Generator.FACING);

            double pgx = (p * 7.5D) + (rand.nextInt(6) * (p));
            double pgy = 0.3D;
            double pgz = 0.5D;
            double outOffset = 0.48D;

            switch (enumfacing) {
                case WEST:
                    spawnGrillParticle(rand, worldPosition.getX() + pgz - outOffset, worldPosition.getY() + pgy, worldPosition.getZ() + pgx);
                    break;
                case EAST:
                    spawnGrillParticle(rand, worldPosition.getX() + pgz + outOffset, worldPosition.getY() + pgy, worldPosition.getZ() + (1D - pgx));
                    break;
                case NORTH:
                    spawnGrillParticle(rand, worldPosition.getX() + (1D - pgx), worldPosition.getY() + pgy, worldPosition.getZ() + pgz - outOffset);
                    break;
                case SOUTH:
                    spawnGrillParticle(rand, worldPosition.getX() + pgx, worldPosition.getY() + pgy, worldPosition.getZ() + pgz + outOffset);
            }
        }
        if (rand.nextInt(5 - mode.get().index) == 0) {
            Direction enumfacing = getBlockState().getValue(Generator.FACING);

            double pex = (p * 3D) + (rand.nextInt(5) * p);
            double pey = p * 6.5;
            double pez = 0.5D;
            double exhaustOffset = 0.4D;
            double exhaustVelocity = 0.02 + ((0.08 + (rand.nextDouble() * 0.02)) * (mode.get().index / 4D));

            switch (enumfacing) {
                case WEST:
                    spawnExhaustParticle(rand, worldPosition.getX() + (1D - pex), worldPosition.getY() + pey, worldPosition.getZ() + pez - exhaustOffset, new Vec3D(0, 0, -exhaustVelocity));
                    break;
                case EAST:
                    spawnExhaustParticle(rand, worldPosition.getX() + pex, worldPosition.getY() + pey, worldPosition.getZ() + pez + exhaustOffset, new Vec3D(0, 0, exhaustVelocity));
                    break;
                case NORTH:
                    spawnExhaustParticle(rand, worldPosition.getX() + pez + exhaustOffset, worldPosition.getY() + pey, worldPosition.getZ() + (1D - pex), new Vec3D(exhaustVelocity, 0, 0));
                    break;
                case SOUTH:
                    spawnExhaustParticle(rand, worldPosition.getX() + pez - exhaustOffset, worldPosition.getY() + pey, worldPosition.getZ() + pex, new Vec3D(-exhaustVelocity, 0, 0));
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnGrillParticle(Random rand, double x, double y, double z) {
        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
        if (mode.get() != Mode.PERFORMANCE_PLUS && rand.nextInt(8) == 0) {
            level.addParticle(new IntParticleData(DEParticles.flame, 127), x, y, z, 0, 0, 0);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnExhaustParticle(Random rand, double x, double y, double z, Vec3D velocity) {
        if (rand.nextBoolean()) {
            level.addParticle(ParticleTypes.SMOKE, x, y, z, velocity.x, velocity.y, velocity.z);
            level.addParticle(ParticleTypes.SMOKE, x, y, z, velocity.x, velocity.y, velocity.z);
        } else {
            level.addParticle(new IntParticleData(DEParticles.flame, 64, (int) ((0.1 + (rand.nextDouble() * 0.05)) * 255)), x, y, z, velocity.x, velocity.y, velocity.z);
        }
    }

    @Nullable
    @Override
    public Container createMenu(int currentWindowIndex, PlayerInventory playerInventory, PlayerEntity player) {
        return new ContainerBCTile<>(DEContent.container_generator, currentWindowIndex, player.inventory, this, GuiLayoutFactories.GENERATOR_LAYOUT);
    }

    @Override
    public boolean onBlockActivated(BlockState state, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (player instanceof ServerPlayerEntity) {
            NetworkHooks.openGui((ServerPlayerEntity) player, this, worldPosition);
        }
        return true;
    }

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
            return "gui.draconicevolution.generator.mode_" + name().toLowerCase(Locale.ENGLISH);
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