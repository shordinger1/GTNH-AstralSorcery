package shordinger.wrapper.net.minecraft.util.datafix.fixes;

import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.util.datafix.IFixableData;

public class ArmorStandSilent implements IFixableData {

    public int getFixVersion() {
        return 147;
    }

    public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
        if ("ArmorStand".equals(compound.getString("id")) && compound.getBoolean("Silent")
            && !compound.getBoolean("Marker")) {
            compound.removeTag("Silent");
        }

        return compound;
    }
}
