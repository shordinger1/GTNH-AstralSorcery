package shordinger.wrapper.net.minecraft.block.state;

import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

public interface IBlockBehaviors {

    /**
     * Called on both Client and Server when World#addBlockEvent is called. On the Server, this may perform additional
     * changes to the world, like pistons replacing the block with an extended base. On the client, the update may
     * involve replacing tile entities, playing sounds, or performing other visual actions to reflect the server side
     * changes.
     */
    boolean onBlockEventReceived(World worldIn, BlockPos pos, int id, int param);

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     *
     * @param blockIn The neighboring block causing this block update
     * @param fromPos The neighboring position causing this block update
     */
    void neighborChanged(World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos);
}
