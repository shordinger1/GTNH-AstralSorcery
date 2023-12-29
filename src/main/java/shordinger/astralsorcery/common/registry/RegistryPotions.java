/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.registry;

import net.minecraft.potion.Potion;

import shordinger.astralsorcery.common.CommonProxy;
import shordinger.astralsorcery.common.potion.*;
import shordinger.astralsorcery.common.potion.PotionBleed;
import shordinger.astralsorcery.common.potion.PotionCheatDeath;
import shordinger.astralsorcery.common.potion.PotionDropModifier;
import shordinger.astralsorcery.common.potion.PotionSpellPlague;
import shordinger.astralsorcery.common.potion.PotionTimeFreeze;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryPotions
 * Created by HellFirePvP
 * Date: 13.11.2016 / 01:32
 */
public class RegistryPotions {

    public static PotionCheatDeath potionCheatDeath;
    public static PotionBleed potionBleed;
    public static PotionSpellPlague potionSpellPlague;
    public static PotionDropModifier potionDropModifier;
    public static PotionTimeFreeze potionTimeFreeze;

    public static void init() {
        potionCheatDeath = registerPotion(new PotionCheatDeath());
        potionBleed = registerPotion(new PotionBleed());
        potionSpellPlague = registerPotion(new PotionSpellPlague());
        potionDropModifier = registerPotion(new PotionDropModifier());
        potionTimeFreeze = registerPotion(new PotionTimeFreeze());
    }

    private static <T extends Potion> T registerPotion(T potion) {
        potion.setRegistryName(
            potion.getClass()
                .getSimpleName());
        CommonProxy.registryPrimer.register(potion);
        return potion;
    }

}
