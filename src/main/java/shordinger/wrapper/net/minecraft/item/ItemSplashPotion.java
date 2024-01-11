package shordinger.wrapper.net.minecraft.item;

import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityPotion;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.potion.PotionUtils;
import shordinger.wrapper.net.minecraft.stats.StatList;
import shordinger.wrapper.net.minecraft.util.ActionResult;
import shordinger.wrapper.net.minecraft.util.EnumActionResult;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.SoundCategory;
import shordinger.wrapper.net.minecraft.util.text.translation.I18n;
import shordinger.wrapper.net.minecraft.world.World;

public class ItemSplashPotion extends ItemPotion {

    public String getItemStackDisplayName(ItemStack stack) {
        return I18n.translateToLocal(
            PotionUtils.getPotionFromItem(stack)
                .getNamePrefixed("splash_potion.effect."));
    }

    /**
     * Called when the equipped item is right clicked.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        ItemStack itemstack1 = playerIn.capabilities.isCreativeMode ? itemstack.copy() : itemstack.splitStack(1);
        worldIn.playSound(
            (EntityPlayer) null,
            playerIn.posX,
            playerIn.posY,
            playerIn.posZ,
            SoundEvents.ENTITY_SPLASH_POTION_THROW,
            SoundCategory.PLAYERS,
            0.5F,
            0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isRemote) {
            EntityPotion entitypotion = new EntityPotion(worldIn, playerIn, itemstack1);
            entitypotion.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, -20.0F, 0.5F, 1.0F);
            worldIn.spawnEntity(entitypotion);
        }

        playerIn.addStat(StatList.getObjectUseStats(this));
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
    }
}
