/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.world;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;

import cpw.mods.fml.common.IWorldGenerator;
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

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralWorldGenerator
 * Created by HellFirePvP
 * Date: 07.05.2016 / 19:21
 */
public class AstralWorldGenerator implements IWorldGenerator {
    // public class AstralWorldGenerator {

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

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
                         IChunkProvider chunkProvider) {
        if (!Config.enableFlatGen && world.getWorldInfo()
            .getTerrainType()
            .equals(WorldType.FLAT)) return;
        if (!Config.worldGenDimWhitelist.contains(world.provider.dimensionId)) return;

        ChunkVersionController.instance
            .setGenerationVersion(new ChunkPos(chunkX, chunkZ), CURRENT_WORLD_GENERATOR_VERSION);
        generateWithLastKnownVersion(chunkX, chunkZ, world, -1);
    }
}
