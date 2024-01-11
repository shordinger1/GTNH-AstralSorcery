package shordinger.wrapper.net.minecraft.world.biome;

import shordinger.wrapper.net.minecraft.block.BlockDirt;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.chunk.ChunkPrimer;

import java.util.Random;

public class BiomeSavannaMutated extends BiomeSavanna {

    public BiomeSavannaMutated(Biome.BiomeProperties properties) {
        super(properties);
        this.decorator.treesPerChunk = 2;
        this.decorator.flowersPerChunk = 2;
        this.decorator.grassPerChunk = 5;
    }

    public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
        this.topBlock = Blocks.GRASS.getDefaultState();
        this.fillerBlock = Blocks.DIRT.getDefaultState();

        if (noiseVal > 1.75D) {
            this.topBlock = Blocks.STONE.getDefaultState();
            this.fillerBlock = Blocks.STONE.getDefaultState();
        } else if (noiseVal > -0.5D) {
            this.topBlock = Blocks.DIRT.getDefaultState()
                .withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT);
        }

        this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
    }

    public void decorate(World worldIn, Random rand, BlockPos pos) {
        this.decorator.decorate(worldIn, rand, this, pos);
    }
}
