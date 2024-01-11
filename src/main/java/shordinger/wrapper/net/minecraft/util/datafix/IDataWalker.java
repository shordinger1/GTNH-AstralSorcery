package shordinger.wrapper.net.minecraft.util.datafix;

import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;

public interface IDataWalker {

    NBTTagCompound process(IDataFixer fixer, NBTTagCompound compound, int versionIn);
}
