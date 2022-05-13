package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.gui.modulargui.GuiElementManager;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.draconicevolution.inventory.ContainerDissEnchanter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

/**
 * Created by brandon3055 on 1/10/2016.
 */
public class GuiDissEnchanter extends ModularGuiContainer<ContainerDissEnchanter> {
    private final Player player;
//    private MGuiBackground background;
//    private MGuiButton extractButton;
//    private MGuiSelectDialog selector;


    public GuiDissEnchanter(ContainerDissEnchanter container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
//        this.tile = container.tile;
        this.player = playerInventory.player;
        imageWidth = 175;
        imageHeight = 142 + 70;
    }

    @Override
    public void addElements(GuiElementManager manager) {

    }
//
//    @Override
//    public void initGui() {
//        super.initGui();
//        manager.clear();
//
//        manager.add(background = new MGuiBackground(this, guiLeft, guiTop, 0, 0, xSize, ySize, "draconicevolution:textures/gui/diss_enchanter.png"));
//        manager.add(extractButton = new MGuiButton(this, 0, guiLeft + (xSize / 2) - 30, guiTop + 42, 60, 15, I18n.format("gui.de.button.extract")));
//        manager.add(new MGuiLabel(this, guiLeft + (xSize / 2), guiTop + 3, 0, 12, I18n.format("gui.de.dissEnchanter.name")).setTextColour(0x00FFFF));
//    }
//
//    @Override
//    public void onMGuiEvent(String eventString, MGuiElementBase eventElement) {
//        TileDissEnchanter tile = container.tile;
//        ItemStack slot0 = tile.getStackInSlot(0);
//
//        if (eventElement == extractButton) {
//            if (slot0 == null || !slot0.isItemEnchanted() || slot0.getEnchantmentTagList() == null) {
//                return;
//            }
//
//            ListNBT list = slot0.getEnchantmentTagList();
//
//            if (selector != null) {
//                manager.remove(selector);
//                selector = null;
//                return;
//            }
//
//            selector = new MGuiSelectDialog(this, eventElement.xPos, guiTop + 142);
//            selector.ySize = 2;
//            List<MGuiElementBase> optionList = new ArrayList<>();
//
//            int width = 0;
//            for (int i = 0; i < list.tagCount(); i++) {
//                CompoundNBT compound = list.getCompoundTagAt(i);
//                int id = compound.getShort("id");
//                int lvl = compound.getShort("lvl");
//                Enchantment e = Enchantment.getEnchantmentByID(id);
//
//                if (e == null) {
//                    continue;
//                }
//
//                String s = e.getTranslatedName(lvl);
//
//                int w = fontRenderer.getStringWidth(s) + 4 + 20;
//                if (width < w) {
//                    width = w;
//                }
//            }
//
//            for (int i = 0; i < list.tagCount(); i++) {
//                CompoundNBT compound = list.getCompoundTagAt(i);
//                int id = compound.getShort("id");
//                int lvl = compound.getShort("lvl");
//                Enchantment e = Enchantment.getEnchantmentByID(id);
//
//
//                if (e == null) {
//                    continue;
//                }
//
//                String s = e.getTranslatedName(lvl);
//                int cost = (int) ((((double) lvl / (double) e.getMaxLevel()) * 20) * DEConfig.disenchnaterCostMultiplyer);
//                String xp = (player.experienceLevel >= cost ? TextFormatting.GREEN : TextFormatting.RED) + "" + cost;
//
//                MGuiLabel label = new MGuiLabel(this, 0, 0, width, 12, xp).setAlignment(EnumAlignment.RIGHT);
//                label.linkedObject = id;
//                label.addChild(new MGuiButtonSolid(this, "PICK", 0, 0, fontRenderer.getStringWidth(s) + 4 + 15, 12, s).setColours(0xFF707070, 0xFF707070, 0xFF707070).setAlignment(EnumAlignment.LEFT));
//
//                MGuiButtonSolid option = (MGuiButtonSolid) new MGuiButtonSolid(this, "PICK", 0, 0, fontRenderer.getStringWidth(s) + 4 + 15, 12, s).setColours(0xFF707070, 0xFF707070, 0xFF707070).setAlignment(EnumAlignment.LEFT);
//                option.addChild(new MGuiLabel(this, 0, 0, option.xSize, option.ySize, "Test").setAlignment(EnumAlignment.RIGHT));
//                selector.ySize += label.ySize;
//                optionList.add(label);
//            }
//
//            if (selector.ySize > 70) {
//                selector.ySize = 70;
//            }
//
//            selector.initElement();
//            selector.setOptions(optionList);
//            selector.setListener(this);
//            selector.xPos = guiLeft + (xSize / 2) - (selector.xSize / 2);
//            selector.initElement();
//            manager.add(selector, 0);
//
//            return;
//        }
//        else if (eventString.equals("SELECTOR_PICK")) {
//            if (!(eventElement.linkedObject instanceof Integer)) {
//                player.sendMessage(new StringTextComponent("[ERROR]").setStyle(new Style().setColor(TextFormatting.RED)));
//                return;
//            }
//
//            tile.sendPacketToServer(output -> output.writeInt((Integer) eventElement.linkedObject), 0);
//        }
//    }
//
//    @Override
//    public void updateScreen() {
//        TileDissEnchanter tile = container.tile;
//        ItemStack slot0 = tile.getStackInSlot(0);
//        ItemStack slot1 = tile.getStackInSlot(1);
//        ItemStack slot2 = tile.getStackInSlot(2);
//        extractButton.disabled = !(!slot0.isEmpty() && slot0.isItemEnchanted() && !slot1.isEmpty() && slot1.getCount() > 0 && slot1.getItem() == Items.BOOK && slot2.isEmpty());
//
//        if (extractButton.disabled && selector != null) {
//            manager.remove(selector);
//            selector = null;
//        }
//
//        super.updateScreen();
//    }
}
