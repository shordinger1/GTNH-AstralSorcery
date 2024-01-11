package shordinger.wrapper.net.minecraft.inventory;

import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.EnumFacing;

public interface ISidedInventory extends IInventory {

    int[] getSlotsForFace(EnumFacing side);

    /**
     * Returns true if automation can insert the given item in the given slot from the given side.
     */
    boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction);

    /**
     * Returns true if automation can extract the given item in the given slot from the given side.
     */
    boolean canExtractItem(int index, ItemStack stack, EnumFacing direction);
}
