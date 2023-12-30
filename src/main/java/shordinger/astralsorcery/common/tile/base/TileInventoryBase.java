/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile.base;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import com.gtnewhorizons.modularui.api.forge.ItemStackHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import net.minecraftforge.common.util.ForgeDirection;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.migration.Capability;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileInventoryBase
 * Created by HellFirePvP
 * Date: 27.03.2017 / 17:53
 */
public class TileInventoryBase extends TileEntityTick {

    protected int inventorySize;
    private ItemHandlerTile handle;
    private List<ForgeDirection> applicableSides;

    public TileInventoryBase(int inventorySize) {
        this(inventorySize, ForgeDirection.VALUES);
    }

    public TileInventoryBase(int inventorySize, ForgeDirection... applicableSides) {
        this.inventorySize = inventorySize;
        this.handle = createNewItemHandler();
        this.applicableSides = Arrays.asList(applicableSides);
    }

    protected ItemHandlerTile createNewItemHandler() {
        return new ItemHandlerTile(this);
    }

    @Override
    protected void onFirstTick() {
    }

    public ItemHandlerTile getInventoryHandler() {
        return handle;
    }

    private boolean hasHandlerForSide(ForgeDirection facing) {
        return facing == null || applicableSides.contains(facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, ForgeDirection facing) {
        return hasHandlerForSide(facing) ? capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
            : super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, ForgeDirection facing) {
        if (hasHandlerForSide(facing)) {
            if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(handle);
            }
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.handle = createNewItemHandler();
        this.handle.deserializeNBT(compound.getCompoundTag("inventory"));
        if (this.handle.getSlots() != this.inventorySize) {
            ItemHandlerTile newInv = createNewItemHandler();
            for (int i = 0; i < Math.min(this.handle.getSlots(), this.inventorySize); i++) {
                ItemStack old = this.handle.getStackInSlot(i);
                old = ItemUtils.copyStackWithSize(old, old.getCount());
                newInv.setStackInSlot(i, old);
            }
            this.handle = newInv;
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setTag("inventory", this.handle.serializeNBT());
    }

    public int getInventorySize() {
        return inventorySize;
    }

    protected void onInventoryChanged(int slotChanged) {
    }

    @Override
    public void tick() {

    }

    public static class ItemHandlerTileFiltered extends ItemHandlerTile {

        public ItemHandlerTileFiltered(TileInventoryBase inv) {
            super(inv);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!canInsertItem(slot, stack, getStackInSlot(slot))) {
                return stack;
            }
            return super.insertItem(slot, stack, simulate);
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!canExtractItem(slot, amount, getStackInSlot(slot))) {
                return null;
            }
            return super.extractItem(slot, amount, simulate);
        }

        public boolean canInsertItem(int slot, ItemStack toAdd, @Nonnull ItemStack existing) {
            return true;
        }

        public boolean canExtractItem(int slot, int amount, @Nonnull ItemStack existing) {
            return true;
        }

    }

    public static class ItemHandlerTile extends ItemStackHandler {

        private final TileInventoryBase tile;

        public ItemHandlerTile(TileInventoryBase inv) {
            super(inv.inventorySize);
            this.tile = inv;
        }

        @Override
        public void onContentsChanged(int slot) {
            tile.onInventoryChanged(slot);
            tile.markForUpdate();
        }

        public void clearInventory() {
            for (int i = 0; i < getSlots(); i++) {
                setStackInSlot(i, null);
                onContentsChanged(i);
            }
        }

        @Override
        public int getStackLimit(int slot, ItemStack stack) {
            return super.getStackLimit(slot, stack);
        }

    }

}
