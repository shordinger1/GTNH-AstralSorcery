/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.world.WorldServer;
import net.minecraftforge.common.BiomeDictionary;

import com.google.common.collect.Lists;

import shordinger.astralsorcery.common.data.world.WorldCacheManager;
import shordinger.astralsorcery.common.data.world.data.StructureGenBuffer;
import shordinger.astralsorcery.migration.block.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureFinder
 * Created by HellFirePvP
 * Date: 25.02.2018 / 15:29
 */
public class StructureFinder {

    public static final String STRUCT_VILLAGE = "Village";
    public static final String STRUCT_STRONGHOLD = "Stronghold";
    public static final String STRUCT_MASNION = "Mansion";
    public static final String STRUCT_MONUMENT = "Monument";
    public static final String STRUCT_MINESHAFT = "Mineshaft";
    public static final String STRUCT_TEMPLE = "Temple";
    public static final String STRUCT_ENDCITY = "EndCity";
    public static final String STRUCT_FORTRESS = "Fortress";

    private StructureFinder() {
    }

    @Nullable
    public static BlockPos tryFindClosestAstralSorceryStructure(WorldServer world, BlockPos playerPos,
                                                                StructureGenBuffer.StructureType searchKey) {
        StructureGenBuffer buffer = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.STRUCTURE_GEN);
        return buffer.getClosest(searchKey, playerPos);
    }

    @Nullable
    public static BlockPos tryFindClosestVanillaStructure(WorldServer world, BlockPos playerPos, String searchKey) {
        IChunkGenerator gen = world.getChunkProvider().chunkGenerator;
        if (gen == null) return null;
        try {
            return gen.getNearestStructurePos(world, searchKey, playerPos, true);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Nullable
    public static BlockPos tryFindClosestBiomeType(WorldServer world, BlockPos playerPos,
                                                   BiomeDictionary.Type biomeType) {
        List<Biome> fitting = Lists.newArrayList(BiomeDictionary.getBiomes(biomeType));
        if (fitting.isEmpty()) {
            return null;
        }
        BiomeProvider gen = world.getBiomeProvider();
        for (int reach = 64; reach < 2112; reach += 128) {
            BlockPos closest = gen
                .findBiomePosition(playerPos.getX(), playerPos.getZ(), reach, fitting, new Random(world.getSeed()));
            if (closest != null) {
                return closest;
            }
        }
        return null;
    }

}
