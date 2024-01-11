package shordinger.wrapper.net.minecraft.world.chunk.storage;

import java.io.IOException;

import javax.annotation.Nullable;

import shordinger.wrapper.net.minecraft.world.MinecraftException;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.chunk.Chunk;

public interface IChunkLoader {

    /**
     * Loads the specified(XZ) chunk into the specified world.
     */
    @Nullable
    Chunk loadChunk(World worldIn, int x, int z) throws IOException;

    void saveChunk(World worldIn, Chunk chunkIn) throws MinecraftException, IOException;

    /**
     * Save extra data associated with this Chunk not normally saved during autosave, only during chunk unload.
     * Currently unused.
     */
    void saveExtraChunkData(World worldIn, Chunk chunkIn) throws IOException;

    /**
     * Called every World.tick()
     */
    void chunkTick();

    /**
     * Flushes all pending chunks fully back to disk
     */
    void flush();

    boolean isChunkGeneratedAt(int x, int z);
}
