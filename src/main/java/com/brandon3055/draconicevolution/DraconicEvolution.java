package com.brandon3055.draconicevolution;

import com.brandon3055.brandonscore.api.power.OPStorage;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.IngredientStack;
import com.brandon3055.draconicevolution.client.ClientProxy;
import com.brandon3055.draconicevolution.client.DEShaders;
import com.brandon3055.draconicevolution.command.CommandKaboom;
import com.brandon3055.draconicevolution.command.CommandMakeRecipe;
import com.brandon3055.draconicevolution.command.CommandRespawnGuardian;
import com.brandon3055.draconicevolution.init.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Mod(DraconicEvolution.MODID)
public class DraconicEvolution {
    public static final Logger LOGGER = LogManager.getLogger("DraconicEvolution");
    public static final String MODID = "draconicevolution";
    public static final String MODNAME = "Draconic Evolution";

    public static CommonProxy proxy;

//    The Plan.
//     - Poke covers about VBO render type.....
//     - Look into getting the reactor shader working
//     - Work on converting to the new fucky model format for stuff and things
//     - Build the new energy core GUI or at least a base that i can work with when i actually redo the energy core
//     - Fix up the reactor GUI and get the reactor working.
//     - crystal shaders

    public DraconicEvolution() {
        proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        proxy.construct();
        FMLJavaModLoadingContext.get().getModEventBus().register(this);

        DraconicAPI.FUSION_RECIPE_TYPE = new RecipeType<>() {
            @Override
            public String toString() {
                return MODID + ":fusion_crafting";
            }
        };

        CraftingHelper.register(DraconicAPI.INGREDIENT_STACK_TYPE, IngredientStack.SERIALIZER);
    }

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        proxy.commonSetup(event);
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        proxy.clientSetup(event);
    }

    @SubscribeEvent
    public void onServerSetup(FMLDedicatedServerSetupEvent event) {
        proxy.serverSetup(event);
    }

    public static void registerCommands(RegisterCommandsEvent event) {
        CommandKaboom.register(event.getDispatcher());
        CommandMakeRecipe.register(event.getDispatcher());
        CommandRespawnGuardian.register(event.getDispatcher());
    }
}