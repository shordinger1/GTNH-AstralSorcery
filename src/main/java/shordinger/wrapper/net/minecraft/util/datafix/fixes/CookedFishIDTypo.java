package shordinger.wrapper.net.minecraft.util.datafix.fixes;

import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.datafix.IFixableData;

public class CookedFishIDTypo implements IFixableData {

    private static final ResourceLocation WRONG = new ResourceLocation("cooked_fished");

    public int getFixVersion() {
        return 502;
    }

    public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
        if (compound.hasKey("id", 8) && WRONG.equals(new ResourceLocation(compound.getString("id")))) {
            compound.setString("id", "minecraft:cooked_fish");
        }

        return compound;
    }
}