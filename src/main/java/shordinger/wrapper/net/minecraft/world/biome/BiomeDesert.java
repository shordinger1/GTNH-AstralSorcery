package shordinger.wrapper.net.minecraft.world.biome;

import shordinger.wrapper.net.minecraft.entity.monster.EntityHusk;
import shordinger.wrapper.net.minecraft.entity.monster.EntityZombie;
import shordinger.wrapper.net.minecraft.entity.monster.EntityZombieVillager;
import shordinger.wrapper.net.minecraft.entity.passive.EntityRabbit;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.gen.feature.WorldGenDesertWells;
import shordinger.wrapper.net.minecraft.world.gen.feature.WorldGenFossils;

import java.util.Iterator;
import java.util.Random;

public class BiomeDesert extends Biome {

    public BiomeDesert(Biome.BiomeProperties properties) {
        super(properties);
        this.spawnableCreatureList.clear();
        this.topBlock = Blocks.SAND.getDefaultState();
        this.fillerBlock = Blocks.SAND.getDefaultState();
        this.decorator.treesPerChunk = -999;
        this.decorator.deadBushPerChunk = 2;
        this.decorator.reedsPerChunk = 50;
        this.decorator.cactiPerChunk = 10;
        this.spawnableCreatureList.clear();
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityRabbit.class, 4, 2, 3));
        Iterator<Biome.SpawnListEntry> iterator = this.spawnableMonsterList.iterator();

        while (iterator.hasNext()) {
            Biome.SpawnListEntry biome$spawnlistentry = iterator.next();

            if (biome$spawnlistentry.entityClass == EntityZombie.class
                || biome$spawnlistentry.entityClass == EntityZombieVillager.class) {
                iterator.remove();
            }
        }

        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityZombie.class, 19, 4, 4));
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityZombieVillager.class, 1, 1, 1));
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityHusk.class, 80, 4, 4));
    }

    public void decorate(World worldIn, Random rand, BlockPos pos) {
        super.decorate(worldIn, rand, pos);

        if (net.minecraftforge.event.terraingen.TerrainGen.decorate(
            worldIn,
            rand,
            new net.minecraft.util.math.ChunkPos(pos),
            net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.DESERT_WELL))
            if (rand.nextInt(1000) == 0) {
                int i = rand.nextInt(16) + 8;
                int j = rand.nextInt(16) + 8;
                BlockPos blockpos = worldIn.getHeight(pos.add(i, 0, j))
                    .up();
                (new WorldGenDesertWells()).generate(worldIn, rand, blockpos);
            }

        if (net.minecraftforge.event.terraingen.TerrainGen.decorate(
            worldIn,
            rand,
            new net.minecraft.util.math.ChunkPos(pos),
            net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.FOSSIL))
            if (rand.nextInt(64) == 0) {
                (new WorldGenFossils()).generate(worldIn, rand, pos);
            }
    }
}
