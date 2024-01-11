package shordinger.wrapper.net.minecraft.world.chunk.storage;

import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.util.datafix.DataFixer;
import shordinger.wrapper.net.minecraft.world.WorldProvider;
import shordinger.wrapper.net.minecraft.world.storage.SaveHandler;
import shordinger.wrapper.net.minecraft.world.storage.ThreadedFileIOBase;
import shordinger.wrapper.net.minecraft.world.storage.WorldInfo;

import javax.annotation.Nullable;
import java.io.File;

public class AnvilSaveHandler extends SaveHandler {

    public AnvilSaveHandler(File p_i46650_1_, String saveDirectoryName, boolean p_i46650_3_, DataFixer dataFixerIn) {
        super(p_i46650_1_, saveDirectoryName, p_i46650_3_, dataFixerIn);
    }

    /**
     * initializes and returns the chunk loader for the specified world provider
     */
    public IChunkLoader getChunkLoader(WorldProvider provider) {
        File file1 = this.getWorldDirectory();

        if (provider.getSaveFolder() != null) {
            File file3 = new File(file1, provider.getSaveFolder());
            file3.mkdirs();
            return new AnvilChunkLoader(file3, this.dataFixer);
        } else {
            return new AnvilChunkLoader(file1, this.dataFixer);
        }
    }

    /**
     * Saves the given World Info with the given NBTTagCompound as the Player.
     */
    public void saveWorldInfoWithPlayer(WorldInfo worldInformation, @Nullable NBTTagCompound tagCompound) {
        worldInformation.setSaveVersion(19133);
        super.saveWorldInfoWithPlayer(worldInformation, tagCompound);
    }

    /**
     * Called to flush all changes to disk, waiting for them to complete.
     */
    public void flush() {
        try {
            ThreadedFileIOBase.getThreadedIOInstance()
                .waitForFinish();
        } catch (InterruptedException interruptedexception) {
            interruptedexception.printStackTrace();
        }

        RegionFileCache.clearRegionFileReferences();
    }
}
