package shordinger.wrapper.net.minecraft.util.datafix.fixes;

import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.util.datafix.IFixableData;

public class ShulkerBoxTileColor implements IFixableData {

    public int getFixVersion() {
        return 813;
    }

    public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
        if ("minecraft:shulker".equals(compound.getString("id"))) {
            compound.removeTag("Color");
        }

        return compound;
    }
}
