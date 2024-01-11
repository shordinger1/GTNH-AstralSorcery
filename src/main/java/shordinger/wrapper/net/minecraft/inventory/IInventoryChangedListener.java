package shordinger.wrapper.net.minecraft.inventory;

public interface IInventoryChangedListener {

    /**
     * Called by InventoryBasic.onInventoryChanged() on a array that is never filled.
     */
    void onInventoryChanged(IInventory invBasic);
}
