/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.enchantment;

import shordinger.wrapper.net.minecraft.enchantment.EnumEnchantmentType;
import shordinger.wrapper.net.minecraft.inventory.EntityEquipmentSlot;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EnchantmentScorchingHeat
 * Created by HellFirePvP
 * Date: 05.05.2017 / 15:04
 */
public class EnchantmentScorchingHeat extends EnchantmentBase {

    public EnchantmentScorchingHeat() {
        super("as.smelting", Rarity.VERY_RARE, EnumEnchantmentType.DIGGER, EntityEquipmentSlot.MAINHAND);
    }
}
