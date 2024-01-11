/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile.base;

import java.util.Random;

import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.network.NetworkManager;
import shordinger.wrapper.net.minecraft.network.play.server.SPacketUpdateTileEntity;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileEntitySynchronized
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:17
 */
public abstract class TileEntitySynchronized extends TileEntity {

    protected static final Random rand = new Random();

    public final void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readCustomNBT(compound);
        readSaveNBT(compound);
    }

    // Both Network & Chunk-saving
    public void readCustomNBT(NBTTagCompound compound) {}

    // Only Network-read
    public void readNetNBT(NBTTagCompound compound) {}

    // Only Chunk-read
    public void readSaveNBT(NBTTagCompound compound) {}

    public final NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        writeCustomNBT(compound);
        writeSaveNBT(compound);
        return compound;
    }

    // Both Network & Chunk-saving
    public void writeCustomNBT(NBTTagCompound compound) {}

    // Only Network-write
    public void writeNetNBT(NBTTagCompound compound) {}

    // Only Chunk-write
    public void writeSaveNBT(NBTTagCompound compound) {}

    @Override
    public final SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();
        super.writeToNBT(compound);
        writeCustomNBT(compound);
        writeNetNBT(compound);
        return new SPacketUpdateTileEntity(getPos(), 255, compound);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = new NBTTagCompound();
        super.writeToNBT(compound);
        writeCustomNBT(compound);
        return compound;
    }

    public IBlockState getBlockState() {
        return this.world.getBlockState(this.pos);
    }

    public final void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity packet) {
        super.onDataPacket(manager, packet);
        readCustomNBT(packet.getNbtCompound());
        readNetNBT(packet.getNbtCompound());
    }

    public void markForUpdate() {
        IBlockState thisState = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, thisState, thisState, 3);
        markDirty();
    }

}
