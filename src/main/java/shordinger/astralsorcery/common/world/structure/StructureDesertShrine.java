/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.world.structure;

import java.util.Collection;
import java.util.Random;

import shordinger.astralsorcery.common.data.world.data.StructureGenBuffer;
import shordinger.astralsorcery.common.lib.MultiBlockArrays;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.biome.Biome;
import shordinger.wrapper.net.minecraftforge.common.BiomeDictionary;
import shordinger.wrapper.net.minecraftforge.common.config.Configuration;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureDesertShrine
 * Created by HellFirePvP
 * Date: 21.10.2016 / 13:43
 */
public class StructureDesertShrine extends WorldGenAttributeStructure {

    private int heightThreshold = 3;

    public StructureDesertShrine() {
        super(
            0,
            "desertStructure",
            () -> MultiBlockArrays.desertShrine,
            StructureGenBuffer.StructureType.DESERT,
            false,
            BiomeDictionary.Type.SANDY);
        this.idealDistance = 1024F;
    }

    @Override
    public void generate(BlockPos pos, World world, Random rand) {
        generateAsSubmergedStructure(world, pos);
        getBuffer(world).markStructureGeneration(pos, getStructureType());
    }

    @Override
    public boolean fulfillsSpecificConditions(BlockPos pos, World world, Random random) {
        if (!isApplicableWorld(world)) return false;
        if (!isDesertBiome(world, pos)) return false;
        if (!canSpawnShrineCorner(world, pos.add(-4, 0, 4))) return false;
        if (!canSpawnShrineCorner(world, pos.add(4, 0, -4))) return false;
        if (!canSpawnShrineCorner(world, pos.add(4, 0, 4))) return false;
        if (!canSpawnShrineCorner(world, pos.add(-4, 0, -4))) return false;
        return true;
    }

    @Override
    public BlockPos getGenerationPosition(int chX, int chZ, World world, Random rand) {
        int rX = (chX * 16) + rand.nextInt(16) + 8;
        int rZ = (chZ * 16) + rand.nextInt(16) + 8;
        int rY = world.getTopSolidOrLiquidBlock(new BlockPos(rX, 0, rZ))
            .getY();
        return new BlockPos(rX, rY, rZ);
    }

    private boolean canSpawnShrineCorner(World world, BlockPos pos) {
        int dY = world.getTopSolidOrLiquidBlock(pos)
            .getY();
        if (dY >= cfgEntry.getMinY() && dY <= cfgEntry.getMaxY()
            && Math.abs(dY - pos.getY()) <= heightThreshold
            && isDesertBiome(world, pos)) {
            IBlockState state = world.getBlockState(new BlockPos(pos.getX(), dY - 1, pos.getZ()));
            return !state.getMaterial()
                .isLiquid() && state.getMaterial()
                .isOpaque();
        }
        return false;
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

    private boolean isDesertBiome(World world, BlockPos pos) {
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

    @Override
    protected void loadAdditionalConfigEntries(Configuration cfg) {
        super.loadAdditionalConfigEntries(cfg);

        heightThreshold = cfg.getInt(
            "heightThreshold",
            cfgEntry.getConfigurationSection(),
            heightThreshold,
            1,
            32,
            "Defines how high/low the surface in comparison to the structure can be to be seen as 'sufficiently flat' for the structure to spawn at the given position.");
    }

}
