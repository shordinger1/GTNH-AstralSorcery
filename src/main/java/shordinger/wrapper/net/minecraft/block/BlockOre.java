package shordinger.wrapper.net.minecraft.block;

import java.util.Random;

import shordinger.wrapper.net.minecraft.block.material.MapColor;
import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.item.EnumDyeColor;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.world.World;

public class BlockOre extends Block {

    public BlockOre() {
        this(Material.ROCK.getMaterialMapColor());
    }

    public BlockOre(MapColor color) {
        super(Material.ROCK, color);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        if (this == Blocks.COAL_ORE) {
            return Items.COAL;
        } else if (this == Blocks.DIAMOND_ORE) {
            return Items.DIAMOND;
        } else if (this == Blocks.LAPIS_ORE) {
            return Items.DYE;
        } else if (this == Blocks.EMERALD_ORE) {
            return Items.EMERALD;
        } else {
            return this == Blocks.QUARTZ_ORE ? Items.QUARTZ : Item.getItemFromBlock(this);
        }
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random) {
        return this == Blocks.LAPIS_ORE ? 4 + random.nextInt(5) : 1;
    }

    /**
     * Get the quantity dropped based on the given fortune level
     */
    public int quantityDroppedWithBonus(int fortune, Random random) {
        if (fortune > 0 && Item.getItemFromBlock(this) != this.getItemDropped(
            (IBlockState) this.getBlockState()
                .getValidStates()
                .iterator()
                .next(),
            random,
            fortune)) {
            int i = random.nextInt(fortune + 2) - 1;

            if (i < 0) {
                i = 0;
            }

            return this.quantityDropped(random) * (i + 1);
        } else {
            return this.quantityDropped(random);
        }
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     */
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
    }

    @Override
    public int getExpDrop(IBlockState state, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune) {
        Random rand = world instanceof World ? ((World) world).rand : new Random();
        if (this.getItemDropped(state, rand, fortune) != Item.getItemFromBlock(this)) {
            int i = 0;

            if (this == Blocks.COAL_ORE) {
                i = MathHelper.getInt(rand, 0, 2);
            } else if (this == Blocks.DIAMOND_ORE) {
                i = MathHelper.getInt(rand, 3, 7);
            } else if (this == Blocks.EMERALD_ORE) {
                i = MathHelper.getInt(rand, 3, 7);
            } else if (this == Blocks.LAPIS_ORE) {
                i = MathHelper.getInt(rand, 2, 5);
            } else if (this == Blocks.QUARTZ_ORE) {
                i = MathHelper.getInt(rand, 2, 5);
            }

            return i;
        }
        return 0;
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(this);
    }

    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    public int damageDropped(IBlockState state) {
        return this == Blocks.LAPIS_ORE ? EnumDyeColor.BLUE.getDyeDamage() : 0;
    }
}
