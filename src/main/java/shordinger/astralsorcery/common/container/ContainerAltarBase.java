/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.container;

import com.gtnewhorizons.modularui.api.forge.ItemStackHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import shordinger.astralsorcery.common.item.base.ItemConstellationFocus;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerAltarBase
 * Created by HellFirePvP
 * Date: 16.10.2016 / 19:26
 */
public abstract class ContainerAltarBase extends Container {

    public final InventoryPlayer playerInv;
    public final TileAltar tileAltar;
    public final ItemStackHandler invHandler;
    public final int altarGridSlotSize;

    public ContainerAltarBase(InventoryPlayer playerInv, TileAltar tileAltar, int altarGridSlotSize) {
        this.playerInv = playerInv;
        this.tileAltar = tileAltar;
        this.invHandler = tileAltar.getInventoryHandler();
        this.altarGridSlotSize = altarGridSlotSize;

        bindPlayerInventory();
        bindAltarInventory();
    }

    abstract void bindPlayerInventory();

    abstract void bindAltarInventory();

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = null;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (this instanceof ContainerAltarTrait && index < 36
                && itemstack1.getItem() instanceof ItemConstellationFocus
                && ((ItemConstellationFocus) itemstack1.getItem()).getFocusConstellation(itemstack1) != null) {
                if (this.mergeItemStack(
                    itemstack1,
                    ((ContainerAltarTrait) this).focusSlot.slotNumber,
                    ((ContainerAltarTrait) this).focusSlot.slotNumber + 1,
                    false)) {
                    return itemstack;
                }
            }
            if (index < 27) {
                if (!this.mergeItemStack(itemstack1, 27, 36, false)) {
                    return null;
                }
            } else if (index < 36) {
                if (!this.mergeItemStack(itemstack1, 0, 27, false)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 36, false)) {
                return null;
            }

            if (itemstack1.getCount() == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return null;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        BlockPos pos = this.tileAltar.getPos();
        if (this.tileAltar.getWorld()
            .getTileEntity(pos) != this.tileAltar) {
            return false;
        } else {
            return player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
        }
    }

}
