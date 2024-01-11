package shordinger.wrapper.net.minecraft.client.settings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.nbt.CompressedStreamTools;

import java.io.File;

@SideOnly(Side.CLIENT)
public class CreativeSettings {

    private static final Logger LOGGER = LogManager.getLogger();
    protected Minecraft minecraft;
    private final File dataFile;
    private final HotbarSnapshot[] hotbarSnapshots = new HotbarSnapshot[9];

    public CreativeSettings(Minecraft minecraftIn, File dataDir) {
        this.minecraft = minecraftIn;
        this.dataFile = new File(dataDir, "hotbar.nbt");

        for (int i = 0; i < 9; ++i) {
            this.hotbarSnapshots[i] = new HotbarSnapshot();
        }

        this.read();
    }

    public void read() {
        try {
            NBTTagCompound nbttagcompound = CompressedStreamTools.read(this.dataFile);

            if (nbttagcompound == null) {
                return;
            }

            for (int i = 0; i < 9; ++i) {
                this.hotbarSnapshots[i].fromTag(nbttagcompound.getTagList(String.valueOf(i), 10));
            }
        } catch (Exception exception) {
            LOGGER.error("Failed to load creative mode options", (Throwable) exception);
        }
    }

    public void write() {
        try {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            for (int i = 0; i < 9; ++i) {
                nbttagcompound.setTag(String.valueOf(i), this.hotbarSnapshots[i].createTag());
            }

            CompressedStreamTools.write(nbttagcompound, this.dataFile);
        } catch (Exception exception) {
            LOGGER.error("Failed to save creative mode options", (Throwable) exception);
        }
    }

    public HotbarSnapshot getHotbarSnapshot(int p_192563_1_) {
        return this.hotbarSnapshots[p_192563_1_];
    }
}
