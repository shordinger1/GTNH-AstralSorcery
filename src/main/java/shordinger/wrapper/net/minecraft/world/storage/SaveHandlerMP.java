package shordinger.wrapper.net.minecraft.world.storage;

import java.io.File;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.world.MinecraftException;
import shordinger.wrapper.net.minecraft.world.WorldProvider;
import shordinger.wrapper.net.minecraft.world.chunk.storage.IChunkLoader;
import shordinger.wrapper.net.minecraft.world.gen.structure.template.TemplateManager;

@SideOnly(Side.CLIENT)
public class SaveHandlerMP implements ISaveHandler {

    /**
     * Loads and returns the world info
     */
    public WorldInfo loadWorldInfo() {
        return null;
    }

    /**
     * Checks the session lock to prevent save collisions
     */
    public void checkSessionLock() throws MinecraftException {
    }

    /**
     * initializes and returns the chunk loader for the specified world provider
     */
    public IChunkLoader getChunkLoader(WorldProvider provider) {
        return null;
    }

    /**
     * Saves the given World Info with the given NBTTagCompound as the Player.
     */
    public void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound) {
    }

    /**
     * used to update level.dat from old format to MCRegion format
     */
    public void saveWorldInfo(WorldInfo worldInformation) {
    }

    public IPlayerFileData getPlayerNBTManager() {
        return null;
    }

    /**
     * Called to flush all changes to disk, waiting for them to complete.
     */
    public void flush() {
    }

    /**
     * Gets the file location of the given map
     */
    public File getMapFileFromName(String mapName) {
        return null;
    }

    /**
     * Gets the File object corresponding to the base directory of this world.
     */
    public File getWorldDirectory() {
        return null;
    }

    public TemplateManager getStructureTemplateManager() {
        return null;
    }
}
