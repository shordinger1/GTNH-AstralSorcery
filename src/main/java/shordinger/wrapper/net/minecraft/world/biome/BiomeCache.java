package shordinger.wrapper.net.minecraft.world.biome;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import shordinger.wrapper.net.minecraft.server.MinecraftServer;

import java.util.List;

public class BiomeCache {

    /**
     * Reference to the WorldChunkManager
     */
    private final BiomeProvider provider;
    /**
     * The last time this BiomeCache was cleaned, in milliseconds.
     */
    private long lastCleanupTime;
    /**
     * The map of keys to BiomeCacheBlocks. Keys are based on the chunk x, z coordinates as (x | z << 32).
     */
    private final Long2ObjectMap<BiomeCache.Block> cacheMap = new Long2ObjectOpenHashMap<BiomeCache.Block>(4096);
    /**
     * The list of cached BiomeCacheBlocks
     */
    private final List<BiomeCache.Block> cache = Lists.<BiomeCache.Block>newArrayList();

    public BiomeCache(BiomeProvider provider) {
        this.provider = provider;
    }

    /**
     * Returns a biome cache block at location specified.
     */
    public BiomeCache.Block getEntry(int x, int z) {
        x = x >> 4;
        z = z >> 4;
        long i = (long) x & 4294967295L | ((long) z & 4294967295L) << 32;
        BiomeCache.Block biomecache$block = (BiomeCache.Block) this.cacheMap.get(i);

        if (biomecache$block == null) {
            biomecache$block = new BiomeCache.Block(x, z);
            this.cacheMap.put(i, biomecache$block);
            this.cache.add(biomecache$block);
        }

        biomecache$block.lastAccessTime = MinecraftServer.getCurrentTimeMillis();
        return biomecache$block;
    }

    public Biome getBiome(int x, int z, Biome defaultValue) {
        Biome biome = this.getEntry(x, z)
            .getBiome(x, z);
        return biome == null ? defaultValue : biome;
    }

    /**
     * Removes BiomeCacheBlocks from this cache that haven't been accessed in at least 30 seconds.
     */
    public void cleanupCache() {
        long i = MinecraftServer.getCurrentTimeMillis();
        long j = i - this.lastCleanupTime;

        if (j > 7500L || j < 0L) {
            this.lastCleanupTime = i;

            for (int k = 0; k < this.cache.size(); ++k) {
                BiomeCache.Block biomecache$block = this.cache.get(k);
                long l = i - biomecache$block.lastAccessTime;

                if (l > 30000L || l < 0L) {
                    this.cache.remove(k--);
                    long i1 = (long) biomecache$block.x & 4294967295L | ((long) biomecache$block.z & 4294967295L) << 32;
                    this.cacheMap.remove(i1);
                }
            }
        }
    }

    /**
     * Returns the array of cached biome types in the BiomeCacheBlock at the given location.
     */
    public Biome[] getCachedBiomes(int x, int z) {
        return this.getEntry(x, z).biomes;
    }

    public class Block {

        /**
         * Flattened 16 * 16 array of the biomes in this chunk
         */
        public Biome[] biomes = new Biome[256];
        /**
         * World x coordinate of this entry, rounded down to nearest chunk
         */
        public int x;
        /**
         * World z coordinate of this entry, rounded down to nearest chunk
         */
        public int z;
        /**
         * The last time this BiomeCacheBlock was accessed, in milliseconds.
         */
        public long lastAccessTime;

        public Block(int x, int z) {
            this.x = x;
            this.z = z;
            BiomeCache.this.provider.getBiomes(this.biomes, x << 4, z << 4, 16, 16, false);
        }

        /**
         * Returns the BiomeGenBase related to the x, z position from the cache block.
         */
        public Biome getBiome(int x, int z) {
            return this.biomes[x & 15 | (z & 15) << 4];
        }
    }
}
