package shordinger.wrapper.net.minecraft.dispenser;

import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;

public interface IBlockSource extends ILocatableSource {

    double getX();

    double getY();

    double getZ();

    BlockPos getBlockPos();

    /**
     * Gets the block state of this position and returns it.
     *
     * @return Block state in this position
     */
    IBlockState getBlockState();

    <T extends TileEntity> T getBlockTileEntity();
}
