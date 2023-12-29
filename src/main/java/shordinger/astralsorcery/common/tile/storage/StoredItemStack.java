/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile.storage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import shordinger.astralsorcery.common.util.ItemUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StoredItemStack
 * Created by HellFirePvP
 * Date: 01.12.2018 / 10:58
 */
public class StoredItemStack {

    private ItemStack stack;
    private int amount;

    StoredItemStack(ItemStack stack) {
        this(stack, stack.stackSize);
    }

    private StoredItemStack(ItemStack stack, int amount) {
        this.stack = ItemUtils.copyStackWithSize(stack, 1);
        this.amount = amount;
    }

    public ItemStack getTemplateStack() {
        return ItemUtils.copyStackWithSize(stack, Math.min(stack.getMaxStackSize(), amount));
    }

    public boolean removeAmount(int amount) {
        if (this.amount - amount < 0) {
            return false;
        }
        this.amount -= amount;
        return true;
    }

    public boolean isEmpty() {
        return this.amount <= 0;
    }

    public int getAmount() {
        return amount;
    }

    public boolean combineIntoThis(StoredItemStack other) {
        if (other.stack.getItem() != stack.getItem()) {
            return false;
        } else if (stack.hasTagCompound() ^ other.stack.hasTagCompound()) {
            return false;
        } else if (stack.hasTagCompound() && !stack.getTagCompound()
            .equals(other.stack.getTagCompound())) {
            return false;
        } else if (stack.getItem()
            .getHasSubtypes() && stack.getMetadata() != other.stack.getMetadata()) {
            return false;
        } else if (!stack.areCapsCompatible(other.stack)) {
            return false;
        } else {
            amount += other.amount;
            return true;
        }
    }

    public boolean combineIntoThis(ItemStack other) {
        if (other.getItem() != stack.getItem()) {
            return false;
        } else if (stack.hasTagCompound() ^ other.hasTagCompound()) {
            return false;
        } else if (stack.hasTagCompound() && !stack.getTagCompound()
            .equals(other.getTagCompound())) {
            return false;
        } else if (stack.getItem()
            .getHasSubtypes() && stack.getMetadata() != other.getMetadata()) {
            return false;
        } else if (!stack.areCapsCompatible(other)) {
            return false;
        } else {
            amount += other.getCount();
            return true;
        }
    }

    @Nonnull
    public NBTTagCompound serialize() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("item", stack.serializeNBT());
        tag.setInteger("amount", amount);
        return tag;
    }

    @Nullable
    public static StoredItemStack deserialize(NBTTagCompound cmp) {
        ItemStack stack = new ItemStack(cmp.getCompoundTag("item"));
        if (stack.stackSize==0) {
            return null;
        }
        int amount = cmp.getInteger("amount");
        return new StoredItemStack(stack, amount);
    }

}
