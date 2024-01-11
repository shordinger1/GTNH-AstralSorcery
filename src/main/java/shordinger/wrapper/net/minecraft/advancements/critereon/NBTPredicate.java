package shordinger.wrapper.net.minecraft.advancements.critereon;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import shordinger.wrapper.net.minecraft.command.CommandBase;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import shordinger.wrapper.net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.nbt.NBTUtil;
import shordinger.wrapper.net.minecraft.util.JsonUtils;

public class NBTPredicate {

    /**
     * The predicate that matches any NBT tag.
     */
    public static final NBTPredicate ANY = new NBTPredicate((NBTTagCompound) null);
    @Nullable
    private final NBTTagCompound tag;

    public NBTPredicate(@Nullable NBTTagCompound tag) {
        this.tag = tag;
    }

    public boolean test(ItemStack item) {
        return this == ANY ? true : this.test(item.getTagCompound());
    }

    public boolean test(Entity entityIn) {
        return this == ANY ? true : this.test(CommandBase.entityToNBT(entityIn));
    }

    public boolean test(@Nullable NBTBase nbt) {
        if (nbt == null) {
            return this == ANY;
        } else {
            return this.tag == null || NBTUtil.areNBTEquals(this.tag, nbt, true);
        }
    }

    public static NBTPredicate deserialize(@Nullable JsonElement json) {
        if (json != null && !json.isJsonNull()) {
            NBTTagCompound nbttagcompound;

            try {
                nbttagcompound = JsonToNBT.getTagFromJson(JsonUtils.getString(json, "nbt"));
            } catch (NBTException nbtexception) {
                throw new JsonSyntaxException("Invalid nbt tag: " + nbtexception.getMessage());
            }

            return new NBTPredicate(nbttagcompound);
        } else {
            return ANY;
        }
    }
}
