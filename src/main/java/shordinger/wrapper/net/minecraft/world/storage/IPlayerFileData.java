package shordinger.wrapper.net.minecraft.world.storage;

import javax.annotation.Nullable;

import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;

public interface IPlayerFileData {

    /**
     * Writes the player data to disk from the specified PlayerEntityMP.
     */
    void writePlayerData(EntityPlayer player);

    /**
     * Reads the player data from disk into the specified PlayerEntityMP.
     */
    @Nullable
    NBTTagCompound readPlayerData(EntityPlayer player);

    /**
     * Returns an array of usernames for which player.dat exists for.
     */
    String[] getAvailablePlayerDat();
}
