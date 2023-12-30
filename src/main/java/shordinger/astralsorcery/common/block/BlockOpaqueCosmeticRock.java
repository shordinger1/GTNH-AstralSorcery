/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.migration.IStringSerializable;
import shordinger.astralsorcery.migration.block.AstralBlock;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.BlockStateContainer;
import shordinger.astralsorcery.migration.block.IBlockState;
import shordinger.astralsorcery.migration.NonNullList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockOpaqueCosmetic
 * Created by HellFirePvP
 * Date: 12.05.2016 / 16:58
 */
public class BlockOpaqueCosmeticRock extends AstralBlock implements BlockCustomName {

    public static PropertyEnum<BlockType> BLOCK_TYPE = PropertyEnum.create("blocktype", BlockType.class);

    public BlockOpaqueCosmeticRock() {
        super(Material.ROCK, MapColor.IRON);
        setHardness(2.0F);
        setHarvestLevel("pickaxe", 3);
        setResistance(20.0F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (BlockType bt : BlockType.values()) {
            list.add(new ItemStack(this, 1, bt.ordinal()));
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return meta < BlockType.values().length ? getDefaultState().withProperty(BLOCK_TYPE, BlockType.values()[meta])
            : getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        BlockType type = state.getValue(BLOCK_TYPE);
        return type.ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BLOCK_TYPE);
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        BlockType mt = getStateFromMeta(meta).getValue(BLOCK_TYPE);
        return mt.getName();
    }

    public static enum BlockType implements IStringSerializable {

        NONE;

        @Override
        public String getName() {
            return name().toLowerCase();
        }

    }

}
