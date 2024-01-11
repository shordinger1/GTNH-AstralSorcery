package shordinger.wrapper.net.minecraft.item;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.EntityAreaEffectCloud;
import shordinger.wrapper.net.minecraft.entity.boss.EntityDragon;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.init.PotionTypes;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.potion.PotionUtils;
import shordinger.wrapper.net.minecraft.stats.StatList;
import shordinger.wrapper.net.minecraft.util.ActionResult;
import shordinger.wrapper.net.minecraft.util.EnumActionResult;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.SoundCategory;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.RayTraceResult;
import shordinger.wrapper.net.minecraft.world.World;

public class ItemGlassBottle extends Item {

    public ItemGlassBottle() {
        this.setCreativeTab(CreativeTabs.BREWING);
    }

    /**
     * Called when the equipped item is right clicked.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        List<EntityAreaEffectCloud> list = worldIn.<EntityAreaEffectCloud>getEntitiesWithinAABB(
            EntityAreaEffectCloud.class,
            playerIn.getEntityBoundingBox()
                .grow(2.0D),
            new Predicate<EntityAreaEffectCloud>() {

                public boolean apply(@Nullable EntityAreaEffectCloud p_apply_1_) {
                    return p_apply_1_ != null && p_apply_1_.isEntityAlive()
                        && p_apply_1_.getOwner() instanceof EntityDragon;
                }
            });
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        if (!list.isEmpty()) {
            EntityAreaEffectCloud entityareaeffectcloud = list.get(0);
            entityareaeffectcloud.setRadius(entityareaeffectcloud.getRadius() - 0.5F);
            worldIn.playSound(
                (EntityPlayer) null,
                playerIn.posX,
                playerIn.posY,
                playerIn.posZ,
                SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH,
                SoundCategory.NEUTRAL,
                1.0F,
                1.0F);
            return new ActionResult(
                EnumActionResult.SUCCESS,
                this.turnBottleIntoItem(itemstack, playerIn, new ItemStack(Items.DRAGON_BREATH)));
        } else {
            RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, true);

            if (raytraceresult == null) {
                return new ActionResult(EnumActionResult.PASS, itemstack);
            } else {
                if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK) {
                    BlockPos blockpos = raytraceresult.getBlockPos();

                    if (!worldIn.isBlockModifiable(playerIn, blockpos) || !playerIn
                        .canPlayerEdit(blockpos.offset(raytraceresult.sideHit), raytraceresult.sideHit, itemstack)) {
                        return new ActionResult(EnumActionResult.PASS, itemstack);
                    }

                    if (worldIn.getBlockState(blockpos)
                        .getMaterial() == Material.WATER) {
                        worldIn.playSound(
                            playerIn,
                            playerIn.posX,
                            playerIn.posY,
                            playerIn.posZ,
                            SoundEvents.ITEM_BOTTLE_FILL,
                            SoundCategory.NEUTRAL,
                            1.0F,
                            1.0F);
                        return new ActionResult(
                            EnumActionResult.SUCCESS,
                            this.turnBottleIntoItem(
                                itemstack,
                                playerIn,
                                PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER)));
                    }
                }

                return new ActionResult(EnumActionResult.PASS, itemstack);
            }
        }
    }

    protected ItemStack turnBottleIntoItem(ItemStack p_185061_1_, EntityPlayer player, ItemStack stack) {
        p_185061_1_.shrink(1);
        player.addStat(StatList.getObjectUseStats(this));

        if (p_185061_1_.isEmpty()) {
            return stack;
        } else {
            if (!player.inventory.addItemStackToInventory(stack)) {
                player.dropItem(stack, false);
            }

            return p_185061_1_;
        }
    }
}
