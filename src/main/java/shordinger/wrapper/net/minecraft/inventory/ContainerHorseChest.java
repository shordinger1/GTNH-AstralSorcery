package shordinger.wrapper.net.minecraft.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;

public class ContainerHorseChest extends InventoryBasic {

    public ContainerHorseChest(String inventoryTitle, int slotCount) {
        super(inventoryTitle, false, slotCount);
    }

    @SideOnly(Side.CLIENT)
    public ContainerHorseChest(ITextComponent inventoryTitle, int slotCount) {
        super(inventoryTitle, slotCount);
    }
}
