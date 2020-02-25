package com.brandon3055.draconicevolution.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 31/05/2016.
 * Can be implemented by IEnergyContainerItems to control weather or not they can be charged by a DE Capacitor.
 * Not sure if i will be keeping this so if anyone uses it please poke or pm me and i will leave it in.
 */
public interface IInvCharge {
    boolean canCharge(ItemStack stack, PlayerEntity player);
}
