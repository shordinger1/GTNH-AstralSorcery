/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.enchantment;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

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
    public boolean canApply(ItemStack p_92089_1_) {
        return super.canApply(p_92089_1_);
    }

    @Override
    public int getMaxLevel() {
        return super.getMaxLevel();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
    }

}
