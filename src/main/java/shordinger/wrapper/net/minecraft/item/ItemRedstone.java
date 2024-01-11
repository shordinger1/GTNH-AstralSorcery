package shordinger.wrapper.net.minecraft.item;

import shordinger.wrapper.net.minecraft.advancements.CriteriaTriggers;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.util.EnumActionResult;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

public class ItemRedstone extends Item {

    public ItemRedstone() {
        this.setCreativeTab(CreativeTabs.REDSTONE);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        boolean flag = worldIn.getBlockState(pos)
            .getBlock()
            .isReplaceable(worldIn, pos);
        BlockPos blockpos = flag ? pos : pos.offset(facing);
        ItemStack itemstack = player.getHeldItem(hand);

        if (player.canPlayerEdit(blockpos, facing, itemstack) && worldIn.mayPlace(
            worldIn.getBlockState(blockpos)
                .getBlock(),
            blockpos,
            false,
            facing,
            player) && Blocks.REDSTONE_WIRE.canPlaceBlockAt(worldIn, blockpos)) {
            worldIn.setBlockState(blockpos, Blocks.REDSTONE_WIRE.getDefaultState());

            if (player instanceof EntityPlayerMP) {
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, blockpos, itemstack);
            }

            itemstack.shrink(1);
            return EnumActionResult.SUCCESS;
        } else {
            return EnumActionResult.FAIL;
        }
    }
}
