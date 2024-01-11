/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.registry;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.CommonProxy;
import shordinger.astralsorcery.common.enchantment.EnchantmentNightVision;
import shordinger.astralsorcery.common.enchantment.EnchantmentPlayerWornTick;
import shordinger.astralsorcery.common.enchantment.EnchantmentScorchingHeat;
import shordinger.wrapper.net.minecraft.enchantment.Enchantment;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

import java.util.LinkedList;
import java.util.List;

import static hellfirepvp.astralsorcery.common.lib.EnchantmentsAS.enchantmentNightVision;
import static hellfirepvp.astralsorcery.common.lib.EnchantmentsAS.enchantmentScorchingHeat;

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
