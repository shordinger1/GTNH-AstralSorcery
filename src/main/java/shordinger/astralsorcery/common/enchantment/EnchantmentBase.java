/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.EnumRarity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EnchantmentBase
 * Created by HellFirePvP
 * Date: 05.05.2017 / 15:05
 */
public class EnchantmentBase extends Enchantment {

    protected EnchantmentBase(String unlocName, EnumRarity rarityIn, EnumEnchantmentType typeIn,
                              EntityEquipmentSlot... slots) {
        super(rarityIn, typeIn, slots);
        setName(unlocName);
    }

}
