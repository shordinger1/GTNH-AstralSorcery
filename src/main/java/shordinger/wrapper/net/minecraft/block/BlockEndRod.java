package shordinger.wrapper.net.minecraft.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.material.EnumPushReaction;
import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.properties.IProperty;
import shordinger.wrapper.net.minecraft.block.state.BlockFaceShape;
import shordinger.wrapper.net.minecraft.block.state.BlockStateContainer;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.util.BlockRenderLayer;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.EnumParticleTypes;
import shordinger.wrapper.net.minecraft.util.Mirror;
import shordinger.wrapper.net.minecraft.util.Rotation;
import shordinger.wrapper.net.minecraft.util.math.AxisAlignedBB;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.IBlockAccess;
import shordinger.wrapper.net.minecraft.world.World;

public class BlockEndRod extends BlockDirectional {

    protected static final AxisAlignedBB END_ROD_VERTICAL_AABB = new AxisAlignedBB(
        0.375D,
        0.0D,
        0.375D,
        0.625D,
        1.0D,
        0.625D);
    protected static final AxisAlignedBB END_ROD_NS_AABB = new AxisAlignedBB(
        0.375D,
        0.375D,
        0.0D,
        0.625D,
        0.625D,
        1.0D);
    protected static final AxisAlignedBB END_ROD_EW_AABB = new AxisAlignedBB(
        0.0D,
        0.375D,
        0.375D,
        1.0D,
        0.625D,
        0.625D);

    protected BlockEndRod() {
        super(Material.CIRCUITS);
        this.setDefaultState(
            this.blockState.getBaseState()
                .withProperty(FACING, EnumFacing.UP));
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withProperty(FACING, mirrorIn.mirror((EnumFacing) state.getValue(FACING)));
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (((EnumFacing) state.getValue(FACING)).getAxis()) {
            case X:
            default:
                return END_ROD_EW_AABB;
            case Z:
                return END_ROD_NS_AABB;
            case Y:
                return END_ROD_VERTICAL_AABB;
        }
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    /**
     * Checks if this block can be placed exactly at the given position.
     */
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return true;
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer) {
        IBlockState iblockstate = worldIn.getBlockState(pos.offset(facing.getOpposite()));

        if (iblockstate.getBlock() == Blocks.END_ROD) {
            EnumFacing enumfacing = (EnumFacing) iblockstate.getValue(FACING);

            if (enumfacing == facing) {
                return this.getDefaultState()
                    .withProperty(FACING, facing.getOpposite());
            }
        }

        return this.getDefaultState()
            .withProperty(FACING, facing);
    }

    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
     * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
     * of whether the block can receive random update ticks
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        EnumFacing enumfacing = (EnumFacing) stateIn.getValue(FACING);
        double d0 = (double) pos.getX() + 0.55D - (double) (rand.nextFloat() * 0.1F);
        double d1 = (double) pos.getY() + 0.55D - (double) (rand.nextFloat() * 0.1F);
        double d2 = (double) pos.getZ() + 0.55D - (double) (rand.nextFloat() * 0.1F);
        double d3 = (double) (0.4F - (rand.nextFloat() + rand.nextFloat()) * 0.4F);

        if (rand.nextInt(5) == 0) {
            worldIn.spawnParticle(
                EnumParticleTypes.END_ROD,
                d0 + (double) enumfacing.getFrontOffsetX() * d3,
                d1 + (double) enumfacing.getFrontOffsetY() * d3,
                d2 + (double) enumfacing.getFrontOffsetZ() * d3,
                rand.nextGaussian() * 0.005D,
                rand.nextGaussian() * 0.005D,
                rand.nextGaussian() * 0.005D);
        }
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta) {
        IBlockState iblockstate = this.getDefaultState();
        iblockstate = iblockstate.withProperty(FACING, EnumFacing.getFront(meta));
        return iblockstate;
    }

    /**
     * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
     * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
     */
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
        return ((EnumFacing) state.getValue(FACING)).getIndex();
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{FACING});
    }

    public EnumPushReaction getMobilityFlag(IBlockState state) {
        return EnumPushReaction.NORMAL;
    }

    /**
     * Get the geometry of the queried face at the given position and state. This is used to decide whether things like
     * buttons are allowed to be placed on the face, or how glass panes connect to the face, among other things.
     * <p>
     * Common values are {@code SOLID}, which is the default, and {@code UNDEFINED}, which represents something that
     * does not fit the other descriptions and will generally cause other things not to connect to the face.
     *
     * @return an approximation of the form of the given face
     */
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
}
