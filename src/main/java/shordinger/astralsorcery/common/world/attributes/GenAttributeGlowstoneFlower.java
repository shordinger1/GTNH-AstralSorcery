/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.world.attributes;

import java.util.Collection;
import java.util.Random;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.Configuration;

import shordinger.astralsorcery.common.block.BlockCustomFlower;
import shordinger.astralsorcery.common.item.tool.sextant.SextantFinder;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.world.WorldGenAttributeCommon;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.WorldHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GenAttributeGlowstoneFlower
 * Created by HellFirePvP
 * Date: 30.03.2017 / 09:04
 */
public class GenAttributeGlowstoneFlower extends WorldGenAttributeCommon {

    private static boolean isGeneratingAdditional = false;
    private boolean ignoreSnowCondition = false;

    public GenAttributeGlowstoneFlower() {
        super(1, 2, "glowstone_flower", false, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.COLD);
    }

    private boolean isApplicableBiome(World world, BlockPos pos) {
        if (cfgEntry.shouldIgnoreBiomeSpecifications()) return true;

        SextantFinder.Biome b = world.getBiome(pos);
        Collection<BiomeDictionary.Type> types = BiomeDictionary.getTypes(b);
        if (types.isEmpty()) return false;
        boolean applicable = false;
        for (BiomeDictionary.Type t : types) {
            if (cfgEntry.getTypes()
                .contains(t)) {
                applicable = true;
                break;
            }
        }
        return applicable;
    }

    private boolean isApplicableWorld(World world) {
        if (cfgEntry.shouldIgnoreDimensionSpecifications()) return true;

        Integer dimId = world.provider.dimensionId;
        if (cfgEntry.getApplicableDimensions()
            .isEmpty()) return false;
        for (Integer dim : cfgEntry.getApplicableDimensions()) {
            if (dim.equals(dimId)) return true;
        }
        return false;
    }

    @Override
    protected void loadAdditionalConfigEntries(Configuration cfg) {
        ignoreSnowCondition = cfg.getBoolean(
            "ignoreSnowCondition",
            cfgEntry.getConfigurationSection(),
            false,
            "Set this to true and the decorator will ignore the spawn-condition if snow is/can fall in the area.");
    }

    @Override
    public void generate(BlockPos pos, World world, Random rand) {
        if (!world.setBlockState(
            pos,
            BlocksAS.customFlower.getDefaultState()
                .withProperty(BlockCustomFlower.FLOWER_TYPE, BlockCustomFlower.FlowerType.GLOW_FLOWER))) {
            return;
        }

        if (!isGeneratingAdditional) {
            isGeneratingAdditional = true;
            try {
                for (int i = 0; i < 8; i++) {
                    if (rand.nextInt(4) == 0) {
                        tryGenerateAtPosition(randomOffset(world, pos, rand), world, rand);
                    }
                }
            } finally {
                isGeneratingAdditional = false;
            }
        }
    }

    private BlockPos randomOffset(World world, BlockPos origin, Random random) {
        int rX = origin.getX() - 7 + random.nextInt(7 * 2 + 1);
        int rZ = origin.getZ() - 7 + random.nextInt(7 * 2 + 1);
        int rY = world.getPrecipitationHeight(rX, rZ);
        return new BlockPos(rX, rY, rZ);
    }

    @Override
    public boolean fulfillsSpecificConditions(BlockPos pos, World world, Random random) {
        return isApplicableBiome(world, pos) && isApplicableWorld(world)
            && pos.getY() >= cfgEntry.getMinY()
            && pos.getY() <= cfgEntry.getMaxY()
            && WorldHelper.getBlockState(world, pos.down())
            .isSideSolid(world, pos.down(), EnumFacing.UP)
            && (ignoreSnowCondition || world.canSnowAt(pos, false));
    }

    @Override
    public BlockPos getGenerationPosition(int chX, int chZ, World world, Random rand) {
        int rX = (chX * 16) + rand.nextInt(16) + 8;
        int rZ = (chZ * 16) + rand.nextInt(16) + 8;
        int rY = world.getPrecipitationHeight(rX, rZ);
        return new BlockPos(rX, rY, rZ);
    }

}
