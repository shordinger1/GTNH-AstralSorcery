package shordinger.wrapper.net.minecraft.item;

import java.util.List;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.util.ITooltipFlag;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityArrow;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityTippedArrow;
import shordinger.wrapper.net.minecraft.init.PotionTypes;
import shordinger.wrapper.net.minecraft.potion.PotionType;
import shordinger.wrapper.net.minecraft.potion.PotionUtils;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.util.text.translation.I18n;
import shordinger.wrapper.net.minecraft.world.World;

public class ItemTippedArrow extends ItemArrow {

    @SideOnly(Side.CLIENT)
    public ItemStack getDefaultInstance() {
        return PotionUtils.addPotionToItemStack(super.getDefaultInstance(), PotionTypes.POISON);
    }

    public EntityArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase shooter) {
        EntityTippedArrow entitytippedarrow = new EntityTippedArrow(worldIn, shooter);
        entitytippedarrow.setPotionEffect(stack);
        return entitytippedarrow;
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (PotionType potiontype : PotionType.REGISTRY) {
                if (!potiontype.getEffects()
                    .isEmpty()) {
                    items.add(PotionUtils.addPotionToItemStack(new ItemStack(this), potiontype));
                }
            }
        }
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        PotionUtils.addPotionTooltip(stack, tooltip, 0.125F);
    }

    public String getItemStackDisplayName(ItemStack stack) {
        return I18n.translateToLocal(
            PotionUtils.getPotionFromItem(stack)
                .getNamePrefixed("tipped_arrow.effect."));
    }
}
