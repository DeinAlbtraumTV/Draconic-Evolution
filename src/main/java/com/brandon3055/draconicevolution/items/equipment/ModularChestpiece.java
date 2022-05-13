package com.brandon3055.draconicevolution.items.equipment;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.capability.MultiCapabilityProvider;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.config.DecimalProperty;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.JumpData;
import com.brandon3055.draconicevolution.api.modules.data.SpeedData;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import com.brandon3055.draconicevolution.client.model.ModularArmorModel;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.init.ModuleCfg;
import com.brandon3055.draconicevolution.init.TechProperties;
import com.brandon3055.draconicevolution.integration.equipment.EquipmentManager;
import com.brandon3055.draconicevolution.integration.equipment.IDEEquipment;
import net.covers1624.quack.util.SneakyUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by brandon3055 on 21/5/20.
 */
public class ModularChestpiece extends ArmorItem implements IModularArmor, IDEEquipment {
    private final TechLevel techLevel;

    public ModularChestpiece(TechProperties props) {
        super(ArmorMaterials.DIAMOND, EquipmentSlot.CHEST, props);
        this.techLevel = props.getTechLevel();
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        if (entity instanceof LivingEntity && !EquipmentManager.findItem(e -> e.getItem() instanceof ModularChestpiece, (LivingEntity) entity).isEmpty()) {
            return false;
        }
        return Mob.getEquipmentSlotForItem(stack) == armorType;
    }

    @Override
    public boolean canEquip(LivingEntity livingEntity, String identifier) {
        if (!identifier.equals("body") || !EquipmentManager.findItem(e -> e.getItem() instanceof ModularChestpiece, livingEntity).isEmpty()) {
            return false;
        }

        return !(livingEntity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ModularChestpiece);
    }

    @Override
    public TechLevel getTechLevel() {
        return techLevel;
    }

    @Override
    public ModuleHostImpl createHost(ItemStack stack) {
        ModuleHostImpl host = new ModuleHostImpl(techLevel, ModuleCfg.chestpieceWidth(techLevel), ModuleCfg.chestpieceHeight(techLevel), "chestpiece", ModuleCfg.removeInvalidModules);
        host.addCategories(ModuleCategory.CHESTPIECE);
        host.addPropertyBuilder(props -> {
            SpeedData speed = host.getModuleData(ModuleTypes.SPEED);
            if (speed != null) {
                Supplier<Double> speedGetter = () -> {
                    SpeedData data = host.getModuleData(ModuleTypes.SPEED);
                    double maxSpeed = data == null ? 0 : data.getSpeedMultiplier();
                    if (DEConfig.armorSpeedLimit != -1) {
                        maxSpeed = Math.min(maxSpeed, DEConfig.armorSpeedLimit);
                    }
                    return maxSpeed;
                };

                props.add(new DecimalProperty("walk_speed", 0).min(0).max(speedGetter).setFormatter(ConfigProperty.DecimalFormatter.PLUS_PERCENT_0));
                props.add(new DecimalProperty("run_speed", speedGetter.get()).min(0).max(speedGetter).setFormatter(ConfigProperty.DecimalFormatter.PLUS_PERCENT_0));
            }

            JumpData jump = host.getModuleData(ModuleTypes.JUMP_BOOST);
            if (jump != null) {
                Supplier<Double> jumpGetter = () -> {
                    JumpData data = host.getModuleData(ModuleTypes.JUMP_BOOST);
                    return data == null ? 0 : data.getMultiplier();
                };

                props.add(new DecimalProperty("jump_boost_run", 0).min(0).max(jumpGetter).setFormatter(ConfigProperty.DecimalFormatter.PLUS_PERCENT_0));
                props.add(new DecimalProperty("jump_boost", jumpGetter.get()).min(0).max(jumpGetter).setFormatter(ConfigProperty.DecimalFormatter.PLUS_PERCENT_0));
            }
        });
        return host;
    }

    @Nullable
    @Override
    public ModularOPStorage createOPStorage(ItemStack stack, ModuleHostImpl host) {
        return new ModularOPStorage(host, EquipCfg.getBaseChestpieceEnergy(techLevel), EquipCfg.getBaseChestpieceTransfer(techLevel));
    }

    @Override
    public void initCapabilities(ItemStack stack, ModuleHostImpl host, MultiCapabilityProvider provider) {
        EquipmentManager.addCaps(stack, provider);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        addModularItemInformation(stack, worldIn, tooltip, flagIn);
    }

    @OnlyIn(Dist.CLIENT)
    private HumanoidModel<?> model;

    @OnlyIn(Dist.CLIENT)
    private HumanoidModel<?> model_on_armor;

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Nullable
            @Override
            public HumanoidModel<?> getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
                if (model == null) {
                    model = new ModularArmorModel(1F, techLevel, false);
                }
                return SneakyUtils.unsafeCast(model);
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    public <A extends HumanoidModel<?>> A getChestPieceModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, boolean onArmor) {
        if (model == null || model_on_armor == null) {
            model = new ModularArmorModel(1F, techLevel, false);
            model_on_armor = new ModularArmorModel(1F, techLevel, true);
        }

        return SneakyUtils.unsafeCast(onArmor ? model_on_armor : model);
    }


    public static ItemStack getChestpiece(LivingEntity entity) {
        ItemStack stack = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (stack.getItem() instanceof ModularChestpiece) {
            return stack;
        }
        return EquipmentManager.findItem(e -> e.getItem() instanceof ModularChestpiece, entity);
    }

    @Override
    public boolean canBeHurtBy(DamageSource source) {
        return source == DamageSource.OUT_OF_WORLD;
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, Level level) {
        return -32768;
    }
}
