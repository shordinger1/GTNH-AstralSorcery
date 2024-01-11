package shordinger.wrapper.net.minecraft.block;

import shordinger.wrapper.net.minecraft.block.material.MapColor;
import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.properties.IProperty;
import shordinger.wrapper.net.minecraft.block.properties.PropertyInteger;
import shordinger.wrapper.net.minecraft.block.state.BlockStateContainer;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.util.SoundCategory;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.world.World;

public class BlockPressurePlateWeighted extends BlockBasePressurePlate {

    public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);
    private final int maxWeight;

    protected BlockPressurePlateWeighted(Material materialIn, int p_i46379_2_) {
        this(materialIn, p_i46379_2_, materialIn.getMaterialMapColor());
    }

    protected BlockPressurePlateWeighted(Material materialIn, int p_i46380_2_, MapColor color) {
        super(materialIn, color);
        this.setDefaultState(
            this.blockState.getBaseState()
                .withProperty(POWER, Integer.valueOf(0)));
        this.maxWeight = p_i46380_2_;
    }

    protected int computeRedstoneStrength(World worldIn, BlockPos pos) {
        int i = Math.min(
            worldIn.getEntitiesWithinAABB(Entity.class, PRESSURE_AABB.offset(pos))
                .size(),
            this.maxWeight);

        if (i > 0) {
            float f = (float) Math.min(this.maxWeight, i) / (float) this.maxWeight;
            return MathHelper.ceil(f * 15.0F);
        } else {
            return 0;
        }
    }

    protected void playClickOnSound(World worldIn, BlockPos color) {
        worldIn.playSound(
            (EntityPlayer) null,
            color,
            SoundEvents.BLOCK_METAL_PRESSPLATE_CLICK_ON,
            SoundCategory.BLOCKS,
            0.3F,
            0.90000004F);
    }

    protected void playClickOffSound(World worldIn, BlockPos pos) {
        worldIn.playSound(
            (EntityPlayer) null,
            pos,
            SoundEvents.BLOCK_METAL_PRESSPLATE_CLICK_OFF,
            SoundCategory.BLOCKS,
            0.3F,
            0.75F);
    }

    protected int getRedstoneStrength(IBlockState state) {
        return ((Integer) state.getValue(POWER)).intValue();
    }

    protected IBlockState setRedstoneStrength(IBlockState state, int strength) {
        return state.withProperty(POWER, Integer.valueOf(strength));
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn) {
        return 10;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState()
            .withProperty(POWER, Integer.valueOf(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
        return ((Integer) state.getValue(POWER)).intValue();
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{POWER});
    }
}
