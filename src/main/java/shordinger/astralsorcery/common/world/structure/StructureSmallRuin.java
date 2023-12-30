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

import javax.annotation.Nullable;

import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.Configuration;

import shordinger.astralsorcery.common.data.world.data.StructureGenBuffer;
import shordinger.astralsorcery.common.item.tool.sextant.SextantFinder;
import shordinger.astralsorcery.common.lib.MultiBlockArrays;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.IBlockState;
import shordinger.astralsorcery.migration.WorldHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureSmallRuin
 * Created by HellFirePvP
 * Date: 25.01.2018 / 20:06
 */
public class StructureSmallRuin extends WorldGenAttributeStructure {

    private int heightThreshold = 0;

    public StructureSmallRuin() {
        super(3, "smallRuin", () -> MultiBlockArrays.smallRuin, StructureGenBuffer.StructureType.SMALL_RUIN, true);
        this.idealDistance = 2048F;
    }

    @Override
    public void generate(BlockPos pos, World world, Random rand) {
        generateAsSubmergedStructure(world, pos);
        getBuffer(world).markStructureGeneration(pos, getStructureType());
    }

    @Override
    public boolean fulfillsSpecificConditions(BlockPos pos, World world, Random random) {
        if (!isApplicableWorld(world)) return false;
        if (isApplicableBiome(world, pos)) return false;
        if (canSpawnPosition(world, pos.add(-1, 0, 5))) return false;
        if (canSpawnPosition(world, pos.add(1, 0, -5))) return false;
        if (canSpawnPosition(world, pos.add(1, 0, 5))) return false;
        if (canSpawnPosition(world, pos.add(-1, 0, -5))) return false;
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
        if (cfgEntry.shouldIgnoreBiomeSpecifications()) return false;

        SextantFinder.Biome b = world.getBiomeGenForCoords(pos.getX(), pos.getZ());
        Collection<BiomeDictionary.Type> types = BiomeDictionary.getTypes(b);
        if (types.isEmpty()) return true;
        boolean applicable = false;
        for (BiomeDictionary.Type t : types) {
            if (cfgEntry.getTypes()
                .contains(t)) applicable = true;
        }
        return !applicable;
    }

    private boolean canSpawnPosition(World world, BlockPos pos) {
        int dY = world.getTopSolidOrLiquidBlock(pos)
            .getY();
        if (dY >= cfgEntry.getMinY() && dY <= cfgEntry.getMaxY() && Math.abs(dY - pos.getY()) <= heightThreshold) {
            IBlockState at = WorldHelper.getBlockState(world, new BlockPos(pos.getX(), dY - 1, pos.getZ()));
            return at.getMaterial()
                .isLiquid() || !at.getMaterial()
                .isOpaque()
                || isApplicableBiome(world, pos);
        }
        return true;
    }

    @Nullable
    @Override
    public BlockPos getGenerationPosition(int chX, int chZ, World world, Random rand) {
        int rX = (chX * 16) + rand.nextInt(16) + 8;
        int rZ = (chZ * 16) + rand.nextInt(16) + 8;
        int rY = world.getTopSolidOrLiquidBlock(rX,  rZ);
        return new BlockPos(rX, rY - 1, rZ);
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
