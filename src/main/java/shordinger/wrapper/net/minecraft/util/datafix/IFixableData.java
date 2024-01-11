package shordinger.wrapper.net.minecraft.util.datafix;

import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;

public interface IFixableData {

    int getFixVersion();

    NBTTagCompound fixTagCompound(NBTTagCompound compound);
}
