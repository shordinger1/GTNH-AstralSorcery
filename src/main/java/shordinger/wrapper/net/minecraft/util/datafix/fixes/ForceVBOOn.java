package shordinger.wrapper.net.minecraft.util.datafix.fixes;

import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.util.datafix.IFixableData;

public class ForceVBOOn implements IFixableData {

    public int getFixVersion() {
        return 505;
    }

    public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
        compound.setString("useVbo", "true");
        return compound;
    }
}
