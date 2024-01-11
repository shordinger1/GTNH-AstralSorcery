package shordinger.wrapper.net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.renderer.block.model.ModelResourceLocation;
import shordinger.wrapper.net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public interface ItemMeshDefinition {

    ModelResourceLocation getModelLocation(ItemStack stack);
}
