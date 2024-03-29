package shordinger.wrapper.net.minecraft.world.gen.feature;

import java.util.Random;

import shordinger.wrapper.net.minecraft.block.BlockBush;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

public class WorldGenBush extends WorldGenerator {

    private final BlockBush block;

    public WorldGenBush(BlockBush blockIn) {
        this.block = blockIn;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position) {
        for (int i = 0; i < 64; ++i) {
            BlockPos blockpos = position.add(
                rand.nextInt(8) - rand.nextInt(8),
                rand.nextInt(4) - rand.nextInt(4),
                rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(blockpos)
                && (!worldIn.provider.isNether() || blockpos.getY() < worldIn.getHeight() - 1)
                && this.block.canBlockStay(worldIn, blockpos, this.block.getDefaultState())) {
                worldIn.setBlockState(blockpos, this.block.getDefaultState(), 2);
            }
        }

        return true;
    }
}
