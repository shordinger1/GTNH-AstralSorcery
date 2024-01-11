package shordinger.wrapper.net.minecraft.item;

import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.passive.EntityPig;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.SoundCategory;

public class ItemSaddle extends Item {

    public ItemSaddle() {
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.TRANSPORTATION);
    }

    /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     */
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target,
                                            EnumHand hand) {
        if (target instanceof EntityPig) {
            EntityPig entitypig = (EntityPig) target;

            if (!entitypig.getSaddled() && !entitypig.isChild()) {
                entitypig.setSaddled(true);
                entitypig.world.playSound(
                    playerIn,
                    entitypig.posX,
                    entitypig.posY,
                    entitypig.posZ,
                    SoundEvents.ENTITY_PIG_SADDLE,
                    SoundCategory.NEUTRAL,
                    0.5F,
                    1.0F);
                stack.shrink(1);
            }

            return true;
        } else {
            return false;
        }
    }
}
