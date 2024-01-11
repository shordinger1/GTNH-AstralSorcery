package shordinger.wrapper.net.minecraft.item;

import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityArrow;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityTippedArrow;
import shordinger.wrapper.net.minecraft.world.World;

public class ItemArrow extends Item {

    public ItemArrow() {
        this.setCreativeTab(CreativeTabs.COMBAT);
    }

    public EntityArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase shooter) {
        EntityTippedArrow entitytippedarrow = new EntityTippedArrow(worldIn, shooter);
        entitytippedarrow.setPotionEffect(stack);
        return entitytippedarrow;
    }

    public boolean isInfinite(ItemStack stack, ItemStack bow, net.minecraft.entity.player.EntityPlayer player) {
        int enchant = net.minecraft.enchantment.EnchantmentHelper
            .getEnchantmentLevel(net.minecraft.init.Enchantments.INFINITY, bow);
        return enchant <= 0 ? false : this.getClass() == ItemArrow.class;
    }
}
