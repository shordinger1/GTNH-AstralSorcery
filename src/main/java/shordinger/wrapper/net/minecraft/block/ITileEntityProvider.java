package shordinger.wrapper.net.minecraft.block;

import javax.annotation.Nullable;

import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.world.World;

public interface ITileEntityProvider {

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    @Nullable
    TileEntity createNewTileEntity(World worldIn, int meta);
}
