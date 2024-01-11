package shordinger.wrapper.net.minecraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.passive.EntityPig;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.stats.StatList;
import shordinger.wrapper.net.minecraft.util.ActionResult;
import shordinger.wrapper.net.minecraft.util.EnumActionResult;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.world.World;

public class ItemCarrotOnAStick extends Item {

    public ItemCarrotOnAStick() {
        this.setCreativeTab(CreativeTabs.TRANSPORTATION);
        this.setMaxStackSize(1);
        this.setMaxDamage(25);
    }

    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }

    /**
     * Returns true if this item should be rotated by 180 degrees around the Y axis when being held in an entities
     * hands.
     */
    @SideOnly(Side.CLIENT)
    public boolean shouldRotateAroundWhenRendering() {
        return true;
    }

    /**
     * Called when the equipped item is right clicked.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        if (worldIn.isRemote) {
            return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);
        } else {
            if (playerIn.isRiding() && playerIn.getRidingEntity() instanceof EntityPig) {
                EntityPig entitypig = (EntityPig) playerIn.getRidingEntity();

                if (itemstack.getMaxDamage() - itemstack.getMetadata() >= 7 && entitypig.boost()) {
                    itemstack.damageItem(7, playerIn);

                    if (itemstack.isEmpty()) {
                        ItemStack itemstack1 = new ItemStack(Items.FISHING_ROD);
                        itemstack1.setTagCompound(itemstack.getTagCompound());
                        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack1);
                    }

                    return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
                }
            }

            playerIn.addStat(StatList.getObjectUseStats(this));
            return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);
        }
    }
}
