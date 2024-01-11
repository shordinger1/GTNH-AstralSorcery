package shordinger.wrapper.net.minecraft.world;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.biome.Biome;

public interface IBlockAccess extends net.minecraft.world.IBlockAccess {

    @Nullable
    TileEntity getTileEntity(BlockPos pos);

    @SideOnly(Side.CLIENT)
    int getCombinedLight(BlockPos pos, int lightValue);

    IBlockState getBlockState(BlockPos pos);

    /**
     * Checks to see if an air block exists at the provided location. Note that this only checks to see if the blocks
     * material is set to air, meaning it is possible for non-vanilla blocks to still pass this check.
     */
    boolean isAirBlock(BlockPos pos);

    @SideOnly(Side.CLIENT)
    Biome getBiome(BlockPos pos);

    int getStrongPower(BlockPos pos, EnumFacing direction);

    @SideOnly(Side.CLIENT)
    WorldType getWorldType();

    /**
     * FORGE: isSideSolid, pulled up from {@link World}
     *
     * @param pos      Position
     * @param side     Side
     * @param _default default return value
     * @return if the block is solid on the side
     */
    boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default);
}
