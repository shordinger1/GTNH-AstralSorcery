package shordinger.astralsorcery.migration;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class WorldHelper {

    public static IBlockState getBlockState(World world, BlockPos pos) {
        if (isOutsideBuildHeight(world, pos)) {
            return Blocks.air.getDefaultState();
        } else {
            Chunk chunk = world.getChunkFromBlockCoords(pos.getX(), pos.getZ());
            return chunk.getBlockState(pos);
        }
    }

    public static boolean isOutsideBuildHeight(World world, BlockPos pos) {
        return pos.getY() < 0 || pos.getY() >= 256;
    }
}
