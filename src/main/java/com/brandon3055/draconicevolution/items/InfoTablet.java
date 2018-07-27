package com.brandon3055.draconicevolution.items;

import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.draconicevolution.client.gui.modwiki.GuiModWiki;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 22/09/2016.
 */
public class InfoTablet extends ItemBCore {

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if (worldIn.isRemote) {
//            if (PiAPI.isAPIAvalible()) {
//                PiAPI.openModPage(DraconicEvolution.MODID);
//            }
//            else {
                openGui();
//            }
        }
        return super.onItemRightClick(worldIn, playerIn, hand);
    }

    @SideOnly(Side.CLIENT)
    private void openGui() {
        Minecraft.getMinecraft().displayGuiScreen(new GuiModWiki());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, world, tooltip, advanced);
    }
}
