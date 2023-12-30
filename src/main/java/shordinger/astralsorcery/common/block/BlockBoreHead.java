/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.tile.TileBore;
import shordinger.astralsorcery.migration.block.AstralBlock;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.IBlockState;
import shordinger.astralsorcery.migration.MathHelper;
import shordinger.astralsorcery.migration.NonNullList;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockBoreHead
 * Created by HellFirePvP
 * Date: 07.11.2017 / 20:22
 */
public class BlockBoreHead extends AstralBlock implements BlockCustomName, BlockVariants {

    public static final PropertyEnum<TileBore.BoreType> BORE_TYPE = PropertyEnum
        .create("type", TileBore.BoreType.class);

    public BlockBoreHead() {
        super(Material.IRON, MapColor.GOLD);
        setHarvestLevel("pickaxe", 2);
        setHardness(10F);
        setResistance(15F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        setDefaultState(
            this.blockState.getBaseState()
                .withProperty(BORE_TYPE, TileBore.BoreType.LIQUID));
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (TileBore.BoreType bt : TileBore.BoreType.values()) {
            items.add(new ItemStack(this, 1, bt.ordinal()));
        }
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, ForgeDirection side) {
        return super.canPlaceBlockAt(worldIn, pos) && side == ForgeDirection.DOWN
            && worldIn.getBlockState(pos.offset(ForgeDirection.UP))
            .getBlock() instanceof BlockBore;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, ForgeDirection side) {
        return false;
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_,
                                            ForgeDirection p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BORE_TYPE)
            .ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(
            BORE_TYPE,
            TileBore.BoreType.values()[MathHelper.clamp(meta, 0, TileBore.BoreType.values().length - 1)]);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BORE_TYPE);
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        TileBore.BoreType bt = getStateFromMeta(meta).getValue(BORE_TYPE);
        return bt.getName();
    }

    @Override
    public List<IBlockState> getValidStates() {
        return singleEnumPropertyStates(getDefaultState(), BORE_TYPE, TileBore.BoreType.values());
    }

    @Override
    public String getStateName(IBlockState state) {
        return extractEnumPropertyString(state, BORE_TYPE);
    }
}
