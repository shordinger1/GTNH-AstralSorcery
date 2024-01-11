package shordinger.wrapper.net.minecraft.world.gen.feature;

import java.util.Random;

import shordinger.wrapper.net.minecraft.block.BlockDoublePlant;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

public class WorldGenDoublePlant extends WorldGenerator {

    private BlockDoublePlant.EnumPlantType plantType;

    public void setPlantType(BlockDoublePlant.EnumPlantType plantTypeIn) {
        this.plantType = plantTypeIn;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position) {
        boolean flag = false;

        for (int i = 0; i < 64; ++i) {
            BlockPos blockpos = position.add(
                rand.nextInt(8) - rand.nextInt(8),
                rand.nextInt(4) - rand.nextInt(4),
                rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(blockpos) && (!worldIn.provider.isNether() || blockpos.getY() < 254)
                && Blocks.DOUBLE_PLANT.canPlaceBlockAt(worldIn, blockpos)) {
                Blocks.DOUBLE_PLANT.placeAt(worldIn, blockpos, this.plantType, 2);
                flag = true;
            }
        }

        return flag;
    }
}
