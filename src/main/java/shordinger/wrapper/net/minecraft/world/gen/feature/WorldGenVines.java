package shordinger.wrapper.net.minecraft.world.gen.feature;

import java.util.Random;

import shordinger.wrapper.net.minecraft.block.BlockVine;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

public class WorldGenVines extends WorldGenerator {

    public boolean generate(World worldIn, Random rand, BlockPos position) {
        for (; position.getY() < 128; position = position.up()) {
            if (worldIn.isAirBlock(position)) {
                for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL.facings()) {
                    if (Blocks.VINE.canPlaceBlockOnSide(worldIn, position, enumfacing)) {
                        IBlockState iblockstate = Blocks.VINE.getDefaultState()
                            .withProperty(BlockVine.NORTH, Boolean.valueOf(enumfacing == EnumFacing.NORTH))
                            .withProperty(BlockVine.EAST, Boolean.valueOf(enumfacing == EnumFacing.EAST))
                            .withProperty(BlockVine.SOUTH, Boolean.valueOf(enumfacing == EnumFacing.SOUTH))
                            .withProperty(BlockVine.WEST, Boolean.valueOf(enumfacing == EnumFacing.WEST));
                        worldIn.setBlockState(position, iblockstate, 2);
                        break;
                    }
                }
            } else {
                position = position.add(rand.nextInt(4) - rand.nextInt(4), 0, rand.nextInt(4) - rand.nextInt(4));
            }
        }

        return true;
    }
}
