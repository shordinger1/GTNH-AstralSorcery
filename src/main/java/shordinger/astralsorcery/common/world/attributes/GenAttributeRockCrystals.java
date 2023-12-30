/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.world.attributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.Configuration;

import com.google.common.collect.Lists;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.base.RockCrystalHandler;
import shordinger.astralsorcery.common.block.BlockCustomOre;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.data.config.entry.ConfigEntry;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.world.WorldGenAttribute;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.IBlockState;
import shordinger.astralsorcery.migration.WorldHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GenAttributeRockCrystals
 * Created by HellFirePvP
 * Date: 12.01.2017 / 21:52
 */
public class GenAttributeRockCrystals extends WorldGenAttribute {

    private boolean doGenerate = false;
    private boolean doIgnoreBiomeSpecifications = true;
    private boolean doIgnoreDimensionSpecifications = true;
    private List<BiomeDictionary.Type> biomeTypes = new ArrayList<>();
    private List<Integer> applicableDimensions = new ArrayList<>();
    private int crystalDensity = 15;

    private List<IBlockState> replaceableStates = null;
    private List<String> replaceableStatesSerialized = new ArrayList<>(); // Delay resolving states to a later state...

    public GenAttributeRockCrystals() {
        super(0);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.WORLDGEN, "rockcrystals") {

            @Override
            public void loadFromConfig(Configuration cfg) {
                doGenerate = cfg.getBoolean("Generate", getConfigurationSection(), true, "Generate " + getKey());
                doIgnoreBiomeSpecifications = cfg.getBoolean(
                    "IgnoreBiomeSpecification",
                    getConfigurationSection(),
                    doIgnoreBiomeSpecifications,
                    "Ignore Biome specifications when trying to generate " + getKey());
                doIgnoreDimensionSpecifications = cfg.getBoolean(
                    "IgnoreDimensionSettings",
                    getConfigurationSection(),
                    doIgnoreDimensionSpecifications,
                    "Ignore dimension-whitelist when trying to generate " + getKey());
                crystalDensity = cfg.getInt(
                    "CrystalDensity",
                    getConfigurationSection(),
                    crystalDensity,
                    1,
                    40,
                    "Defines how rarely Rock crystal ores spawn. The higher, the more rare.");
                String[] strTypes = cfg.getStringList(
                    "BiomeTypes",
                    getConfigurationSection(),
                    new String[0],
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
                        AstralSorcery.log.error(
                            "Could not add " + s
                                + " to dimension whitelist for "
                                + getKey()
                                + " - It is not a number!");
                    }
                }
                String[] applicableReplacements = cfg.getStringList(
                    "ReplacementStates",
                    getConfigurationSection(),
                    new String[]{"minecraft:stone:0"},
                    "Defines the blockstates that may be replaced by rock crystal ore when trying to generate a rock crystal ore. format: <modid>:<name>:<meta> - Use meta -1 for wildcard");
                replaceableStatesSerialized = Arrays.asList(applicableReplacements);
            }
        });
    }

    private boolean isApplicableWorld(World world) {
        if (this.doIgnoreDimensionSpecifications) return true;

        Integer dimId = world.provider.dimensionId;
        if (this.applicableDimensions.isEmpty()) return false;
        for (Integer dim : this.applicableDimensions) {
            if (dim.equals(dimId)) return true;
        }
        return false;
    }

    private boolean fitsBiome(World world, BlockPos pos) {
        if (this.doIgnoreBiomeSpecifications) return true;

        Biome b = world.getBiome(pos);
        Collection<BiomeDictionary.Type> types = BiomeDictionary.getTypes(b);
        if (types.isEmpty()) return false;
        boolean applicable = false;
        for (BiomeDictionary.Type t : types) {
            if (biomeTypes.contains(t)) {
                applicable = true;
                break;
            }
        }
        return applicable;
    }

    private void resolveReplaceableStates() {
        replaceableStates = new LinkedList<>();
        for (String stateStr : replaceableStatesSerialized) {
            String[] spl = stateStr.split(":");
            if (spl.length != 3) {
                AstralSorcery.log.info("Skipping invalid replacement state: " + stateStr);
                continue;
            }
            String strMeta = spl[2];
            Integer meta;
            try {
                meta = Integer.parseInt(strMeta);
            } catch (NumberFormatException exc) {
                AstralSorcery.log
                    .error("Skipping invalid replacement state: " + stateStr + " - Its 'meta' is not a number!");
                continue;
            }
            Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(spl[0], spl[1]));
            if (b == null || b == Blocks.air) {
                AstralSorcery.log
                    .error("Skipping invalid replacement state: " + stateStr + " - The block does not exist!");
                continue;
            }
            if (meta == -1) {
                replaceableStates.addAll(
                    b.getBlockState()
                        .getValidStates());
            } else {
                replaceableStates.add(b.getStateFromMeta(meta));
            }
        }
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world) {
        if (replaceableStates == null) {
            resolveReplaceableStates();
        }

        if (doGenerate && random.nextInt(crystalDensity) == 0) {
            int xPos = chunkX * 16 + random.nextInt(16) + 8;
            int zPos = chunkZ * 16 + random.nextInt(16) + 8;
            int yPos = 2 + random.nextInt(4);
            BlockPos pos = new BlockPos(xPos, yPos, zPos);
            if (!fitsBiome(world, pos) || !isApplicableWorld(world)) return;
            IBlockState state = WorldHelper.getBlockState(world, pos);
            if (MiscUtils.getMatchingState(replaceableStates, state) != null) {
                IBlockState newState = BlocksAS.customOre.getDefaultState()
                    .withProperty(BlockCustomOre.ORE_TYPE, BlockCustomOre.OreType.ROCK_CRYSTAL);
                if (!world.setBlockState(pos, newState)) {
                    return;
                }

                RockCrystalHandler.INSTANCE.addOre(world, pos, true);

                if (random.nextInt(4) == 0) {
                    pos = pos.add(random.nextInt(2), random.nextInt(2), random.nextInt(2));
                    state = WorldHelper.getBlockState(world, pos);
                    if (MiscUtils.getMatchingState(replaceableStates, state) != null) {
                        if (world.setBlockState(pos, newState)) {
                            RockCrystalHandler.INSTANCE.addOre(world, pos, true);
                        }
                    }
                }
            }
        }
    }
}
