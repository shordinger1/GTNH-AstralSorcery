package shordinger.wrapper.net.minecraft.tileentity;

import shordinger.wrapper.net.minecraft.util.datafix.DataFixer;
import shordinger.wrapper.net.minecraft.util.datafix.FixTypes;
import shordinger.wrapper.net.minecraft.util.datafix.walkers.ItemStackDataLists;

public class TileEntityDropper extends TileEntityDispenser {

    public static void registerFixesDropper(DataFixer fixer) {
        fixer.registerWalker(
            FixTypes.BLOCK_ENTITY,
            new ItemStackDataLists(TileEntityDropper.class, new String[]{"Items"}));
    }

    /**
     * Get the name of this object. For players this returns their username
     */
    public String getName() {
        return this.hasCustomName() ? this.customName : "container.dropper";
    }

    public String getGuiID() {
        return "minecraft:dropper";
    }
}
