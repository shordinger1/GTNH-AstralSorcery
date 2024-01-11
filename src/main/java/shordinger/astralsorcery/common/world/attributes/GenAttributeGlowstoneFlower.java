/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.world.attributes;

import java.util.Collection;
import java.util.Random;

import shordinger.astralsorcery.common.block.BlockCustomFlower;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.world.WorldGenAttributeCommon;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.biome.Biome;
import shordinger.wrapper.net.minecraftforge.common.BiomeDictionary;
import shordinger.wrapper.net.minecraftforge.common.config.Configuration;

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

        Biome b = world.getBiome(pos);
        Collection<BiomeDictionary.Type> types = BiomeDictionary.getTypes(b);
        if (types.isEmpty()) return false;
        boolean applicable = false;
        for (BiomeDictionary.Type t : types) {
            if (cfgEntry.getTypes()
                .contains(t)) applicable = true;
        }
        return applicable;
    }

    private boolean isApplicableWorld(World world) {
        if (cfgEntry.shouldIgnoreDimensionSpecifications()) return true;

        Integer dimId = world.provider.getDimension();
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
                        tryGenerateAtPosition(randomOffset(world, pos, rand, 7), world, rand);
                    }
                }
            } finally {
                isGeneratingAdditional = false;
            }
        }
    }

    private BlockPos randomOffset(World world, BlockPos origin, Random random, int offsetRand) {
        int rX = origin.getX() - offsetRand + random.nextInt(offsetRand * 2 + 1);
        int rZ = origin.getZ() - offsetRand + random.nextInt(offsetRand * 2 + 1);
        int rY = world.getPrecipitationHeight(new BlockPos(rX, 0, rZ))
            .getY();
        return new BlockPos(rX, rY, rZ);
    }

    @Override
    public boolean fulfillsSpecificConditions(BlockPos pos, World world, Random random) {
        return isApplicableBiome(world, pos) && isApplicableWorld(world)
            && pos.getY() >= cfgEntry.getMinY()
            && pos.getY() <= cfgEntry.getMaxY()
            && world.getBlockState(pos.down())
            .isSideSolid(world, pos.down(), EnumFacing.UP)
            && (ignoreSnowCondition || world.canSnowAt(pos, false));
    }

    @Override
    public BlockPos getGenerationPosition(int chX, int chZ, World world, Random rand) {
        int rX = (chX * 16) + rand.nextInt(16) + 8;
        int rZ = (chZ * 16) + rand.nextInt(16) + 8;
        int rY = world.getPrecipitationHeight(new BlockPos(rX, 0, rZ))
            .getY();
        return new BlockPos(rX, rY, rZ);
    }

}
