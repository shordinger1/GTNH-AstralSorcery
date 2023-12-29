/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util;

import net.minecraft.util.ResourceLocation;

import com.google.common.collect.ImmutableSet;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import shordinger.astralsorcery.Tags;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.lib.ItemsAS;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LootTableUtil
 * Created by HellFirePvP
 * Date: 01.08.2016 / 19:29
 */
public class LootTableUtil {

    public static final ResourceLocation LOOT_TABLE_SHRINE = new ResourceLocation(
        Tags.MODID.toLowerCase(),
        "chest_shrine");
    public static final ResourceLocation LOOT_TABLE_SHOOTING_STAR = new ResourceLocation(
        Tags.MODID.toLowerCase(),
        "shooting_star");

    private static final ImmutableSet<ResourceLocation> constellationPaperTables = ImmutableSet.of(
        LootTableList.CHESTS_STRONGHOLD_LIBRARY,
        LootTableList.CHESTS_ABANDONED_MINESHAFT,
        LootTableList.CHESTS_JUNGLE_TEMPLE,
        LootTableList.CHESTS_DESERT_PYRAMID,
        LootTableList.CHESTS_IGLOO_CHEST);

    public static void initLootTable() {
        LootTableList.register(LOOT_TABLE_SHRINE);
        LootTableList.register(LOOT_TABLE_SHOOTING_STAR);
    }

    @SubscribeEvent
    public void onLootLoad(LootTableLoadEvent event) {
        ResourceLocation name = event.getName();
        if (constellationPaperTables.contains(name)) {
            event.getTable()
                .getPool("main")
                .addEntry(
                    new LootEntryItem(
                        ItemsAS.constellationPaper,
                        Config.constellationPaperRarity,
                        Config.constellationPaperQuality,
                        new LootFunction[0],
                        new LootCondition[0],
                        "astralsorcery:constellation_paper"));
        }
    }

}
