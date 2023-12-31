/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile.base;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.tileentity.TileEntity;

import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.IBlockState;
import shordinger.astralsorcery.migration.WorldHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileEntitySynchronized
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:17
 */
public abstract class TileEntitySynchronized extends TileEntity {

    protected static final Random rand = new Random();


    public BlockPos getPos() {

        return new BlockPos(xCoord, yCoord, zCoord);
    }

    public final void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readCustomNBT(compound);
        readSaveNBT(compound);
    }

    // Both Network & Chunk-saving
    public void readCustomNBT(NBTTagCompound compound) {
    }

    // Only Network-read
    public void readNetNBT(NBTTagCompound compound) {
    }

    // Only Chunk-read
    public void readSaveNBT(NBTTagCompound compound) {
    }

    public final void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        writeCustomNBT(compound);
        writeSaveNBT(compound);
        //return compound;
    }

    // Both Network & Chunk-saving
    public void writeCustomNBT(NBTTagCompound compound) {
    }

    // Only Network-write
    public void writeNetNBT(NBTTagCompound compound) {
    }

    // Only Chunk-write
    public void writeSaveNBT(NBTTagCompound compound) {
    }

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
        return WorldHelper.getBlockState(worldObj, this.pos);
    }

    public final void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity packet) {
        super.onDataPacket(manager, packet);
        readCustomNBT(packet.getNbtCompound());
        readNetNBT(packet.getNbtCompound());
    }

    public void markForUpdate() {
        IBlockState thisState = WorldHelper.getBlockState(worldObj, pos);
        world.notifyBlockUpdate(pos, thisState, thisState, 3);
        markDirty();
    }

}
