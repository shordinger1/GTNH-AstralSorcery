/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.enchantment.amulet.registry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.data.config.ConfigDataAdapter;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WeightedAmuletEnchantment
 * Created by HellFirePvP
 * Date: 27.01.2018 / 17:44
 */
public class WeightedAmuletEnchantment extends WeightedRandom.Item implements ConfigDataAdapter.DataSet {

    private final Enchantment enchantment;

    public WeightedAmuletEnchantment(Enchantment ench, int weight) {
        super(weight);
        this.enchantment = ench;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    @Nonnull
    @Override
    public String serialize() {
        return this.enchantment.getName() + ":" + itemWeight;
    }

    @Nullable
    public static WeightedAmuletEnchantment deserialize(String str) {
        String[] spl = str.split(":");
        if (spl.length < 3) {
            return null;
        }
        String domain = spl[0];
        String weight = spl[spl.length - 1];
        StringBuilder path = new StringBuilder();
        for (int i = 1; i < spl.length - 1; i++) {
            if (path.length() > 0) {
                path.append(":"); // Cause that vanishes when splitting...
            }
            path.append(spl[i]);
        }
        // TODO find a better solution than hardcoding (duh)
        String registryName = domain + ":" + path.toString();
        if (registryName.equalsIgnoreCase("cofhcore:holding")) {
            AstralSorcery.log.info("Ignoring amulet enchantment 'cofhcore:holding' as it's prone to cause issues.");
            return null;
        }
        // see #1302
        if (domain.equalsIgnoreCase("dungeontactics")) {
            AstralSorcery.log.info(
                "Ignoring amulet enchantments for '" + registryName
                    + "' as dungeontactic's enchantments generated through the prism are prone to cause issues.");
            return null;
        }
        Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(registryName));
        if (ench == null) {
            AstralSorcery.log
                .info("Ignoring whitelist entry " + str + " for amulet enchantments - Enchantment does not exist!");
            return null;
        }
        int w;
        try {
            w = Integer.parseInt(weight);
        } catch (NumberFormatException exc) {
            AstralSorcery.log.info(
                "Ignoring whitelist entry " + str
                    + " for amulet enchantments - last :-separated argument is not a number!");
            return null;
        }
        return new WeightedAmuletEnchantment(ench, w);
    }

}
