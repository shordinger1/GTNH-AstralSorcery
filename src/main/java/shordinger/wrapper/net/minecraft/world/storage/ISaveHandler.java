package shordinger.wrapper.net.minecraft.world.storage;

import java.io.File;

import javax.annotation.Nullable;

import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.world.MinecraftException;
import shordinger.wrapper.net.minecraft.world.WorldProvider;
import shordinger.wrapper.net.minecraft.world.chunk.storage.IChunkLoader;
import shordinger.wrapper.net.minecraft.world.gen.structure.template.TemplateManager;

public interface ISaveHandler {

    /**
     * Loads and returns the world info
     */
    @Nullable
    WorldInfo loadWorldInfo();

    /**
     * Checks the session lock to prevent save collisions
     */
    void checkSessionLock() throws MinecraftException;

    /**
     * initializes and returns the chunk loader for the specified world provider
     */
    IChunkLoader getChunkLoader(WorldProvider provider);

    /**
     * Saves the given World Info with the given NBTTagCompound as the Player.
     */
    void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound);

    /**
     * used to update level.dat from old format to MCRegion format
     */
    void saveWorldInfo(WorldInfo worldInformation);

    IPlayerFileData getPlayerNBTManager();

    /**
     * Called to flush all changes to disk, waiting for them to complete.
     */
    void flush();

    /**
     * Gets the File object corresponding to the base directory of this world.
     */
    File getWorldDirectory();

    /**
     * Gets the file location of the given map
     */
    File getMapFileFromName(String mapName);

    TemplateManager getStructureTemplateManager();
}