package com.astralsorcery.gtnh_astralsorcery.lib;

import static com.astralsorcery.gtnh_astralsorcery.AstralSorcery.LOG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.metatileentity.MetaTileEntity;

public final class Utils {

    public static boolean metaItemEqual(ItemStack a, ItemStack b) {
        return a.getItem() == b.getItem() && a.getItemDamage() == b.getItemDamage();
    }

    /**
     *
     * @param isa1 The ItemStack Array 1.
     * @param isa2 The ItemStack Array 2.
     * @return The elements of these two arrays are identical and in the same order.
     */
    public static boolean itemStackArrayEqualAbsolutely(ItemStack[] isa1, ItemStack[] isa2) {
        if (isa1.length != isa2.length) return false;
        for (int i = 0; i < isa1.length; i++) {
            if (!metaItemEqual(isa1[i], isa2[i])) return false;
            if (isa1[i].stackSize != isa2[i].stackSize) return false;
        }
        return true;
    }

    public static boolean itemStackArrayEqualFuzzy(ItemStack[] isa1, ItemStack[] isa2) {
        if (isa1.length != isa2.length) return false;
        for (ItemStack itemStack1 : isa1) {
            boolean flag = false;
            for (ItemStack itemStack2 : isa2) {
                if (metaItemEqual(itemStack1, itemStack2)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) return false;
        }
        return true;
    }

    public static boolean fluidStackEqualAbsolutely(FluidStack[] fsa1, FluidStack[] fsa2) {
        if (fsa1.length != fsa2.length) return false;
        for (int i = 0; i < fsa1.length; i++) {
            if (!fluidEqual(fsa1[i], fsa2[i])) return false;
            if (fsa1[i].amount != fsa2[i].amount) return false;
        }
        return true;
    }

    public static boolean fluidStackEqualFuzzy(FluidStack[] fsa1, FluidStack[] fsa2) {
        if (fsa1.length != fsa2.length) return false;
        for (FluidStack fluidStack1 : fsa1) {
            boolean flag = false;
            for (FluidStack fluidStack2 : fsa2) {
                if (fluidEqual(fluidStack1, fluidStack2)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) return false;
        }
        return true;
    }

    public static boolean fluidEqual(FluidStack a, FluidStack b) {
        return a.getFluid() == b.getFluid();
    }

    public static ItemStack copyAmount(int aAmount, ItemStack aStack) {
        ItemStack rStack = aStack.copy();
        if (isStackInvalid(rStack)) return null;
        // if (aAmount > 64) aAmount = 64;
        else if (aAmount == -1) aAmount = 111;
        else if (aAmount < 0) aAmount = 0;
        rStack.stackSize = aAmount;
        return rStack;
    }

    public static boolean isStackValid(ItemStack aStack) {
        return (aStack != null) && aStack.getItem() != null && aStack.stackSize >= 0;
    }

    public static boolean isStackInvalid(ItemStack aStack) {
        return aStack == null || aStack.getItem() == null || aStack.stackSize < 0;
    }

    public static ItemStack setStackSize(ItemStack itemStack, int amount) {
        if (itemStack == null) return null;
        if (amount < 0) {
            LOG.info("Error! Trying to set a item stack size lower than zero! " + itemStack + " to amount " + amount);
            return itemStack;
        }
        itemStack.stackSize = amount;
        return itemStack;
    }

    public static <T extends Collection<E>, E extends MetaTileEntity> T filterValidMTEs(T metaTileEntities) {
        metaTileEntities.removeIf(mte -> mte == null || !mte.isValid());
        return metaTileEntities;
    }

    public static <T> T[] mergeArrays(T[]... arrays) {
        List<T> totals = new ArrayList<>();
        for (T[] array : arrays) {
            totals.addAll(Arrays.asList(array));
        }
        return (T[]) totals.toArray(new Object[0]);
    }

}
