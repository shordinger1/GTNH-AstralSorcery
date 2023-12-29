/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.data.config.entry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.Configuration;

import com.google.common.collect.Lists;

import shordinger.astralsorcery.AstralSorcery;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldGenEntry
 * Created by HellFirePvP
 * Date: 30.03.2017 / 10:36
 */
public class WorldGenEntry extends ConfigEntry {

    private int generationChance;
    private boolean doGenerate = false;
    private boolean doIgnoreBiomeSpecifications = false;
    private boolean doIgnoreDimensionSpecifications = true;
    private final BiomeDictionary.Type[] defaultBiomeTypes;
    private List<BiomeDictionary.Type> biomeTypes = new ArrayList<>();
    private List<Integer> applicableDimensions = new ArrayList<>();
    private int minY, maxY;

    private boolean loaded = false;

    public WorldGenEntry(String key, int defaultChance, boolean ignoreBiomeSpecifications,
                         BiomeDictionary.Type... applicableTypes) {
        super(Section.WORLDGEN, key);
        this.doIgnoreBiomeSpecifications = ignoreBiomeSpecifications;
        this.generationChance = defaultChance;
        this.defaultBiomeTypes = applicableTypes;
        this.minY = 0;
        this.maxY = 255;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        doGenerate = cfg.getBoolean("Generate", getConfigurationSection(), true, "Generate " + getKey());
        doIgnoreBiomeSpecifications = cfg.getBoolean(
            "IgnoreBiomes",
            getConfigurationSection(),
            this.doIgnoreBiomeSpecifications,
            "Ignore Biome specifications when trying to generate " + getKey());
        doIgnoreDimensionSpecifications = cfg.getBoolean(
            "IgnoreDimensionSettings",
            getConfigurationSection(),
            this.doIgnoreDimensionSpecifications,
            "Ignore dimension-whitelist when trying to generate " + getKey());
        generationChance = cfg.getInt(
            "Chance",
            getConfigurationSection(),
            this.generationChance,
            1,
            Integer.MAX_VALUE,
            "Chance to generate the structure in a chunk. The higher, the lower the chance.");
        minY = cfg.getInt(
            "MinY",
            getConfigurationSection(),
            this.minY,
            0,
            255,
            "Set the minimum Y level to spawn this structure on");
        maxY = cfg.getInt(
            "MaxY",
            getConfigurationSection(),
            this.maxY,
            0,
            255,
            "Set the maximum Y level to spawn this structure on");
        String[] strTypes = cfg.getStringList(
            "BiomeTypes",
            getConfigurationSection(),
            getDefaultBiomeTypes(),
            "Set the BiomeTypes (according to the BiomeDicitionary) this structure will spawn in.");
        List<BiomeDictionary.Type> resolvedTypes = new LinkedList<>();
        for (String s : strTypes) {
            try {
                resolvedTypes.add(BiomeDictionary.Type.getType(s));
            } catch (Exception e) {
                AstralSorcery.log.error(
                    "Could not find BiomeType by name '" + s
                        + "' - Ignoring BiomeType specification for structure "
                        + getKey());
            }
        }
        biomeTypes = Lists.newArrayList(resolvedTypes);
        String[] dimensionWhitelist = cfg.getStringList(
            "DimensionWhitelist",
            getConfigurationSection(),
            new String[0],
            "Define an array of dimensionID's where the structure is allowed to spawn in.");
        applicableDimensions = new ArrayList<>();
        for (String s : dimensionWhitelist) {
            try {
                applicableDimensions.add(Integer.parseInt(s));
            } catch (NumberFormatException exc) {
                AstralSorcery.log
                    .error("Could not add " + s + " to dimension whitelist for " + getKey() + " - It is not a number!");
            }
        }
        loaded = true;
    }

    private String[] getDefaultBiomeTypes() {
        String[] def = new String[defaultBiomeTypes.length];
        for (int i = 0; i < defaultBiomeTypes.length; i++) {
            BiomeDictionary.Type t = defaultBiomeTypes[i];
            def[i] = t.getName();
        }
        return def;
    }

    public void setMinY(int minY) {
        if (loaded) return;
        this.minY = minY;
    }

    public void setMaxY(int maxY) {
        if (loaded) return;
        this.maxY = maxY;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public List<BiomeDictionary.Type> getTypes() {
        return biomeTypes;
    }

    public List<Integer> getApplicableDimensions() {
        return applicableDimensions;
    }

    public boolean shouldIgnoreDimensionSpecifications() {
        return doIgnoreDimensionSpecifications;
    }

    public boolean shouldGenerate() {
        return doGenerate;
    }

    public boolean shouldIgnoreBiomeSpecifications() {
        return doIgnoreBiomeSpecifications;
    }

    public boolean tryGenerate(Random random, double chanceMultiplier) {
        return random.nextInt(Math.max((int) Math.round(generationChance * chanceMultiplier), 1)) == 0;
    }

}
