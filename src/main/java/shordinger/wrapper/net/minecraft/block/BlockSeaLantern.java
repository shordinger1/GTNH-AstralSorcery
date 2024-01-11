package shordinger.wrapper.net.minecraft.block;

import java.util.Random;

import shordinger.wrapper.net.minecraft.block.material.MapColor;
import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.world.IBlockAccess;

public class BlockSeaLantern extends Block {

    public BlockSeaLantern(Material materialIn) {
        super(materialIn);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random) {
        return 2 + random.nextInt(2);
    }

    /**
     * Get the quantity dropped based on the given fortune level
     */
    public int quantityDroppedWithBonus(int fortune, Random random) {
        return MathHelper.clamp(this.quantityDropped(random) + random.nextInt(fortune + 1), 1, 5);
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.PRISMARINE_CRYSTALS;
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    public net.minecraft.block.material.MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return MapColor.QUARTZ;
    }

    protected boolean canSilkHarvest() {
        return true;
    }
}
