package shordinger.wrapper.net.minecraft.world;

import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.InventoryPlayer;
import shordinger.wrapper.net.minecraft.inventory.Container;

public interface IInteractionObject extends IWorldNameable {

    Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn);

    String getGuiID();
}
