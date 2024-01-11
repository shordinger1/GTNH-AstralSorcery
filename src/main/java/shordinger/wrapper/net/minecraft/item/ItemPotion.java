package shordinger.wrapper.net.minecraft.item;

import java.util.List;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.advancements.CriteriaTriggers;
import shordinger.wrapper.net.minecraft.client.util.ITooltipFlag;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.init.PotionTypes;
import shordinger.wrapper.net.minecraft.potion.PotionEffect;
import shordinger.wrapper.net.minecraft.potion.PotionType;
import shordinger.wrapper.net.minecraft.potion.PotionUtils;
import shordinger.wrapper.net.minecraft.stats.StatList;
import shordinger.wrapper.net.minecraft.util.ActionResult;
import shordinger.wrapper.net.minecraft.util.EnumActionResult;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.util.text.translation.I18n;
import shordinger.wrapper.net.minecraft.world.World;

public class ItemPotion extends Item {

    public ItemPotion() {
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.BREWING);
    }

    @SideOnly(Side.CLIENT)
    public ItemStack getDefaultInstance() {
        return PotionUtils.addPotionToItemStack(super.getDefaultInstance(), PotionTypes.WATER);
    }

    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
     * the Item before the action is complete.
     */
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        EntityPlayer entityplayer = entityLiving instanceof EntityPlayer ? (EntityPlayer) entityLiving : null;

        if (entityplayer == null || !entityplayer.capabilities.isCreativeMode) {
            stack.shrink(1);
        }

        if (entityplayer instanceof EntityPlayerMP) {
            CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) entityplayer, stack);
        }

        if (!worldIn.isRemote) {
            for (PotionEffect potioneffect : PotionUtils.getEffectsFromStack(stack)) {
                if (potioneffect.getPotion()
                    .isInstant()) {
                    potioneffect.getPotion()
                        .affectEntity(entityplayer, entityplayer, entityLiving, potioneffect.getAmplifier(), 1.0D);
                } else {
                    entityLiving.addPotionEffect(new PotionEffect(potioneffect));
                }
            }
        }

        if (entityplayer != null) {
            entityplayer.addStat(StatList.getObjectUseStats(this));
        }

        if (entityplayer == null || !entityplayer.capabilities.isCreativeMode) {
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (entityplayer != null) {
                entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        return stack;
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    /**
     * Called when the equipped item is right clicked.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        playerIn.setActiveHand(handIn);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

    public String getItemStackDisplayName(ItemStack stack) {
        return I18n.translateToLocal(
            PotionUtils.getPotionFromItem(stack)
                .getNamePrefixed("potion.effect."));
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (PotionType potiontype : PotionType.REGISTRY) {
                if (potiontype != PotionTypes.EMPTY) {
                    items.add(PotionUtils.addPotionToItemStack(new ItemStack(this), potiontype));
                }
            }
        }
    }

    /**
     * Returns true if this item has an enchantment glint. By default, this returns
     * <code>stack.isItemEnchanted()</code>, but other items can override it (for instance, written books always return
     * true).
     * <p>
     * Note that if you override this method, you generally want to also call the super version (on {@link Item}) to get
     * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
     */
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return super.hasEffect(stack) || !PotionUtils.getEffectsFromStack(stack)
            .isEmpty();
    }
}
