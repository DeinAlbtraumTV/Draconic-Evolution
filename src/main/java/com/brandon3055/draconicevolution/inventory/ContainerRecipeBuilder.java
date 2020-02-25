package com.brandon3055.draconicevolution.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brandon3055 on 21/07/2016.
 */
public class ContainerRecipeBuilder extends Container {

    private PlayerEntity player;
    public InventoryCache inventoryCache = new InventoryCache(20);
    private List<Slot> craftingSlots = new LinkedList<>();

    public ContainerRecipeBuilder(@Nullable ContainerType<?> type, int id, PlayerEntity player) {
        super(type, id);
        this.player = player;
    }

//    public ContainerRecipeBuilder(PlayerEntity player) {
//        this.player = player;
//        addSlots();
//        arangeCraftingSlots(0);
//    }

    public void addSlots() {
        int posX = 20;
        int posY = 145;
        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(player.inventory, x, posX + 18 * x, posY + 58));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(player.inventory, x + y * 9 + 9, posX + 18 * x, posY + y * 18));
            }
        }

        for (int i = 0; i < inventoryCache.getSizeInventory(); i++) {
            Slot slot = new Slot(inventoryCache, i, 1000, 1000);
            addSlot(slot);
            craftingSlots.add(slot);
        }
    }

    public void arangeCraftingSlots(int craftingType) {
        for (Slot slot : craftingSlots) {
            slot.xPos = slot.yPos = 1000;
        }

        if (craftingType == 0) {
            int posX = 20;
            int posY = 30;
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    craftingSlots.get(x + y * 3 + 1).xPos = posX + 18 * x;
                    craftingSlots.get(x + y * 3 + 1).yPos = posY + y * 18;
                }
            }

            craftingSlots.get(0).xPos = 107;
            craftingSlots.get(0).yPos = posY + 18;
        }
        else if (craftingType == 1) {
            craftingSlots.get(0).xPos = 90 - 18;
            craftingSlots.get(0).yPos = 20;
            craftingSlots.get(1).xPos = 90 + 18;
            craftingSlots.get(1).yPos = 20;

            for (int i = 2; i < 11; i++) {
                craftingSlots.get(i).xPos = 20 + (i - 2) * 18;
                craftingSlots.get(i).yPos = 50;

                craftingSlots.get(i + 9).xPos = 20 + (i - 2) * 18;
                craftingSlots.get(i + 9).yPos = 68;
            }
        }
    }

    @Nullable
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        Slot slot = getSlot(index);

        if (slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();

            if (index >= 36) {
                if (!mergeItemStack(stack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (ForgeHooks.getBurnTime(stack) == 0 || !mergeItemStack(stack, 36, 36 + inventoryCache.getSizeInventory(), false)) {
                return ItemStack.EMPTY;
            }

            if (stack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            }
            else {
                slot.onSlotChanged();
            }

            slot.onTake(player, stack);

            return result;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

}
