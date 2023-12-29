/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import shordinger.astralsorcery.common.auxiliary.StorageNetworkHandler;
import shordinger.astralsorcery.common.tile.base.TileEntityTick;
import shordinger.astralsorcery.common.tile.storage.IStorageNetworkTile;
import shordinger.astralsorcery.common.tile.storage.StorageCache;
import shordinger.astralsorcery.common.tile.storage.StorageKey;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileStorageCore
 * Created by HellFirePvP
 * Date: 13.12.2017 / 12:22
 */
public class TileStorageCore extends TileEntityTick implements IStorageNetworkTile<TileStorageCore> {

    private StorageCache storageCache = new StorageCache();
    private UUID ownerUUID;

    @Override
    public void update() {
        super.update();

        if (getOwnerUUID() == null) {
            return;
        }
    }

    @Override
    public void receiveMappingChange(StorageNetworkHandler.MappingChange newMapping) {

    }

    @Override
    public void onLoad() {
        super.onLoad();

        StorageNetworkHandler.getHandler(getWorld())
            .addCore(this);
    }

    @Override
    public void invalidate() {
        super.invalidate();

        StorageNetworkHandler.getHandler(getWorld())
            .removeCore(this);
    }

    @Override
    protected void onFirstTick() {
    }

    @Override
    public TileStorageCore getAssociatedCore() {
        return this;
    }

    @Override
    public World getNetworkWorld() {
        return getWorld();
    }

    @Override
    public BlockPos getLocationPos() {
        return getPos();
    }

    @Nullable
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(@Nullable UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public boolean extractFromStorage(IItemHandler inv, StorageKey key, boolean simulate) {
        return this.storageCache.attemptTransferInto(key, inv, simulate);
    }

    public boolean insertIntoStorage(ItemStack stack) {
        return !stack.isEmpty() && this.storageCache.add(stack);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        if (this.ownerUUID != null) {
            compound.setUniqueId("ownerUUID", this.ownerUUID);
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        if (compound.hasUniqueId("ownerUUID")) {
            this.ownerUUID = compound.getUniqueId("ownerUUID");
        } else {
            this.ownerUUID = null;
        }
    }

    @Override
    public void readSaveNBT(NBTTagCompound compound) {
        super.readSaveNBT(compound);

        StorageCache cache = new StorageCache();
        cache.readFromNBT(compound.getCompoundTag("storage"));
        this.storageCache = cache;
    }

    @Override
    public void writeSaveNBT(NBTTagCompound compound) {
        super.writeSaveNBT(compound);

        NBTHelper.setAsSubTag(compound, "storage", this.storageCache::writeToNBT);
    }

}
