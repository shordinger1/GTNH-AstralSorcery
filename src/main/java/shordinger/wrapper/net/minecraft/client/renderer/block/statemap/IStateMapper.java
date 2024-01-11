package shordinger.wrapper.net.minecraft.client.renderer.block.statemap;

import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.client.renderer.block.model.ModelResourceLocation;

@SideOnly(Side.CLIENT)
public interface IStateMapper {

    Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block blockIn);
}
