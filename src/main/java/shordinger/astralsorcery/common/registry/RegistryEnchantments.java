/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.registry;

import static shordinger.astralsorcery.common.lib.EnchantmentsAS.enchantmentNightVision;
import static shordinger.astralsorcery.common.lib.EnchantmentsAS.enchantmentScorchingHeat;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.CommonProxy;
import shordinger.astralsorcery.common.enchantment.EnchantmentNightVision;
import shordinger.astralsorcery.common.enchantment.EnchantmentPlayerWornTick;
import shordinger.astralsorcery.common.enchantment.EnchantmentScorchingHeat;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryEnchantments
 * Created by HellFirePvP
 * Date: 18.03.2017 / 19:58
 */
public class RegistryEnchantments {

    public static List<EnchantmentPlayerWornTick> wearableTickEnchantments = new LinkedList<>();

    public static void init() {
        enchantmentNightVision = register(new EnchantmentNightVision());
        enchantmentScorchingHeat = register(new EnchantmentScorchingHeat());
    }

    private static <T extends Enchantment> T register(T e) {
        e.setRegistryName(new ResourceLocation(AstralSorcery.MODID, e.getName()));
        CommonProxy.registryPrimer.register(e);
        if (e instanceof EnchantmentPlayerWornTick) {
            wearableTickEnchantments.add((EnchantmentPlayerWornTick) e);
        }
        return e;
    }

}
