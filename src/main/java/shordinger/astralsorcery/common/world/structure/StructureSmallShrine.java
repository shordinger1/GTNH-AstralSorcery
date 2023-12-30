/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.world.structure;

import java.util.Collection;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.Configuration;

import shordinger.astralsorcery.common.data.world.data.StructureGenBuffer;
import shordinger.astralsorcery.common.lib.MultiBlockArrays;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.IBlockState;
import shordinger.astralsorcery.migration.WorldHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureSmallShrine
 * Created by HellFirePvP
 * Date: 07.01.2017 / 16:58
 */
public class StructureSmallShrine extends WorldGenAttributeStructure {

    private int heightThreshold = 1;

    public StructureSmallShrine() {
        super(
            0,
            "smallShrine",
            () -> MultiBlockArrays.smallShrine,
            StructureGenBuffer.StructureType.SMALL,
            false,
            BiomeDictionary.Type.PLAINS,
            BiomeDictionary.Type.FOREST);
        this.idealDistance = 512F;
    }

    @Override
    public void generate(BlockPos pos, World world, Random rand) {
        generateAsSubmergedStructure(world, pos);
        getBuffer(world).markStructureGeneration(pos, getStructureType());
    }

    @Override
    public boolean fulfillsSpecificConditions(BlockPos pos, World world, Random random) {
        if (!isApplicableWorld(world)) return false;
        if (!isApplicableBiome(world, pos)) return false;
        if (canSpawnShrineCorner(world, pos.add(-4, 0, 4))) return false;
        if (canSpawnShrineCorner(world, pos.add(4, 0, -4))) return false;
        if (canSpawnShrineCorner(world, pos.add(4, 0, 4))) return false;
        return !canSpawnShrineCorner(world, pos.add(-4, 0, -4));
    }

    private boolean canSpawnShrineCorner(World world, BlockPos pos) {
        int dY = world.getTopSolidOrLiquidBlock(pos)
            .getY();
        if (dY >= cfgEntry.getMinY() && dY <= cfgEntry.getMaxY()
            && Math.abs(dY - pos.getY()) <= heightThreshold
            && isApplicableBiome(world, pos)) {
            IBlockState state = WorldHelper.getBlockState(world, new BlockPos(pos.getX(), dY - 1, pos.getZ()));
            return state.getMaterial()
                .isLiquid() || !state.getMaterial()
                .isOpaque();
        }
        return true;
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

    private boolean isApplicableBiome(World world, BlockPos pos) {
        if (cfgEntry.shouldIgnoreBiomeSpecifications()) return true;

        Biome b = world.getBiome(pos);
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

    @Override
    public BlockPos getGenerationPosition(int chX, int chZ, World world, Random rand) {
        int rX = (chX * 16) + rand.nextInt(16) + 8;
        int rZ = (chZ * 16) + rand.nextInt(16) + 8;
        int rY = world.getTopSolidOrLiquidBlock(new BlockPos(rX, 0, rZ))
            .getY();
        return new BlockPos(rX, rY, rZ);
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
