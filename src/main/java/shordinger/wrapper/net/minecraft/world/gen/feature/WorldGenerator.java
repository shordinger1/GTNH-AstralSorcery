package shordinger.wrapper.net.minecraft.world.gen.feature;

import java.util.Random;

import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

public abstract class WorldGenerator {

    /**
     * Sets wither or not the generator should notify blocks of blocks it changes. When the world is first generated,
     * this is false, when saplings grow, this is true.
     */
    private final boolean doBlockNotify;

    public WorldGenerator() {
        this(false);
    }

    public WorldGenerator(boolean notify) {
        this.doBlockNotify = notify;
    }

    public abstract boolean generate(World worldIn, Random rand, BlockPos position);

    public void setDecorationDefaults() {
    }

    protected void setBlockAndNotifyAdequately(World worldIn, BlockPos pos, IBlockState state) {
        if (this.doBlockNotify) {
            worldIn.setBlockState(pos, state, 3);
        } else {
            int flag = net.minecraftforge.common.ForgeModContainer.fixVanillaCascading ? 2 | 16 : 2; // Forge: With bit
            // 5 unset, it will
            // notify neighbors
            // and load
            // adjacent chunks.
            worldIn.setBlockState(pos, state, flag);
        }
    }
}
