/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.IBlockState;
import shordinger.astralsorcery.migration.NonNullList;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockMarbleSlab
 * Created by HellFirePvP
 * Date: 05.07.2017 / 18:47
 */
public class BlockMarbleSlab extends BlockSlab {

    public static final PropertyEnum<EnumType> MARBLE_TYPE = PropertyEnum.create("marbletype", EnumType.class);

    public BlockMarbleSlab() {
        super(Material.ROCK, MapColor.QUARTZ);
        IBlockState state = this.blockState.getBaseState();
        if (!isDouble()) {
            state = state.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
        }
        setDefaultState(state.withProperty(MARBLE_TYPE, EnumType.BRICKS));
        setSoundType(SoundType.STONE);
        setLightOpacity(0);
        setHardness(1.0F);
        setHarvestLevel("pickaxe", 1);
        setResistance(3.0F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(BlocksAS.blockMarbleSlab);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
                                  EntityPlayer player) {
        return new ItemStack(
            BlocksAS.blockMarbleSlab,
            1,
            state.getValue(MARBLE_TYPE)
                .ordinal());
    }

    @Override
    public String getUnlocalizedName(int meta) {
        return super.getUnlocalizedName() + "."
            + EnumType.byMetadata(meta)
            .getName();
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (EnumType type : EnumType.values()) {
            items.add(new ItemStack(this, 1, type.ordinal()));
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState iblockstate = getDefaultState().withProperty(MARBLE_TYPE, EnumType.byMetadata(meta & 7));
        if (!isDouble()) {
            iblockstate = iblockstate
                .withProperty(HALF, (meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
        }
        return iblockstate;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = state.getValue(MARBLE_TYPE)
            .ordinal();
        if (!isDouble()) {
            if (state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP) {
                i |= 8;
            }
        }
        return i;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return isDouble() ? new BlockStateContainer(this, MARBLE_TYPE)
            : new BlockStateContainer(this, HALF, MARBLE_TYPE);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(MARBLE_TYPE)
            .ordinal();
    }

    @Override
    public boolean isDouble() {
        return false;
    }

    @Override
    public IProperty<?> getVariantProperty() {
        return MARBLE_TYPE;
    }

    @Override
    public Comparable<?> getTypeForItem(ItemStack stack) {
        return EnumType.byMetadata(stack.getMetadata() & 7);
    }

    public static enum EnumType implements IStringSerializable {

        BRICKS;

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public static EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= values().length) {
                meta = 0;
            }
            return values()[meta];
        }
    }

}
