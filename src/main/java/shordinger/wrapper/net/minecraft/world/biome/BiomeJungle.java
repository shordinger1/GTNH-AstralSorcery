package shordinger.wrapper.net.minecraft.world.biome;

import shordinger.wrapper.net.minecraft.block.BlockLeaves;
import shordinger.wrapper.net.minecraft.block.BlockOldLeaf;
import shordinger.wrapper.net.minecraft.block.BlockOldLog;
import shordinger.wrapper.net.minecraft.block.BlockPlanks;
import shordinger.wrapper.net.minecraft.block.BlockTallGrass;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.entity.passive.EntityChicken;
import shordinger.wrapper.net.minecraft.entity.passive.EntityOcelot;
import shordinger.wrapper.net.minecraft.entity.passive.EntityParrot;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.gen.feature.WorldGenAbstractTree;
import shordinger.wrapper.net.minecraft.world.gen.feature.WorldGenMegaJungle;
import shordinger.wrapper.net.minecraft.world.gen.feature.WorldGenMelon;
import shordinger.wrapper.net.minecraft.world.gen.feature.WorldGenShrub;
import shordinger.wrapper.net.minecraft.world.gen.feature.WorldGenTallGrass;
import shordinger.wrapper.net.minecraft.world.gen.feature.WorldGenTrees;
import shordinger.wrapper.net.minecraft.world.gen.feature.WorldGenVines;
import shordinger.wrapper.net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class BiomeJungle extends Biome {

    private final boolean isEdge;
    private static final IBlockState JUNGLE_LOG = Blocks.LOG.getDefaultState()
        .withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
    private static final IBlockState JUNGLE_LEAF = Blocks.LEAVES.getDefaultState()
        .withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE)
        .withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
    /**
     * The block state for the Oak leaf
     */
    private static final IBlockState OAK_LEAF = Blocks.LEAVES.getDefaultState()
        .withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK)
        .withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));

    public BiomeJungle(boolean isEdgeIn, Biome.BiomeProperties properties) {
        super(properties);
        this.isEdge = isEdgeIn;

        if (isEdgeIn) {
            this.decorator.treesPerChunk = 2;
        } else {
            this.decorator.treesPerChunk = 50;
        }

        this.decorator.grassPerChunk = 25;
        this.decorator.flowersPerChunk = 4;

        if (!isEdgeIn) {
            this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityOcelot.class, 2, 1, 1));
        }

        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityParrot.class, 40, 1, 2));
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityChicken.class, 10, 4, 4));
    }

    public WorldGenAbstractTree getRandomTreeFeature(Random rand) {
        if (rand.nextInt(10) == 0) {
            return BIG_TREE_FEATURE;
        } else if (rand.nextInt(2) == 0) {
            return new WorldGenShrub(JUNGLE_LOG, OAK_LEAF);
        } else {
            return (WorldGenAbstractTree) (!this.isEdge && rand.nextInt(3) == 0
                ? new WorldGenMegaJungle(false, 10, 20, JUNGLE_LOG, JUNGLE_LEAF)
                : new WorldGenTrees(false, 4 + rand.nextInt(7), JUNGLE_LOG, JUNGLE_LEAF, true));
        }
    }

    /**
     * Gets a WorldGen appropriate for this biome.
     */
    public WorldGenerator getRandomWorldGenForGrass(Random rand) {
        return rand.nextInt(4) == 0 ? new WorldGenTallGrass(BlockTallGrass.EnumType.FERN)
            : new WorldGenTallGrass(BlockTallGrass.EnumType.GRASS);
    }

    public void decorate(World worldIn, Random rand, BlockPos pos) {
        super.decorate(worldIn, rand, pos);
        int i = rand.nextInt(16) + 8;
        int j = rand.nextInt(16) + 8;
        int height = worldIn.getHeight(pos.add(i, 0, j))
            .getY() * 2; // could == 0, which crashes nextInt
        if (height < 1) height = 1;
        int k = rand.nextInt(height);
        if (net.minecraftforge.event.terraingen.TerrainGen.decorate(
            worldIn,
            rand,
            new net.minecraft.util.math.ChunkPos(pos),
            pos.add(i, k, j),
            net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.PUMPKIN))
            (new WorldGenMelon()).generate(worldIn, rand, pos.add(i, k, j));
        WorldGenVines worldgenvines = new WorldGenVines();

        if (net.minecraftforge.event.terraingen.TerrainGen.decorate(
            worldIn,
            rand,
            new net.minecraft.util.math.ChunkPos(pos),
            net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.GRASS))
            for (int j1 = 0; j1 < 50; ++j1) {
                k = rand.nextInt(16) + 8;
                int l = 128;
                int i1 = rand.nextInt(16) + 8;
                worldgenvines.generate(worldIn, rand, pos.add(k, 128, i1));
            }
    }
}
