package shordinger.wrapper.net.minecraft.world.biome;

import shordinger.wrapper.net.minecraft.block.BlockDirt;
import shordinger.wrapper.net.minecraft.block.BlockDoublePlant;
import shordinger.wrapper.net.minecraft.block.BlockTallGrass;
import shordinger.wrapper.net.minecraft.entity.passive.EntityRabbit;
import shordinger.wrapper.net.minecraft.entity.passive.EntityWolf;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.chunk.ChunkPrimer;
import shordinger.wrapper.net.minecraft.world.gen.feature.WorldGenAbstractTree;
import shordinger.wrapper.net.minecraft.world.gen.feature.WorldGenBlockBlob;
import shordinger.wrapper.net.minecraft.world.gen.feature.WorldGenMegaPineTree;
import shordinger.wrapper.net.minecraft.world.gen.feature.WorldGenTaiga1;
import shordinger.wrapper.net.minecraft.world.gen.feature.WorldGenTaiga2;
import shordinger.wrapper.net.minecraft.world.gen.feature.WorldGenTallGrass;
import shordinger.wrapper.net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class BiomeTaiga extends Biome {

    private static final WorldGenTaiga1 PINE_GENERATOR = new WorldGenTaiga1();
    private static final WorldGenTaiga2 SPRUCE_GENERATOR = new WorldGenTaiga2(false);
    private static final WorldGenMegaPineTree MEGA_PINE_GENERATOR = new WorldGenMegaPineTree(false, false);
    private static final WorldGenMegaPineTree MEGA_SPRUCE_GENERATOR = new WorldGenMegaPineTree(false, true);
    private static final WorldGenBlockBlob FOREST_ROCK_GENERATOR = new WorldGenBlockBlob(Blocks.MOSSY_COBBLESTONE, 0);
    private final BiomeTaiga.Type type;

    public BiomeTaiga(BiomeTaiga.Type typeIn, Biome.BiomeProperties properties) {
        super(properties);
        this.type = typeIn;
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityWolf.class, 8, 4, 4));
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityRabbit.class, 4, 2, 3));
        this.decorator.treesPerChunk = 10;

        if (typeIn != BiomeTaiga.Type.MEGA && typeIn != BiomeTaiga.Type.MEGA_SPRUCE) {
            this.decorator.grassPerChunk = 1;
            this.decorator.mushroomsPerChunk = 1;
        } else {
            this.decorator.grassPerChunk = 7;
            this.decorator.deadBushPerChunk = 1;
            this.decorator.mushroomsPerChunk = 3;
        }
    }

    public WorldGenAbstractTree getRandomTreeFeature(Random rand) {
        if ((this.type == BiomeTaiga.Type.MEGA || this.type == BiomeTaiga.Type.MEGA_SPRUCE) && rand.nextInt(3) == 0) {
            return this.type != BiomeTaiga.Type.MEGA_SPRUCE && rand.nextInt(13) != 0 ? MEGA_PINE_GENERATOR
                : MEGA_SPRUCE_GENERATOR;
        } else {
            return (WorldGenAbstractTree) (rand.nextInt(3) == 0 ? PINE_GENERATOR : SPRUCE_GENERATOR);
        }
    }

    /**
     * Gets a WorldGen appropriate for this biome.
     */
    public WorldGenerator getRandomWorldGenForGrass(Random rand) {
        return rand.nextInt(5) > 0 ? new WorldGenTallGrass(BlockTallGrass.EnumType.FERN)
            : new WorldGenTallGrass(BlockTallGrass.EnumType.GRASS);
    }

    public void decorate(World worldIn, Random rand, BlockPos pos) {
        if ((this.type == BiomeTaiga.Type.MEGA || this.type == BiomeTaiga.Type.MEGA_SPRUCE)
            && net.minecraftforge.event.terraingen.TerrainGen.decorate(
            worldIn,
            rand,
            new net.minecraft.util.math.ChunkPos(pos),
            net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.ROCK)) {
            int i = rand.nextInt(3);

            for (int j = 0; j < i; ++j) {
                int k = rand.nextInt(16) + 8;
                int l = rand.nextInt(16) + 8;
                BlockPos blockpos = worldIn.getHeight(pos.add(k, 0, l));
                FOREST_ROCK_GENERATOR.generate(worldIn, rand, blockpos);
            }
        }

        DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.FERN);

        if (net.minecraftforge.event.terraingen.TerrainGen.decorate(
            worldIn,
            rand,
            new net.minecraft.util.math.ChunkPos(pos),
            net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.FLOWERS))
            for (int i1 = 0; i1 < 7; ++i1) {
                int j1 = rand.nextInt(16) + 8;
                int k1 = rand.nextInt(16) + 8;
                int l1 = rand.nextInt(
                    worldIn.getHeight(pos.add(j1, 0, k1))
                        .getY() + 32);
                DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(j1, l1, k1));
            }

        super.decorate(worldIn, rand, pos);
    }

    public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
        if (this.type == BiomeTaiga.Type.MEGA || this.type == BiomeTaiga.Type.MEGA_SPRUCE) {
            this.topBlock = Blocks.GRASS.getDefaultState();
            this.fillerBlock = Blocks.DIRT.getDefaultState();

            if (noiseVal > 1.75D) {
                this.topBlock = Blocks.DIRT.getDefaultState()
                    .withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT);
            } else if (noiseVal > -0.95D) {
                this.topBlock = Blocks.DIRT.getDefaultState()
                    .withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.PODZOL);
            }
        }

        this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
    }

    public static enum Type {
        NORMAL,
        MEGA,
        MEGA_SPRUCE;
    }
}
