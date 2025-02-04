package com.brandon3055.draconicevolution.inventory;

import com.brandon3055.draconicevolution.blocks.tileentity.TileEntityDetector;
import com.brandon3055.draconicevolution.blocks.tileentity.flowgate.TileFlowGate;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

/**
 * Created by brandon3055 on 07/02/2024
 */
public class EntityDetectorMenu extends DETileMenu<TileEntityDetector> {

    public EntityDetectorMenu(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(windowId, playerInv, getClientTile(playerInv, extraData));
    }

    public EntityDetectorMenu(int windowId, Inventory playerInv, TileEntityDetector tile) {
        super(DEContent.MENU_ENTITY_DETECTOR.get(), windowId, playerInv, tile);
    }
}
