/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.enchantment;

import shordinger.wrapper.net.minecraft.enchantment.EnumEnchantmentType;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.inventory.EntityEquipmentSlot;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EnchantmentPlayerWornTick
 * Created by HellFirePvP
 * Date: 18.03.2017 / 17:41
 */
public abstract class EnchantmentPlayerWornTick extends EnchantmentBase {

    public EnchantmentPlayerWornTick(String name, Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot... slots) {
        super(name, rarityIn, typeIn, slots);
    }

    public void onWornTick(boolean isClient, EntityPlayer base, int level) {}

}
