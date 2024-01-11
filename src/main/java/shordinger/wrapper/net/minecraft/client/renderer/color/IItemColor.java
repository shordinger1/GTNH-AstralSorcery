package shordinger.wrapper.net.minecraft.client.renderer.color;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public interface IItemColor {

    int colorMultiplier(ItemStack stack, int tintIndex);
}
