/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block;

import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.material.MapColor;
import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.properties.PropertyEnum;
import shordinger.wrapper.net.minecraft.block.state.BlockFaceShape;
import shordinger.wrapper.net.minecraft.block.state.BlockStateContainer;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.IStringSerializable;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.util.math.AxisAlignedBB;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.IBlockAccess;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockInfusedWood
 * Created by HellFirePvP
 * Date: 05.06.2018 / 16:15
 */
public class BlockInfusedWood extends Block implements BlockCustomName, BlockVariants {

    public static PropertyEnum<WoodType> WOOD_TYPE = PropertyEnum.create("woodtype", WoodType.class);

    public BlockInfusedWood() {
        super(Material.WOOD, MapColor.BROWN);
        setHardness(1.0F);
        setHarvestLevel("axe", 0);
        setResistance(3.0F);
        setSoundType(SoundType.WOOD);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        setDefaultState(
            this.blockState.getBaseState()
                .withProperty(WOOD_TYPE, WoodType.RAW));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (WoodType t : WoodType.values()) {
            if (!t.obtainableInCreative()) continue;
            list.add(new ItemStack(this, 1, t.getMeta()));
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        // return super.getActualState(state, worldIn, pos);
        if (state.getValue(WOOD_TYPE)
            .isColumn()) {
            IBlockState st = worldIn.getBlockState(pos.up());
            boolean top = false;
            if (st.getBlock() instanceof BlockInfusedWood && st.getValue(WOOD_TYPE)
                .isColumn()) {
                top = true;
            }
            st = worldIn.getBlockState(pos.down());
            boolean down = false;
            if (st.getBlock() instanceof BlockInfusedWood && st.getValue(WOOD_TYPE)
                .isColumn()) {
                down = true;
            }
            if (top && down) {
                return state.withProperty(WOOD_TYPE, WoodType.COLUMN);
            } else if (top) {
                return state.withProperty(WOOD_TYPE, WoodType.COLUMN_BOTTOM);
            } else if (down) {
                return state.withProperty(WOOD_TYPE, WoodType.COLUMN_TOP);
            } else {
                return state.withProperty(WOOD_TYPE, WoodType.COLUMN);
            }
        }
        return super.getActualState(state, worldIn, pos);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        IBlockState actual = state.getActualState(source, pos);
        switch (actual.getValue(WOOD_TYPE)) {
            case COLUMN:
                return new AxisAlignedBB(0.25, 0, 0.25, 0.75, 1, 0.75);
            case COLUMN_TOP:
            case COLUMN_BOTTOM:
                return new AxisAlignedBB(0.125, 0, 0.125, 0.875, 1, 0.875);
        }
        return super.getBoundingBox(state, source, pos);
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        return state.getValue(WOOD_TYPE) != WoodType.COLUMN && super.causesSuffocation(state);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_,
                                            EnumFacing p_193383_4_) {
        return p_193383_2_.getValue(WOOD_TYPE)
            .isColumn() ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        WoodType woodType = state.getValue(WOOD_TYPE);
        if (woodType == WoodType.COLUMN_TOP || woodType == WoodType.COLUMN || woodType == WoodType.COLUMN_BOTTOM) {
            return 0;
        }
        return super.getLightOpacity(state, world, pos);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        WoodType woodType = state.getValue(WOOD_TYPE);
        return woodType != WoodType.COLUMN && woodType != WoodType.COLUMN_BOTTOM && woodType != WoodType.COLUMN_TOP;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        WoodType woodType = state.getValue(WOOD_TYPE);
        return woodType != WoodType.COLUMN && woodType != WoodType.COLUMN_BOTTOM && woodType != WoodType.COLUMN_TOP;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        WoodType woodType = state.getValue(WOOD_TYPE);
        return woodType != WoodType.COLUMN && woodType != WoodType.COLUMN_BOTTOM && woodType != WoodType.COLUMN_TOP;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        WoodType woodType = state.getValue(WOOD_TYPE);
        IBlockState other = world.getBlockState(pos.offset(face));
        if (MiscUtils.isFluidBlock(other)
            && (woodType == WoodType.COLUMN || woodType == WoodType.COLUMN_BOTTOM || woodType == WoodType.COLUMN_TOP)) {
            return false;
        }
        if (woodType == WoodType.COLUMN_TOP) {
            return face == EnumFacing.UP;
        }
        if (woodType == WoodType.COLUMN_BOTTOM) {
            return face == EnumFacing.DOWN;
        }
        return state.isOpaqueCube();
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return true;
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        WoodType mt = getStateFromMeta(meta).getValue(WOOD_TYPE);
        return mt.getName();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        WoodType type = state.getValue(WOOD_TYPE);
        return type.getMeta();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return meta < WoodType.values().length ? getDefaultState().withProperty(WOOD_TYPE, WoodType.values()[meta])
            : getDefaultState();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, WOOD_TYPE);
    }

    @Override
    public List<IBlockState> getValidStates() {
        List<IBlockState> ret = new LinkedList<>();
        for (WoodType type : WoodType.values()) {
            ret.add(getDefaultState().withProperty(WOOD_TYPE, type));
        }
        return ret;
    }

    @Override
    public String getStateName(IBlockState state) {
        return state.getValue(WOOD_TYPE)
            .getName();
    }

    public static enum WoodType implements IStringSerializable {

        RAW(0),
        PLANKS(1),
        COLUMN(2),
        ARCH(3),
        ENGRAVED(4),
        ENRICHED(5),
        INFUSED(6),

        COLUMN_TOP(2),
        COLUMN_BOTTOM(2);

        private final int meta;

        private WoodType(int meta) {
            this.meta = meta;
        }

        public ItemStack asStack() {
            return new ItemStack(BlocksAS.blockInfusedWood, 1, meta);
        }

        public IBlockState asBlock() {
            return BlocksAS.blockInfusedWood.getStateFromMeta(meta);
        }

        public boolean isColumn() {
            return this == COLUMN_BOTTOM || this == COLUMN || this == COLUMN_TOP;
        }

        public boolean obtainableInCreative() {
            return this != COLUMN_TOP && this != COLUMN_BOTTOM;
        }

        public int getMeta() {
            return meta;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }

}
