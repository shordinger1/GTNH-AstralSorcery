package shordinger.wrapper.net.minecraft.block;

import shordinger.wrapper.net.minecraft.block.properties.IProperty;
import shordinger.wrapper.net.minecraft.block.properties.PropertyInteger;
import shordinger.wrapper.net.minecraft.block.state.BlockStateContainer;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.util.math.AxisAlignedBB;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.IBlockAccess;
import shordinger.wrapper.net.minecraft.world.World;

import java.util.Random;

public class BlockBeetroot extends BlockCrops {

    public static final PropertyInteger BEETROOT_AGE = PropertyInteger.create("age", 0, 3);
    private static final AxisAlignedBB[] BEETROOT_AABB = new AxisAlignedBB[]{
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D)};

    protected PropertyInteger getAgeProperty() {
        return BEETROOT_AGE;
    }

    public int getMaxAge() {
        return 3;
    }

    protected Item getSeed() {
        return Items.BEETROOT_SEEDS;
    }

    protected Item getCrop() {
        return Items.BEETROOT;
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (rand.nextInt(3) == 0) {
            this.checkAndDropBlock(worldIn, pos, state);
        } else {
            super.updateTick(worldIn, pos, state, rand);
        }
    }

    protected int getBonemealAgeIncrease(World worldIn) {
        return super.getBonemealAgeIncrease(worldIn) / 3;
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{BEETROOT_AGE});
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BEETROOT_AABB[((Integer) state.getValue(this.getAgeProperty())).intValue()];
    }
}
