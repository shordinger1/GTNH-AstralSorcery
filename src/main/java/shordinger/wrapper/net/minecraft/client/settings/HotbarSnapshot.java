package shordinger.wrapper.net.minecraft.client.settings;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.player.InventoryPlayer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.nbt.NBTTagList;

@SideOnly(Side.CLIENT)
public class HotbarSnapshot extends ArrayList<ItemStack> {

    public static final int HOTBAR_SIZE = InventoryPlayer.getHotbarSize();

    public HotbarSnapshot() {
        this.ensureCapacity(HOTBAR_SIZE);

        for (int i = 0; i < HOTBAR_SIZE; ++i) {
            this.add(ItemStack.EMPTY);
        }
    }

    public NBTTagList createTag() {
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < HOTBAR_SIZE; ++i) {
            nbttaglist.appendTag(((ItemStack) this.get(i)).writeToNBT(new NBTTagCompound()));
        }

        return nbttaglist;
    }

    public void fromTag(NBTTagList p_192833_1_) {
        for (int i = 0; i < HOTBAR_SIZE; ++i) {
            this.set(i, new ItemStack(p_192833_1_.getCompoundTagAt(i)));
        }
    }

    public boolean isEmpty() {
        for (int i = 0; i < HOTBAR_SIZE; ++i) {
            if (!((ItemStack) this.get(i)).isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
