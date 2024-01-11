package shordinger.wrapper.net.minecraft.client.renderer.block.statemap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.client.renderer.block.model.ModelResourceLocation;

@SideOnly(Side.CLIENT)
public class DefaultStateMapper extends StateMapperBase {

    protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
        return new ModelResourceLocation(
            Block.REGISTRY.getNameForObject(state.getBlock()),
            this.getPropertyString(state.getProperties()));
    }
}
