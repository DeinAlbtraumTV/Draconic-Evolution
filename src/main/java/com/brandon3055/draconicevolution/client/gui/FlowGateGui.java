package com.brandon3055.draconicevolution.client.gui;

import codechicken.lib.gui.modular.ModularGui;
import codechicken.lib.gui.modular.ModularGuiContainer;
import codechicken.lib.gui.modular.elements.GuiElement;
import codechicken.lib.gui.modular.elements.GuiManipulable;
import codechicken.lib.gui.modular.elements.GuiTexture;
import codechicken.lib.gui.modular.lib.Constraints;
import codechicken.lib.gui.modular.lib.container.ContainerGuiProvider;
import codechicken.lib.gui.modular.lib.container.ContainerScreenAccess;
import codechicken.lib.gui.modular.lib.geometry.Direction;
import com.brandon3055.brandonscore.client.gui.GuiToolkit;
import com.brandon3055.brandonscore.client.gui.modulargui.templates.ButtonRow;
import com.brandon3055.draconicevolution.client.DEGuiTextures;
import com.brandon3055.draconicevolution.inventory.ContainerReactor;
import com.brandon3055.draconicevolution.inventory.FlowGateMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Created by brandon3055 on 15/11/2016.
 */
public class FlowGateGui extends ContainerGuiProvider<FlowGateMenu> {
    private static final GuiToolkit TOOLKIT = new GuiToolkit("gui.draconicevolution.flow_gate");
    public static final int GUI_WIDTH = 176;
    public static final int GUI_HEIGHT = 166;

    @Override
    public GuiElement<?> createRootElement(ModularGui gui) {
        GuiManipulable root = new GuiManipulable(gui).addMoveHandle(3).enableCursors(true);
        GuiTexture bg = new GuiTexture(root.getContentElement(), DEGuiTextures.themedGetter("flow_gate"));
        Constraints.bind(bg, root.getContentElement());
        return root;
    }

    @Override
    public void buildGui(ModularGui gui, ContainerScreenAccess<FlowGateMenu> screenAccess) {
        gui.initStandardGui(GUI_WIDTH, GUI_HEIGHT);
        FlowGateMenu menu = screenAccess.getMenu();
//		TileDisenchanter tile = menu.tile;
        GuiElement<?> root = gui.getRoot();
        TOOLKIT.createHeading(root, gui.getGuiTitle(), true);

        ButtonRow buttonRow = ButtonRow.topRightInside(root, Direction.DOWN, 3, 3).setSpacing(1);
        buttonRow.addButton(TOOLKIT::createThemeButton);
    }

//    private GuiToolkit<GuiFlowGate> toolkit = new GuiToolkit<>(this, GuiToolkit.GuiLayout.DEFAULT).setTranslationPrefix("gui.draconicevolution.flow_gate");
//    private TileFlowGate tile;
//    private long ltMin = -1;
//    private long ltMax = -1;
//
//    public GuiFlowGate(ContainerBCTile<TileFlowGate> container, Inventory inv, Component title) {
//        super(container, inv, title);
//        this.tile = container.tile;
//    }
//
//    @Override
//    public void addElements(GuiElementManager manager) {
//        TBasicMachine template = toolkit.loadTemplate(new TModularMachine(this, tile));
//
//        template.background.addChild(new GuiLabel(toolkit.i18n("overridden")))
//                .setHoverText(toolkit.i18n("overridden.info"))
//                .setSize(60, 8)
//                .setTextColour(0xff0000)
//                .setShadow(false)
//                .setEnabledCallback(() -> tile.flowOverridden.get())
//                .setPos(guiLeft() + 4, guiTop() + 4);
//
//        GuiLabel highLabel = template.background.addChild(new GuiLabel(toolkit.i18n("redstone_high")))
//                .setSize(xSize(), 8)
//                .setTextColour(0xff0000)
//                .setShadow(false)
//                .setPos(template.playerSlots.xPos(), template.title.maxYPos() + 4);
//
//        GuiTextField highField = toolkit.createTextField(template.background)
//                .setHoverText(toolkit.i18n("redstone_high.info"))
//                .setFilter(toolkit.catchyValidator(s -> s.equals("") || Long.parseLong(s) >= 0))
//                .setSize(template.playerSlots.xSize() - 60, 14)
//                .setPos(highLabel.xPos(), highLabel.maxYPos() + 2);
//
//        toolkit.createButton_old(toolkit.i18n("apply"), template.background)
//                .setPos(highField.maxXPos() + 1, highField.yPos())
//                .setYSize(highField.ySize())
//                .setMaxXPos(template.playerSlots.maxXPos(), true)
//                .onPressed(() -> tile.setMax(highField.getValue()));
//
//        GuiLabel lowLabel = template.background.addChild(new GuiLabel(toolkit.i18n("redstone_low")))
//                .setSize(xSize(), 8)
//                .setTextColour(0x990000)
//                .setShadow(false)
//                .setPos(highField.xPos(), highField.maxYPos() + 3);
//
//        GuiTextField lowField = toolkit.createTextField(template.background)
//                .setHoverText(toolkit.i18n("redstone_low.info"))
//                .setFilter(toolkit.catchyValidator(s -> s.equals("") || Long.parseLong(s) >= 0))
//                .setSize(template.playerSlots.xSize() - 60, 14)
//                .setPos(lowLabel.xPos(), lowLabel.maxYPos() + 2);
//
//        toolkit.createButton_old(toolkit.i18n("apply"), template.background)
//                .setPos(lowField.maxXPos() + 1, lowField.yPos())
//                .setYSize(lowField.ySize())
//                .setMaxXPos(template.playerSlots.maxXPos(), true)
//                .onPressed(() -> tile.setMin(lowField.getValue()));
//
//        template.background.addChild(new GuiLabel())
//                .setHoverText(toolkit.i18n("flow.info"))
//                .setTrim(false)
//                .setShadow(false)
//                .setTextColGetter(GuiToolkit.Palette.Slot::text)
//                .setSize(xSize(), 8)
//                .setYPos(lowField.maxYPos() + 5)
//                .setMaxXPos(template.playerSlots.maxXPos(), false)
//                .setDisplaySupplier(() -> toolkit.i18n("flow") + ": " + (tile.getFlow() > 999999 ? Utils.formatNumber(tile.getFlow()) : Utils.addCommas(tile.getFlow())) + tile.getUnits())
//                .setAlignment(GuiAlign.RIGHT);
//
//        manager.onTick(() -> {
//            if (tile.minFlow.get() != ltMin) {
//                ltMin = tile.minFlow.get();
//                lowField.setValue(String.valueOf(ltMin));
//            }
//            if (tile.maxFlow.get() != ltMax) {
//                ltMax = tile.maxFlow.get();
//                highField.setValue(String.valueOf(ltMax));
//            }
//            if (!highField.isFocused() && highField.getValue().equals("")) {
//                highField.setValue("0");
//            }
//            if (!lowField.isFocused() && lowField.getValue().equals("")) {
//                lowField.setValue("0");
//            }
//        });
//    }

    public static class Screen extends ModularGuiContainer<FlowGateMenu> {
        public Screen(FlowGateMenu menu, Inventory inv, Component title) {
            super(menu, inv, new FlowGateGui());
            getModularGui().setGuiTitle(title);
        }
    }
}
