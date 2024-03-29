package shordinger.wrapper.net.minecraft.world.biome;

import shordinger.wrapper.net.minecraft.block.BlockDoublePlant;
import shordinger.wrapper.net.minecraft.block.BlockFlower;
import shordinger.wrapper.net.minecraft.entity.passive.EntityDonkey;
import shordinger.wrapper.net.minecraft.entity.passive.EntityHorse;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.gen.feature.WorldGenAbstractTree;

import java.util.Random;

public class BiomePlains extends Biome {

    protected boolean sunflowers;

    public BiomePlains(boolean p_i46699_1_, Biome.BiomeProperties properties) {
        super(properties);
        this.sunflowers = p_i46699_1_;
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityHorse.class, 5, 2, 6));
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityDonkey.class, 1, 1, 3));
        this.decorator.treesPerChunk = 0;
        this.decorator.extraTreeChance = 0.05F;
        this.decorator.flowersPerChunk = 4;
        this.decorator.grassPerChunk = 10;
    }

    public BlockFlower.EnumFlowerType pickRandomFlower(Random rand, BlockPos pos) {
        double d0 = GRASS_COLOR_NOISE.getValue((double) pos.getX() / 200.0D, (double) pos.getZ() / 200.0D);

        if (d0 < -0.8D) {
            int j = rand.nextInt(4);

            switch (j) {
                case 0:
                    return BlockFlower.EnumFlowerType.ORANGE_TULIP;
                case 1:
                    return BlockFlower.EnumFlowerType.RED_TULIP;
                case 2:
                    return BlockFlower.EnumFlowerType.PINK_TULIP;
                case 3:
                default:
                    return BlockFlower.EnumFlowerType.WHITE_TULIP;
            }
        } else if (rand.nextInt(3) > 0) {
            int i = rand.nextInt(3);

            if (i == 0) {
                return BlockFlower.EnumFlowerType.POPPY;
            } else {
                return i == 1 ? BlockFlower.EnumFlowerType.HOUSTONIA : BlockFlower.EnumFlowerType.OXEYE_DAISY;
            }
        } else {
            return BlockFlower.EnumFlowerType.DANDELION;
        }
    }

    public void decorate(World worldIn, Random rand, BlockPos pos) {
        double d0 = GRASS_COLOR_NOISE.getValue((double) (pos.getX() + 8) / 200.0D, (double) (pos.getZ() + 8) / 200.0D);

        if (d0 < -0.8D) {
            this.decorator.flowersPerChunk = 15;
            this.decorator.grassPerChunk = 5;
        } else {
            this.decorator.flowersPerChunk = 4;
            this.decorator.grassPerChunk = 10;
            DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.GRASS);

            if (net.minecraftforge.event.terraingen.TerrainGen.decorate(
                worldIn,
                rand,
                new net.minecraft.util.math.ChunkPos(pos),
                net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.GRASS))
                for (int i = 0; i < 7; ++i) {
                    int j = rand.nextInt(16) + 8;
                    int k = rand.nextInt(16) + 8;
                    int l = rand.nextInt(
                        worldIn.getHeight(pos.add(j, 0, k))
                            .getY() + 32);
                    DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(j, l, k));
                }
        }

        if (this.sunflowers && net.minecraftforge.event.terraingen.TerrainGen.decorate(
            worldIn,
            rand,
            new net.minecraft.util.math.ChunkPos(pos),
            net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.FLOWERS)) {
            DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.SUNFLOWER);

            for (int i1 = 0; i1 < 10; ++i1) {
                int j1 = rand.nextInt(16) + 8;
                int k1 = rand.nextInt(16) + 8;
                int l1 = rand.nextInt(
                    worldIn.getHeight(pos.add(j1, 0, k1))
                        .getY() + 32);
                DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(j1, l1, k1));
            }
        }

        super.decorate(worldIn, rand, pos);
    }

    @Override
    public void addDefaultFlowers() {
        BlockFlower red = net.minecraft.init.Blocks.RED_FLOWER;
        BlockFlower yel = net.minecraft.init.Blocks.YELLOW_FLOWER;
        addFlower(
            red.getDefaultState()
                .withProperty(red.getTypeProperty(), BlockFlower.EnumFlowerType.ORANGE_TULIP),
            3);
        addFlower(
            red.getDefaultState()
                .withProperty(red.getTypeProperty(), BlockFlower.EnumFlowerType.RED_TULIP),
            3);
        addFlower(
            red.getDefaultState()
                .withProperty(red.getTypeProperty(), BlockFlower.EnumFlowerType.PINK_TULIP),
            3);
        addFlower(
            red.getDefaultState()
                .withProperty(red.getTypeProperty(), BlockFlower.EnumFlowerType.WHITE_TULIP),
            3);
        addFlower(
            red.getDefaultState()
                .withProperty(red.getTypeProperty(), BlockFlower.EnumFlowerType.POPPY),
            20);
        addFlower(
            red.getDefaultState()
                .withProperty(red.getTypeProperty(), BlockFlower.EnumFlowerType.HOUSTONIA),
            20);
        addFlower(
            red.getDefaultState()
                .withProperty(red.getTypeProperty(), BlockFlower.EnumFlowerType.OXEYE_DAISY),
            20);
        addFlower(
            yel.getDefaultState()
                .withProperty(yel.getTypeProperty(), BlockFlower.EnumFlowerType.DANDELION),
            30);
    }

    public WorldGenAbstractTree getRandomTreeFeature(Random rand) {
        return (WorldGenAbstractTree) (rand.nextInt(3) == 0 ? BIG_TREE_FEATURE : TREE_FEATURE);
    }
}
