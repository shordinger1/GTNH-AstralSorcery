package shordinger.wrapper.net.minecraft.block;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.material.MapColor;
import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.properties.IProperty;
import shordinger.wrapper.net.minecraft.block.properties.PropertyBool;
import shordinger.wrapper.net.minecraft.block.properties.PropertyDirection;
import shordinger.wrapper.net.minecraft.block.state.BlockFaceShape;
import shordinger.wrapper.net.minecraft.block.state.BlockStateContainer;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.inventory.Container;
import shordinger.wrapper.net.minecraft.inventory.InventoryHelper;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.stats.StatList;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.tileentity.TileEntityHopper;
import shordinger.wrapper.net.minecraft.util.BlockRenderLayer;
import shordinger.wrapper.net.minecraft.util.EnumBlockRenderType;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.Mirror;
import shordinger.wrapper.net.minecraft.util.Rotation;
import shordinger.wrapper.net.minecraft.util.math.AxisAlignedBB;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.IBlockAccess;
import shordinger.wrapper.net.minecraft.world.World;

public class BlockHopper extends BlockContainer {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", new Predicate<EnumFacing>() {

        public boolean apply(@Nullable EnumFacing p_apply_1_) {
            return p_apply_1_ != EnumFacing.UP;
        }
    });
    public static final PropertyBool ENABLED = PropertyBool.create("enabled");
    protected static final AxisAlignedBB BASE_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D);
    protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
    protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);

    public BlockHopper() {
        super(Material.IRON, MapColor.STONE);
        this.setDefaultState(
            this.blockState.getBaseState()
                .withProperty(FACING, EnumFacing.DOWN)
                .withProperty(ENABLED, Boolean.valueOf(true)));
        this.setCreativeTab(CreativeTabs.REDSTONE);
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
                                      List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, BASE_AABB);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, EAST_AABB);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, WEST_AABB);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, SOUTH_AABB);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, NORTH_AABB);
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer) {
        EnumFacing enumfacing = facing.getOpposite();

        if (enumfacing == EnumFacing.UP) {
            enumfacing = EnumFacing.DOWN;
        }

        return this.getDefaultState()
            .withProperty(FACING, enumfacing)
            .withProperty(ENABLED, Boolean.valueOf(true));
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityHopper();
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
                                ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

        if (stack.hasDisplayName()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityHopper) {
                ((TileEntityHopper) tileentity).setCustomName(stack.getDisplayName());
            }
        }
    }

    /**
     * Determines if the block is solid enough on the top side to support other blocks, like redstone components.
     */
    public boolean isTopSolid(IBlockState state) {
        return true;
    }

    /**
     * Called after the block is set in the Chunk data, but before the Tile Entity is set
     */
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        this.updateState(worldIn, pos, state);
    }

    /**
     * Called when the block is right clicked by a player.
     */
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        } else {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityHopper) {
                playerIn.displayGUIChest((TileEntityHopper) tileentity);
                playerIn.addStat(StatList.HOPPER_INSPECTED);
            }

            return true;
        }
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        this.updateState(worldIn, pos, state);
    }

    private void updateState(World worldIn, BlockPos pos, IBlockState state) {
        boolean flag = !worldIn.isBlockPowered(pos);

        if (flag != ((Boolean) state.getValue(ENABLED)).booleanValue()) {
            worldIn.setBlockState(pos, state.withProperty(ENABLED, Boolean.valueOf(flag)), 4);
        }
    }

    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityHopper) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityHopper) tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     */
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public static EnumFacing getFacing(int meta) {
        return EnumFacing.getFront(meta & 7);
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos,
                                        EnumFacing side) {
        return true;
    }

    /**
     * Get's the hopper's active status from the 8-bit of the metadata. Note that the metadata stores whether the block
     * is powered, so this returns true when that bit is 0.
     */
    public static boolean isEnabled(int meta) {
        return (meta & 8) != 8;
    }

    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        return Container.calcRedstone(worldIn.getTileEntity(pos));
    }

    /**
     * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
     * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
     */
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState()
            .withProperty(FACING, getFacing(meta))
            .withProperty(ENABLED, Boolean.valueOf(isEnabled(meta)));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | ((EnumFacing) state.getValue(FACING)).getIndex();

        if (!((Boolean) state.getValue(ENABLED)).booleanValue()) {
            i |= 8;
        }

        return i;
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
        return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{FACING, ENABLED});
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
        return face == EnumFacing.UP ? BlockFaceShape.BOWL : BlockFaceShape.UNDEFINED;
    }
}
