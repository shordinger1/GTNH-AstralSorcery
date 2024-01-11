package shordinger.wrapper.net.minecraft.block;

import java.util.Random;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.material.EnumPushReaction;
import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.enchantment.EnchantmentHelper;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.init.Enchantments;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.stats.StatList;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.util.BlockRenderLayer;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.EnumSkyBlock;
import shordinger.wrapper.net.minecraft.world.World;

public class BlockIce extends BlockBreakable {

    public BlockIce() {
        super(Material.ICE, false);
        this.slipperiness = 0.98F;
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    /**
     * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
     * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
     */
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    /**
     * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
     * Block.removedByPlayer
     */
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state,
                             @Nullable TileEntity te, ItemStack stack) {
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.005F);

        if (this.canSilkHarvest(worldIn, pos, state, player)
            && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0) {
            java.util.List<ItemStack> items = new java.util.ArrayList<ItemStack>();
            items.add(this.getSilkTouchDrop(state));

            net.minecraftforge.event.ForgeEventFactory
                .fireBlockHarvesting(items, worldIn, pos, state, 0, 1.0f, true, player);
            for (ItemStack is : items) spawnAsEntity(worldIn, pos, is);
        } else {
            if (worldIn.provider.doesWaterVaporize()) {
                worldIn.setBlockToAir(pos);
                return;
            }

            int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
            harvesters.set(player);
            this.dropBlockAsItem(worldIn, pos, state, i);
            harvesters.set(null);
            Material material = worldIn.getBlockState(pos.down())
                .getMaterial();

            if (material.blocksMovement() || material.isLiquid()) {
                worldIn.setBlockState(pos, Blocks.FLOWING_WATER.getDefaultState());
            }
        }
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random) {
        return 0;
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (worldIn.getLightFor(EnumSkyBlock.BLOCK, pos) > 11 - this.getDefaultState()
            .getLightOpacity()) {
            this.turnIntoWater(worldIn, pos);
        }
    }

    protected void turnIntoWater(World worldIn, BlockPos pos) {
        if (worldIn.provider.doesWaterVaporize()) {
            worldIn.setBlockToAir(pos);
        } else {
            this.dropBlockAsItem(worldIn, pos, worldIn.getBlockState(pos), 0);
            worldIn.setBlockState(pos, Blocks.WATER.getDefaultState());
            worldIn.neighborChanged(pos, Blocks.WATER, pos);
        }
    }

    public EnumPushReaction getMobilityFlag(IBlockState state) {
        return EnumPushReaction.NORMAL;
    }
}
