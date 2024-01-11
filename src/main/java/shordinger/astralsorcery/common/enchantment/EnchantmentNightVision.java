/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.enchantment;

import shordinger.wrapper.net.minecraft.enchantment.EnumEnchantmentType;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.MobEffects;
import shordinger.wrapper.net.minecraft.inventory.EntityEquipmentSlot;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.potion.PotionEffect;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EnchantmentNightVision
 * Created by HellFirePvP
 * Date: 18.03.2017 / 17:41
 */
public class EnchantmentNightVision extends EnchantmentPlayerWornTick {

    public EnchantmentNightVision() {
        super("as.nightvision", Rarity.VERY_RARE, EnumEnchantmentType.ARMOR_HEAD, EntityEquipmentSlot.HEAD);
    }

    @Override
    public void onWornTick(boolean isClient, EntityPlayer base, int level) {
        base.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, level - 1, true, false));
    }

    @Override
    public void onEntityDamaged(EntityLivingBase user, Entity target, int level) {
        if (target instanceof EntityLivingBase) {
            ((EntityLivingBase) target)
                .addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, level - 1, true, false));
        }
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return type.canEnchantItem(stack.getItem());
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
    }

}
