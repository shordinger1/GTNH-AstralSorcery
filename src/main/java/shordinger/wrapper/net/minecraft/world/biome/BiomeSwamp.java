package shordinger.wrapper.net.minecraft.world.biome;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.BlockFlower;
import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.entity.monster.EntitySlime;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.chunk.ChunkPrimer;
import shordinger.wrapper.net.minecraft.world.gen.feature.WorldGenAbstractTree;
import shordinger.wrapper.net.minecraft.world.gen.feature.WorldGenFossils;

import java.util.Random;

public class BiomeSwamp extends Biome {

    protected static final IBlockState WATER_LILY = Blocks.WATERLILY.getDefaultState();

    protected BiomeSwamp(Biome.BiomeProperties properties) {
        super(properties);
        this.decorator.treesPerChunk = 2;
        this.decorator.flowersPerChunk = 1;
        this.decorator.deadBushPerChunk = 1;
        this.decorator.mushroomsPerChunk = 8;
        this.decorator.reedsPerChunk = 10;
        this.decorator.clayPerChunk = 1;
        this.decorator.waterlilyPerChunk = 4;
        this.decorator.sandPatchesPerChunk = 0;
        this.decorator.gravelPatchesPerChunk = 0;
        this.decorator.grassPerChunk = 5;
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntitySlime.class, 1, 1, 1));
    }

    public WorldGenAbstractTree getRandomTreeFeature(Random rand) {
        return SWAMP_FEATURE;
    }

    public BlockFlower.EnumFlowerType pickRandomFlower(Random rand, BlockPos pos) {
        return BlockFlower.EnumFlowerType.BLUE_ORCHID;
    }

    public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
        double d0 = GRASS_COLOR_NOISE.getValue((double) x * 0.25D, (double) z * 0.25D);

        if (d0 > 0.0D) {
            int i = x & 15;
            int j = z & 15;

            for (int k = 255; k >= 0; --k) {
                if (chunkPrimerIn.getBlockState(j, k, i)
                    .getMaterial() != Material.AIR) {
                    if (k == 62 && chunkPrimerIn.getBlockState(j, k, i)
                        .getBlock() != Blocks.WATER) {
                        chunkPrimerIn.setBlockState(j, k, i, WATER);

                        if (d0 < 0.12D) {
                            chunkPrimerIn.setBlockState(j, k + 1, i, WATER_LILY);
                        }
                    }

                    break;
                }
            }
        }

        this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
    }

    public void decorate(World worldIn, Random rand, BlockPos pos) {
        super.decorate(worldIn, rand, pos);

        if (net.minecraftforge.event.terraingen.TerrainGen.decorate(
            worldIn,
            rand,
            new net.minecraft.util.math.ChunkPos(pos),
            net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.FOSSIL))
            if (rand.nextInt(64) == 0) {
                (new WorldGenFossils()).generate(worldIn, rand, pos);
            }
    }

    @SideOnly(Side.CLIENT)
    public int getGrassColorAtPos(BlockPos pos) {
        double d0 = GRASS_COLOR_NOISE.getValue((double) pos.getX() * 0.0225D, (double) pos.getZ() * 0.0225D);
        return getModdedBiomeGrassColor(d0 < -0.1D ? 5011004 : 6975545);
    }

    @SideOnly(Side.CLIENT)
    public int getFoliageColorAtPos(BlockPos pos) {
        return getModdedBiomeFoliageColor(6975545);
    }

    @Override
    public void addDefaultFlowers() {
        addFlower(
            Blocks.RED_FLOWER.getDefaultState()
                .withProperty(Blocks.RED_FLOWER.getTypeProperty(), BlockFlower.EnumFlowerType.BLUE_ORCHID),
            10);
    }
}
