package shordinger.wrapper.net.minecraft.client.player.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.InventoryPlayer;
import shordinger.wrapper.net.minecraft.inventory.Container;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;
import shordinger.wrapper.net.minecraft.world.IInteractionObject;

@SideOnly(Side.CLIENT)
public class LocalBlockIntercommunication implements IInteractionObject {

    private final String guiID;
    private final ITextComponent displayName;

    public LocalBlockIntercommunication(String guiIdIn, ITextComponent displayNameIn) {
        this.guiID = guiIdIn;
        this.displayName = displayNameIn;
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the name of this object. For players this returns their username
     */
    public String getName() {
        return this.displayName.getUnformattedText();
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName() {
        return true;
    }

    public String getGuiID() {
        return this.guiID;
    }

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    public ITextComponent getDisplayName() {
        return this.displayName;
    }
}
