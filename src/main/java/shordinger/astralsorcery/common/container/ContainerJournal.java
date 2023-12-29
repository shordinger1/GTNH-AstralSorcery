/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.container;

import com.gtnewhorizons.modularui.api.forge.IItemHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.item.ItemConstellationPaper;
import shordinger.astralsorcery.common.item.ItemJournal;

import java.util.LinkedList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerJournal
 * Created by HellFirePvP
 * Date: 22.11.2016 / 14:33
 */
public class ContainerJournal extends Container {

    private final ItemStack parentJournal;
    private final int journalIndex;

    public ContainerJournal(InventoryPlayer playerInv, ItemStack journal, int journalIndex) {
        this.parentJournal = journal;
        this.journalIndex = journalIndex;
        buildPlayerSlots(playerInv);
        buildSlots(new InvWrapper(ItemJournal.getJournalStorage(journal)));
    }

    private void buildPlayerSlots(InventoryPlayer playerInv) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                int index = j + i * 9 + 9;

                if (index == journalIndex) {
                    addSlotToContainer(new ContainerSlotUnclickable(playerInv, index, 8 + j * 18, 84 + i * 18));
                } else {
                    addSlotToContainer(new Slot(playerInv, index, 8 + j * 18, 84 + i * 18));
                }
            }
        }
        for (int i = 0; i < 9; i++) {
            if (i == journalIndex) {
                addSlotToContainer(new ContainerSlotUnclickable(playerInv, i, 8 + i * 18, 142));
            } else {
                addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
            }
        }
    }

    private void buildSlots(IItemHandler handle) {
        for (int i = 0; i < 3; i++) {
            for (int xx = 0; xx < 9; xx++) {
                addSlotToContainer(new ConstellationPaperSlot(handle, this, (i * 9) + xx, 8 + xx * 18, 13 + (i * 18)));
            }
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = null;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (!itemstack1.isEmpty() && itemstack1.getItem() instanceof ItemConstellationPaper
                && ItemConstellationPaper.getConstellation(itemstack1) != null) {
                if (index < 36) {
                    if (!this.mergeItemStack(itemstack1, 36, 63, false)) {
                        return null;
                    }
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
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    void slotChanged() {
        if (FMLCommonHandler.instance()
            .getEffectiveSide() == Side.SERVER) {
            LinkedList<IConstellation> saveConstellations = new LinkedList<>();
            for (int i = 36; i < 63; i++) {
                ItemStack in = inventorySlots.get(i)
                    .getStack();
                if (in.isEmpty()) continue;
                IConstellation c = ItemConstellationPaper.getConstellation(in);
                if (c != null) {
                    saveConstellations.add(c);
                }
            }
            ItemJournal.setStoredConstellations(parentJournal, saveConstellations);
        }
    }

}
