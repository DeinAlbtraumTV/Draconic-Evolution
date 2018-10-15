package com.brandon3055.draconicevolution.integration.jei;

import com.brandon3055.brandonscore.client.gui.effects.GuiEffectRenderer;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import com.brandon3055.draconicevolution.client.gui.GuiFusionCraftingCore;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brandon3055 on 24/07/2016.
 */
public class FusionRecipeWrapper extends BlankRecipeWrapper {

    private GuiEffectRenderer effectRenderer = new GuiEffectRenderer();
    public final IFusionRecipe recipe;
    private final List inputs = new LinkedList();
    private int timeout = 0;
    private int xSize = 164;
    private int ySize = 111;
    private int lastTick = 0;
    private ITickTimer timer;

    @SuppressWarnings("unchecked")
    public FusionRecipeWrapper(IFusionRecipe recipe) {
        this.recipe = recipe;
        inputs.add(recipe.getRecipeCatalyst());
        for (Object o : recipe.getRecipeIngredients()) {
            if (o instanceof Item) {
                inputs.add(new ItemStack((Item) o));
            }
            else if (o instanceof Block) {
                inputs.add(new ItemStack((Block) o));
            }
            else {
                inputs.add(o);
            }
        }

        timer = DEJEIPlugin.jeiHelpers.getGuiHelper().createTickTimer(1000000, 1000000, false);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<List<ItemStack>> list = DEJEIPlugin.jeiHelpers.getStackHelper().expandRecipeItemStackInputs(inputs);
        ingredients.setInputLists(ItemStack.class, list);
        ingredients.setOutput(ItemStack.class, recipe.getRecipeOutput(ItemStack.EMPTY));
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        timeout = 0;

        if (minecraft.fontRenderer != null) {
            int tier = recipe.getRecipeTier();
            int colour = tier == 0 ? 0x5050FF : tier == 1 ? 0x8000FF : tier == 2 ? 0xFF6600 : 0x505050;
            GuiHelper.drawCenteredString(minecraft.fontRenderer, I18n.format("gui.jeiFusion.tier." + recipe.getRecipeTier()), xSize / 2, 5, colour, false);
            GuiHelper.drawCenteredString(minecraft.fontRenderer, I18n.format("generic.de.energyCost.txt"), xSize / 2, ySize - 20, 0x4444FF, false);
            GuiHelper.drawCenteredString(minecraft.fontRenderer, Utils.addCommas((long) recipe.getIngredientEnergyCost() * recipe.getRecipeIngredients().size()) + "RF", xSize / 2, ySize - 10, 0x44AAFF, false);
        }

        drawAnimations(minecraft, recipeWidth, recipeHeight);
    }

    //TODO check if this is valid
    public void drawAnimations(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight) {
        if (ClientEventHandler.elapsedTicks != lastTick) {
            lastTick = ClientEventHandler.elapsedTicks;
            tick();
        }

        effectRenderer.renderEffects(minecraft.getRenderPartialTicks());

        GuiHelper.drawBorderedRect((xSize / 2) - 10, 22, 20, 66, 1, 0x40FFFFFF, 0xFF00FFFF);

        if (recipe.getRecipeIngredients().size() > 16) {
            GuiHelper.drawBorderedRect(1, 6, 18, 100, 1, 0x40FFFFFF, 0xFFAA00FF);
            GuiHelper.drawBorderedRect(18, 6, 18, 100, 1, 0x40FFFFFF, 0xFFAA00FF);
            GuiHelper.drawBorderedRect(xSize - 20, 6, 18, 100, 1, 0x40FFFFFF, 0xFFAA00FF);
            GuiHelper.drawBorderedRect(xSize - 37, 6, 18, 100, 1, 0x40FFFFFF, 0xFFAA00FF);
        }
        else {
            GuiHelper.drawBorderedRect(8, 6, 20, 100, 1, 0x40FFFFFF, 0xFFAA00FF);
            GuiHelper.drawBorderedRect(xSize - 28, 6, 20, 100, 1, 0x40FFFFFF, 0xFFAA00FF);
        }
    }

    public void tick() {
        effectRenderer.updateEffects();
        World world = Minecraft.getMinecraft().world;

        if (world != null) {
            List ingredients = recipe.getRecipeIngredients();

            int centerX = xSize / 2;
            int centerY = ySize / 2;

            for (int i = 0; i < ingredients.size(); i++) {
                boolean isLeft = i % 2 == 0;
                boolean isOdd = ingredients.size() % 2 == 1;
                int sideCount = ingredients.size() / 2;

                if (isOdd && !isLeft) {
                    sideCount--;
                }

                int xPos;
                int yPos;


                if (isLeft) {
                    xPos = centerX - 65;
                    int ySize = 80 / Math.max(sideCount - (isOdd ? 0 : 1), 1);

                    int sideIndex = i / 2;

                    if (sideCount <= 1 && (!isOdd || ingredients.size() == 1)) {
                        sideIndex = 1;
                        ySize = 40;
                    }

                    yPos = centerY - 40 + (sideIndex * ySize);
                }
                else {
                    xPos = centerX + 65;

                    int ySize = 80 / Math.max(sideCount - (isOdd ? 0 : 1), 1);

                    int sideIndex = i / 2;

                    if (isOdd) {
                        sideCount++;
                    }

                    if (sideCount <= 1) {
                        sideIndex = 1;
                        ySize = 40;
                    }

                    yPos = centerY - 40 + (sideIndex * ySize);
                }

                if (world.rand.nextInt(10) == 0) {
                    xPos += -8 + (world.rand.nextDouble() * 16);
                    yPos += -8 + (world.rand.nextDouble() * 16);
                    double ty = centerY + (-20 + (world.rand.nextDouble() * 40));
                    effectRenderer.addEffect(new GuiFusionCraftingCore.EnergyEffect(Minecraft.getMinecraft().world, xPos, yPos, centerX, ty, 0));
                }
            }

            double xPos = centerX - 8 + (world.rand.nextDouble() * 16);
            double yTop = 35 - 8 + (world.rand.nextDouble() * 16);//35
            effectRenderer.addEffect(new GuiFusionCraftingCore.EnergyEffect(Minecraft.getMinecraft().world, xPos, yTop, centerX, 78, 1));//78

            effectRenderer.updateEffects();
        }
        else {
            effectRenderer.clearEffects();
        }
    }

    public static class Factory implements IRecipeWrapperFactory<IFusionRecipe> {

        @Override
        public IRecipeWrapper getRecipeWrapper(IFusionRecipe recipe) {
            return new FusionRecipeWrapper(recipe);
        }
    }
}
