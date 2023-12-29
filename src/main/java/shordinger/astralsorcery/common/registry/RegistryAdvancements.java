/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.registry;

import static net.minecraft.advancements.CriteriaTriggers.register;
import static shordinger.astralsorcery.common.lib.AdvancementTriggers.*;

import shordinger.astralsorcery.common.advancements.*;
import shordinger.astralsorcery.common.advancements.AltarCraftTrigger;
import shordinger.astralsorcery.common.advancements.AttuneCrystalTrigger;
import shordinger.astralsorcery.common.advancements.AttuneSelfTrigger;
import shordinger.astralsorcery.common.advancements.DiscoverConstellationTrigger;
import shordinger.astralsorcery.common.advancements.PerkLevelTrigger;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryAdvancements
 * Created by HellFirePvP
 * Date: 27.10.2018 / 10:54
 */
public class RegistryAdvancements {

    public static void init() {
        register(DISCOVER_CONSTELLATION = new DiscoverConstellationTrigger());
        register(ATTUNE_SELF = new AttuneSelfTrigger());
        register(ATTUNE_CRYSTAL = new AttuneCrystalTrigger());
        register(ALTAR_CRAFT = new AltarCraftTrigger());
        register(PERK_LEVEL = new PerkLevelTrigger());
    }

}
