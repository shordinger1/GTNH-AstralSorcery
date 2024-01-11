package shordinger.wrapper.net.minecraft.util.datafix.fixes;

import java.util.Locale;

import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.util.datafix.IFixableData;

public class OptionsLowerCaseLanguage implements IFixableData {

    public int getFixVersion() {
        return 816;
    }

    public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
        if (compound.hasKey("lang", 8)) {
            compound.setString(
                "lang",
                compound.getString("lang")
                    .toLowerCase(Locale.ROOT));
        }

        return compound;
    }
}
