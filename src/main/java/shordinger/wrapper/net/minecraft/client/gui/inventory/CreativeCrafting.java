package shordinger.wrapper.net.minecraft.client.gui.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.inventory.Container;
import shordinger.wrapper.net.minecraft.inventory.IContainerListener;
import shordinger.wrapper.net.minecraft.inventory.IInventory;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.NonNullList;

@SideOnly(Side.CLIENT)
public class CreativeCrafting implements IContainerListener {

    private final Minecraft mc;

    public CreativeCrafting(Minecraft mc) {
        this.mc = mc;
    }

    /**
     * update the crafting window inventory with the items in the list
     */
    public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {
    }

    /**
     * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual
     * contents of that slot.
     */
    public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
        this.mc.playerController.sendSlotPacket(stack, slotInd);
    }

    /**
     * Sends two ints to the client-side Container. Used for furnace burning time, smelting progress, brewing progress,
     * and enchanting level. Normally the first int identifies which variable to update, and the second contains the new
     * value. Both are truncated to shorts in non-local SMP.
     */
    public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {
    }

    public void sendAllWindowProperties(Container containerIn, IInventory inventory) {
    }
}
