/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.crafttweaker.tweaks;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import shordinger.astralsorcery.common.constellation.perk.AbstractPerk;
import shordinger.astralsorcery.common.event.APIRegistryEvent;
import shordinger.astralsorcery.common.integrations.mods.crafttweaker.BaseTweaker;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTree
 * Created by HellFirePvP
 * Date: 19.08.2018 / 21:49
 */
@ZenClass("mods.astralsorcery.PerkTree")
public class PerkTree extends BaseTweaker {

    private static List<String> removedPerks = Lists.newLinkedList();
    private static List<String> disabledPerks = Lists.newLinkedList();
    private static Map<String, Double> perkModifiers = Maps.newHashMap();

    @ZenMethod
    public static void disablePerk(String perkRegistryName) {
        disabledPerks.add(perkRegistryName);
    }

    @ZenMethod
    public static void removePerk(String perkRegistryName) {
        removedPerks.add(perkRegistryName);
    }

    @ZenMethod
    public static void modifyPerk(String perkRegistryName, double multiplier) {
        perkModifiers.put(perkRegistryName, multiplier);
    }

    @SubscribeEvent
    public void onPerkRemoval(APIRegistryEvent.PerkPostRemove event) {
        if (removedPerks.contains(
            event.getPerk()
                .getRegistryName()
                .toString())) {
            event.setRemoved(true);
        }
    }

    @SubscribeEvent
    public void onPerkDisable(APIRegistryEvent.PerkDisable event) {
        if (disabledPerks.contains(
            event.getPerk()
                .getRegistryName()
                .toString())) {
            event.setPerkDisabled(true);
        }
    }

    public static double getMultiplier(AbstractPerk perk) {
        return perkModifiers.getOrDefault(
            perk.getRegistryName()
                .toString(),
            1.0D);
    }

}
