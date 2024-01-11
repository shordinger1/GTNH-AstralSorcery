package shordinger.wrapper.net.minecraft.enchantment;

import java.util.Random;

import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.init.Enchantments;
import shordinger.wrapper.net.minecraft.inventory.EntityEquipmentSlot;
import shordinger.wrapper.net.minecraft.item.ItemArmor;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.DamageSource;

public class EnchantmentThorns extends Enchantment {

    public EnchantmentThorns(Enchantment.Rarity rarityIn, EntityEquipmentSlot... slots) {
        super(rarityIn, EnumEnchantmentType.ARMOR_CHEST, slots);
        this.setName("thorns");
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(int enchantmentLevel) {
        return 10 + 20 * (enchantmentLevel - 1);
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(int enchantmentLevel) {
        return super.getMinEnchantability(enchantmentLevel) + 50;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel() {
        return 3;
    }

    /**
     * Determines if this enchantment can be applied to a specific ItemStack.
     */
    public boolean canApply(ItemStack stack) {
        return stack.getItem() instanceof ItemArmor ? true : super.canApply(stack);
    }

    /**
     * Whenever an entity that has this enchantment on one of its associated items is damaged this method will be
     * called.
     */
    public void onUserHurt(EntityLivingBase user, Entity attacker, int level) {
        Random random = user.getRNG();
        ItemStack itemstack = EnchantmentHelper.getEnchantedItem(Enchantments.THORNS, user);

        if (shouldHit(level, random)) {
            if (attacker != null) {
                attacker.attackEntityFrom(DamageSource.causeThornsDamage(user), (float) getDamage(level, random));
            }

            if (!itemstack.isEmpty()) {
                damageArmor(itemstack, 3, user);
            }
        } else if (!itemstack.isEmpty()) {
            damageArmor(itemstack, 1, user);
        }
    }

    public static boolean shouldHit(int level, Random rnd) {
        if (level <= 0) {
            return false;
        } else {
            return rnd.nextFloat() < 0.15F * (float) level;
        }
    }

    public static int getDamage(int level, Random rnd) {
        return level > 10 ? level - 10 : 1 + rnd.nextInt(4);
    }

    private void damageArmor(ItemStack stack, int amount, EntityLivingBase entity) {
        int slot = -1;
        int x = 0;
        for (ItemStack i : entity.getArmorInventoryList()) {
            if (i == stack) {
                slot = x;
                break;
            }
            x++;
        }
        if (slot == -1 || !(stack.getItem() instanceof net.minecraftforge.common.ISpecialArmor)) {
            stack.damageItem(1, entity);
            return;
        }
        net.minecraftforge.common.ISpecialArmor armor = (net.minecraftforge.common.ISpecialArmor) stack.getItem();
        armor.damageArmor(entity, stack, DamageSource.causeThornsDamage(entity), amount, slot);
    }
}
