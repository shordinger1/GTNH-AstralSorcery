/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.common.Optional;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;

import shordinger.astralsorcery.common.base.Mods;
import shordinger.astralsorcery.common.data.config.entry.ConfigEntry;
import shordinger.astralsorcery.common.integrations.mods.crafttweaker.tweaks.GameStageTweaks;
import shordinger.astralsorcery.migration.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkLevelManager
 * Created by HellFirePvP
 * Date: 12.12.2016 / 00:33
 */
public class PerkLevelManager extends ConfigEntry {

    private static int LEVEL_CAP = 30;
    public static final PerkLevelManager INSTANCE = new PerkLevelManager();

    private Map<Integer, Long> totalExpLevelRequired = new HashMap<>();

    private PerkLevelManager() {
        super(Section.PERK_LEVELS, "level");
    }

    private void ensureLevels() {
        if (totalExpLevelRequired.isEmpty()) {
            for (int i = 1; i <= LEVEL_CAP; i++) {
                long prev = totalExpLevelRequired.getOrDefault(i - 1, 0L);
                totalExpLevelRequired.put(i, prev + 150L + (MathHelper.lfloor(Math.pow(2, (i / 2) + 3))));
            }
        }
    }

    public int getLevel(double totalExp, EntityPlayer player) {
        return getLevel(MathHelper.lfloor(totalExp), player);
    }

    private int getLevel(long totalExp, EntityPlayer player) {
        ensureLevels();

        if (totalExp <= 0) {
            return 1;
        }

        int levelCap = getLevelCapFor(player);

        for (int i = 1; i <= levelCap; i++) {
            if (totalExp < totalExpLevelRequired.getOrDefault(i, Long.MAX_VALUE)) {
                return i;
            }
        }
        return levelCap;
    }

    public long getExpForLevel(int level, EntityPlayer player) {
        ensureLevels();

        if (level <= 1) {
            return 0;
        }
        int levelCap = getLevelCapFor(player);

        if (level > levelCap) {
            level = levelCap;
        }
        return totalExpLevelRequired.get(level);
    }

    public float getNextLevelPercent(double totalExp, EntityPlayer player) {
        ensureLevels();

        int level = getLevel(totalExp, player);
        if (level >= LEVEL_CAP) {
            return 1F; // Done.
        }
        long nextLevel = this.totalExpLevelRequired.getOrDefault(level, 0L);
        long prevLevel = this.totalExpLevelRequired.getOrDefault(level - 1, 0L);
        return ((float) (totalExp - prevLevel)) / ((float) (nextLevel - prevLevel));
    }

    public static int getLevelCapFor(EntityPlayer player) {
        if (Mods.GAMESTAGES.isPresent() && Mods.CRAFTTWEAKER.isPresent()) {
            return resolveLevelCap(player);
        }
        return LEVEL_CAP;
    }

    @Optional.Method(modid = "gamestages")
    private static int resolveLevelCap(EntityPlayer player) {
        if (player == null) {
            return LEVEL_CAP;
        }
        int highestFound = -1;

        IStageData data = GameStageHelper.getPlayerData(player);
        if (data == null) {
            return LEVEL_CAP;
        }
        for (String stage : data.getStages()) {
            int cap = GameStageTweaks.getMaxCap(stage);
            if (cap > highestFound) {
                highestFound = cap;
            }
        }
        return highestFound > -1 ? highestFound : LEVEL_CAP;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        this.totalExpLevelRequired.clear();

        LEVEL_CAP = cfg.getInt(
            getKey() + "Cap",
            getConfigurationSection(),
            LEVEL_CAP,
            1,
            100,
            "Sets the max level for the perk tree levels.");
    }
}
