package shordinger.wrapper.net.minecraft.world.gen.feature;

import java.util.Random;

import shordinger.wrapper.net.minecraft.block.BlockPumpkin;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

public class WorldGenPumpkin extends WorldGenerator {

    public boolean generate(World worldIn, Random rand, BlockPos position) {
        for (int i = 0; i < 64; ++i) {
            BlockPos blockpos = position.add(
                rand.nextInt(8) - rand.nextInt(8),
                rand.nextInt(4) - rand.nextInt(4),
                rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(blockpos) && worldIn.getBlockState(blockpos.down())
                .getBlock() == Blocks.GRASS && Blocks.PUMPKIN.canPlaceBlockAt(worldIn, blockpos)) {
                worldIn.setBlockState(
                    blockpos,
                    Blocks.PUMPKIN.getDefaultState()
                        .withProperty(BlockPumpkin.FACING, EnumFacing.Plane.HORIZONTAL.random(rand)),
                    2);
            }
        }

        return true;
    }
}
