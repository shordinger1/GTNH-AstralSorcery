package shordinger.wrapper.net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public class ChestRenderer {

    public void renderChestBrightness(Block blockIn, float color) {
        GlStateManager.color(color, color, color, 1.0F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        ItemStack stack = new ItemStack(blockIn);
        stack.getItem()
            .getTileEntityItemStackRenderer()
            .renderByItem(stack);
    }
}
