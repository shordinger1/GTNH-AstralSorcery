package shordinger.wrapper.net.minecraft.item;

import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.EntityLiving;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.util.EnumHand;

public class ItemNameTag extends Item {

    public ItemNameTag() {
        this.setCreativeTab(CreativeTabs.TOOLS);
    }

    /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     */
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target,
                                            EnumHand hand) {
        if (stack.hasDisplayName() && !(target instanceof EntityPlayer)) {
            target.setCustomNameTag(stack.getDisplayName());

            if (target instanceof EntityLiving) {
                ((EntityLiving) target).enablePersistence();
            }

            stack.shrink(1);
            return true;
        } else {
            return false;
        }
    }
}
