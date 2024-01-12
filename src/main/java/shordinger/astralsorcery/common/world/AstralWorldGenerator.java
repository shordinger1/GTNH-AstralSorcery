/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.world;

import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.world.attributes.GenAttributeAquamarine;
import shordinger.astralsorcery.common.world.attributes.GenAttributeGlowstoneFlower;
import shordinger.astralsorcery.common.world.attributes.GenAttributeMarble;
import shordinger.astralsorcery.common.world.attributes.GenAttributeRockCrystals;
import shordinger.astralsorcery.common.world.retrogen.ChunkVersionController;
import shordinger.astralsorcery.common.world.structure.StructureAncientShrine;
import shordinger.astralsorcery.common.world.structure.StructureDesertShrine;
import shordinger.astralsorcery.common.world.structure.StructureSmallRuin;
import shordinger.astralsorcery.common.world.structure.StructureSmallShrine;
import shordinger.astralsorcery.common.world.structure.StructureTreasureShrine;
import shordinger.wrapper.net.minecraft.util.math.ChunkPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.WorldProvider;
import shordinger.wrapper.net.minecraft.world.WorldType;
import shordinger.wrapper.net.minecraft.world.chunk.IChunkProvider;
import shordinger.wrapper.net.minecraft.world.gen.IChunkGenerator;
import shordinger.wrapper.net.minecraftforge.fml.common.IWorldGenerator;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralWorldGenerator
 * Created by HellFirePvP
 * Date: 07.05.2016 / 19:21
 */
public class AstralWorldGenerator implements IWorldGenerator {

    public static final int CURRENT_WORLD_GENERATOR_VERSION = 3;

    private final List<WorldGenAttributeCommon> structures = new LinkedList<>();
    private final List<WorldGenAttribute> decorators = new LinkedList<>();

    private final List<WorldGenAttribute> worldGenAttributes = new LinkedList<>();

    public void pushConfigEntries() {
        structures.add(new StructureAncientShrine());
        structures.add(new StructureDesertShrine());
        structures.add(new StructureSmallShrine());
        structures.add(new StructureTreasureShrine());
        structures.add(new StructureSmallRuin());

        decorators.add(new GenAttributeGlowstoneFlower());
        decorators.add(new GenAttributeRockCrystals());
    }

    public AstralWorldGenerator setupAttributes() {
        if (Config.marbleAmount > 0) {
            decorators.add(new GenAttributeMarble());
        }
        if (Config.aquamarineAmount > 0) {
            decorators.add(new GenAttributeAquamarine());
        }

        worldGenAttributes.addAll(decorators);
        worldGenAttributes.addAll(structures);
        return this;
    }

    public void handleRetroGen(World world, ChunkPos pos, Integer lastKnownChunkVersion) {
        ChunkVersionController.instance.setGenerationVersion(pos, CURRENT_WORLD_GENERATOR_VERSION);

        generateWithLastKnownVersion(pos.x, pos.z, world, lastKnownChunkVersion);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
                         IChunkProvider chunkProvider) {
        if (!Config.enableFlatGen && world.getWorldType()
            .equals(WorldType.FLAT)) return;
        if (!Config.worldGenDimWhitelist.contains(world.provider.dimensionId)) return;

        ChunkVersionController.instance
            .setGenerationVersion(new ChunkPos(chunkX, chunkZ), CURRENT_WORLD_GENERATOR_VERSION);
        generateWithLastKnownVersion(chunkX, chunkZ, world, -1);

        /*
         * for (int xx = 0; xx < 16; xx++) {
         * for (int zz = 0; zz < 16; zz++) {
         * BlockPos pos = new BlockPos((chunkX * 16) + xx, 0, (chunkZ * 16) + zz);
         * float distr = SkyNoiseCalculator.getSkyNoiseDistribution(world, pos);
         * int y = (int) (35 + distr * 40);
         * world.setBlockState(new BlockPos(pos.getX(), y, pos.getZ()), Blocks.GLASS.getDefaultState(), 2);
         * }
         * }
         */
    }

    private void generateWithLastKnownVersion(int chunkX, int chunkZ, World world, int lastKnownChunkVersion) {
        long worldSeed = world.getSeed();
        Random rand = new Random(worldSeed);
        long xSeed = rand.nextLong() >> 2 + 1L;
        long zSeed = rand.nextLong() >> 2 + 1L;
        long chunkSeed = (xSeed * chunkX + zSeed * chunkZ) ^ worldSeed;

        for (WorldGenAttribute attribute : worldGenAttributes) {
            if (attribute.attributeVersion > lastKnownChunkVersion) {
                rand.setSeed(chunkSeed);
                attribute.generate(rand, chunkX, chunkZ, world);
            }
        }
    }

}
