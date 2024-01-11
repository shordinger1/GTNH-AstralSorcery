package shordinger.wrapper.net.minecraft.item;

import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.stats.StatList;
import shordinger.wrapper.net.minecraft.util.ActionResult;
import shordinger.wrapper.net.minecraft.util.EnumActionResult;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.world.World;

public class ItemEmptyMap extends ItemMapBase {

    protected ItemEmptyMap() {
        this.setCreativeTab(CreativeTabs.MISC);
    }

    /**
     * Called when the equipped item is right clicked.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = ItemMap.setupNewMap(worldIn, playerIn.posX, playerIn.posZ, (byte) 0, true, false);
        ItemStack itemstack1 = playerIn.getHeldItem(handIn);
        itemstack1.shrink(1);

        if (itemstack1.isEmpty()) {
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
        } else {
            if (!playerIn.inventory.addItemStackToInventory(itemstack.copy())) {
                playerIn.dropItem(itemstack, false);
            }

            playerIn.addStat(StatList.getObjectUseStats(this));
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack1);
        }
    }
}
