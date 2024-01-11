package shordinger.wrapper.net.minecraft.item;

import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.util.EnumActionResult;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.SoundCategory;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

public class ItemFireball extends Item {

    public ItemFireball() {
        this.setCreativeTab(CreativeTabs.MISC);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return EnumActionResult.SUCCESS;
        } else {
            pos = pos.offset(facing);
            ItemStack itemstack = player.getHeldItem(hand);

            if (!player.canPlayerEdit(pos, facing, itemstack)) {
                return EnumActionResult.FAIL;
            } else {
                if (worldIn.getBlockState(pos)
                    .getMaterial() == Material.AIR) {
                    worldIn.playSound(
                        (EntityPlayer) null,
                        pos,
                        SoundEvents.ITEM_FIRECHARGE_USE,
                        SoundCategory.BLOCKS,
                        1.0F,
                        (itemRand.nextFloat() - itemRand.nextFloat()) * 0.2F + 1.0F);
                    worldIn.setBlockState(pos, Blocks.FIRE.getDefaultState());
                }

                if (!player.capabilities.isCreativeMode) {
                    itemstack.shrink(1);
                }

                return EnumActionResult.SUCCESS;
            }
        }
    }
}
