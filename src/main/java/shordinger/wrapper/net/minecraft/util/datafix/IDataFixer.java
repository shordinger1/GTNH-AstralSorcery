package shordinger.wrapper.net.minecraft.util.datafix;

import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;

public interface IDataFixer {

    NBTTagCompound process(IFixType type, NBTTagCompound compound, int versionIn);
}
