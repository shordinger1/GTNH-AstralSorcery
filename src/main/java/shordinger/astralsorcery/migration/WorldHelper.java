package shordinger.astralsorcery.migration;

import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Blocks;
import net.minecraft.util.ReportedException;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.IBlockState;

public class WorldHelper {

    public static IBlockState getBlockState(World world, BlockPos pos) {
        if (isOutsideBuildHeight(world, pos)) {
            return Blocks.air.getDefaultState();
        } else {
            Chunk chunk = world.getChunkFromBlockCoords(pos.getX(), pos.getZ());
            return chunk.getBlockState(pos);
        }
    }

    public static IBlockState getBlockState(IBlockAccess world, BlockPos pos) {
        World world1 = (World) world;
        if (isOutsideBuildHeight(world1, pos)) {
            return Blocks.air.getDefaultState();
        } else {
            Chunk chunk = world.getChunkFromBlockCoords(pos.getX(), pos.getZ());
            return getBlockState(chunk, pos);
        }
    }

    public static boolean isOutsideBuildHeight(World world, BlockPos pos) {
        return pos.getY() < 0 || pos.getY() >= 256;
    }

    public IBlockState getDefaultBlockState(Block block) {

    }


    public static IBlockState getBlockState(Chunk chunk, BlockPos pos) {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
//        if (chunk.worldObj.getWorldInfo().getTerrainType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
//            IBlockState iblockstate = null;
//            if (y == 60) {
//                iblockstate = Blocks.BARRIER.getDefaultState();
//            }
//
//            if (y == 70) {
//                iblockstate = ChunkGeneratorDebug.getBlockStateFor(x, z);
//            }
//
//            return iblockstate == null ? Blocks.AIR.getDefaultState() : iblockstate;
//        } else {
        try {
            if (y >= 0 && y >> 4 < chunk.storageArrays.length) {
                ExtendedBlockStorage extendedblockstorage = this.storageArrays[y >> 4];
                if (extendedblockstorage != NULL_BLOCK_STORAGE) {
                    return extendedblockstorage.get(x & 15, y & 15, z & 15);
                }
            }

            return Blocks.air.getDefaultState();
        } catch (Throwable var7) {
            CrashReport crashreport = CrashReport.makeCrashReport(var7, "Getting block state");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being got");
            crashreportcategory.addDetail("Location", new ICrashReportDetail<String>() {

                public String call() throws Exception {
                    return CrashReportCategory.getCoordinateInfo(x, y, z);
                }
            });
            throw new ReportedException(crashreport);
        }
        //}
    }
}
