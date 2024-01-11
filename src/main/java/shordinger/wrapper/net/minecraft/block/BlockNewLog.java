package shordinger.wrapper.net.minecraft.block;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import shordinger.wrapper.net.minecraft.block.material.MapColor;
import shordinger.wrapper.net.minecraft.block.properties.IProperty;
import shordinger.wrapper.net.minecraft.block.properties.PropertyEnum;
import shordinger.wrapper.net.minecraft.block.state.BlockStateContainer;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.IBlockAccess;

public class BlockNewLog extends BlockLog {

    public static final PropertyEnum<BlockPlanks.EnumType> VARIANT = PropertyEnum.<BlockPlanks.EnumType>create(
        "variant",
        BlockPlanks.EnumType.class,
        new Predicate<BlockPlanks.EnumType>() {

            public boolean apply(@Nullable BlockPlanks.EnumType p_apply_1_) {
                return p_apply_1_.getMetadata() >= 4;
            }
        });

    public BlockNewLog() {
        this.setDefaultState(
            this.blockState.getBaseState()
                .withProperty(VARIANT, BlockPlanks.EnumType.ACACIA)
                .withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    public net.minecraft.block.material.MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        BlockPlanks.EnumType blockplanks$enumtype = (BlockPlanks.EnumType) state.getValue(VARIANT);

        switch ((BlockLog.EnumAxis) state.getValue(LOG_AXIS)) {
            case X:
            case Z:
            case NONE:
            default:

                switch (blockplanks$enumtype) {
                    case ACACIA:
                    default:
                        return MapColor.STONE;
                    case DARK_OAK:
                        return BlockPlanks.EnumType.DARK_OAK.getMapColor();
                }

            case Y:
                return blockplanks$enumtype.getMapColor();
        }
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, BlockPlanks.EnumType.ACACIA.getMetadata() - 4));
        items.add(new ItemStack(this, 1, BlockPlanks.EnumType.DARK_OAK.getMetadata() - 4));
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta) {
        IBlockState iblockstate = this.getDefaultState()
            .withProperty(VARIANT, BlockPlanks.EnumType.byMetadata((meta & 3) + 4));

        switch (meta & 12) {
            case 0:
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y);
                break;
            case 4:
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.X);
                break;
            case 8:
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z);
                break;
            default:
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.NONE);
        }

        return iblockstate;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @SuppressWarnings("incomplete-switch")
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | ((BlockPlanks.EnumType) state.getValue(VARIANT)).getMetadata() - 4;

        switch ((BlockLog.EnumAxis) state.getValue(LOG_AXIS)) {
            case X:
                i |= 4;
                break;
            case Z:
                i |= 8;
                break;
            case NONE:
                i |= 12;
        }

        return i;
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{VARIANT, LOG_AXIS});
    }

    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return new ItemStack(
            Item.getItemFromBlock(this),
            1,
            ((BlockPlanks.EnumType) state.getValue(VARIANT)).getMetadata() - 4);
    }

    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    public int damageDropped(IBlockState state) {
        return ((BlockPlanks.EnumType) state.getValue(VARIANT)).getMetadata() - 4;
    }
}