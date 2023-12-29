/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.container;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import shordinger.astralsorcery.common.item.ItemConstellationPaper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FilteredSlot
 * Created by HellFirePvP
 * Date: 22.11.2016 / 14:43
 */
public class ConstellationPaperSlot extends SlotItemHandler {

    private final ContainerJournal listener;

    public ConstellationPaperSlot(IItemHandler handle, ContainerJournal containerJournal, int index, int xPosition,
                                  int yPosition) {
        super(handle, index, xPosition, yPosition);
        this.listener = containerJournal;
    }

    @Override
    public boolean isItemValid(@Nullable ItemStack stack) {
        return stack.stackSize!=0 && stack.getItem() instanceof ItemConstellationPaper
            && ItemConstellationPaper.getConstellation(stack) != null;
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();

        listener.slotChanged();
    }
}
