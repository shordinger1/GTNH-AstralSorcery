package com.astralsorcery.gtnh_astralsorcery.common.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.astralsorcery.gtnh_astralsorcery.lib.Utils;

import gregtech.api.util.GT_Log;
import gregtech.api.util.GT_OreDictUnificator;

public enum AstralItemList {

    amulet,
    amulet_gem,
    amulet_shine,
    aquamarine,
    chisel,
    crystal_axe,
    crystal_celestial,
    crystal_pickaxe,
    crystal_rock,
    crystal_shovel,
    crystal_sword,
    glass_lens,
    glass_lens_coloured,
    illumination_powder,
    illumination_wand,
    illumination_wand_overlay,
    infused_crystal_axe,
    infused_crystal_pickaxe,
    infused_crystal_shovel,
    infused_crystal_sword,
    infused_glass,
    infused_glass_engraved,
    linking_tool,
    looking_glass,
    mantle,
    mantle_overlay,
    nocturnal_powder,
    parchment,
    perk_gem_day,
    perk_gem_night,
    perk_gem_sky,
    perk_seal,
    resonating_gem,
    resonator_liquid,
    resonator_starlight,
    resonator_structure,
    scroll_constellation,
    scroll_constellation_overlay,
    scroll_empty,
    scroll_written,
    shifting_star,
    shifting_star_aevitas,
    shifting_star_armara,
    shifting_star_base,
    shifting_star_discidia,
    shifting_star_evorsio,
    shifting_star_vicio,
    stardust,
    starmetal_ingot,
    tome,
    wand,
    wand_architect,
    wand_blink,
    wand_exchange,
    wand_grapple;

    private boolean mHasNotBeenSet;
    private boolean mDeprecated;
    private boolean mWarned;

    private ItemStack mStack;

    // endregion

    AstralItemList() {
        mHasNotBeenSet = true;
    }

    AstralItemList(boolean aDeprecated) {
        if (aDeprecated) {
            mDeprecated = true;
            mHasNotBeenSet = true;
        }
    }

    public Item getItem() {
        sanityCheck();
        if (Utils.isStackInvalid(mStack)) return null;// TODO replace a default issue item
        return mStack.getItem();
    }

    public Block getBlock() {
        sanityCheck();
        return Block.getBlockFromItem(getItem());
    }

    public ItemStack get(int aAmount, Object... aReplacements) {
        sanityCheck();
        // if invalid, return a replacements
        if (Utils.isStackInvalid(mStack)) {
            GT_Log.out.println("Object in the ItemList is null at:");
            new NullPointerException().printStackTrace(GT_Log.out);
            return null;
        }
        return Utils.copyAmount(aAmount, GT_OreDictUnificator.get(mStack));
    }

    public AstralItemList set(Item aItem) {
        mHasNotBeenSet = false;
        if (aItem == null) return this;
        ItemStack aStack = new ItemStack(aItem, 1, 0);
        mStack = Utils.copyAmount(1, aStack);
        return this;
    }

    public AstralItemList set(ItemStack aStack) {
        if (aStack != null) {
            mHasNotBeenSet = false;
            mStack = Utils.copyAmount(1, aStack);
        }
        return this;
    }

    public boolean hasBeenSet() {
        return !mHasNotBeenSet;
    }

    /**
     * Returns the internal stack. This method is unsafe. It's here only for quick operations. DON'T CHANGE THE RETURNED
     * VALUE!
     */
    public ItemStack getInternalStack_unsafe() {
        return mStack;
    }

    private void sanityCheck() {
        if (mHasNotBeenSet)
            throw new IllegalAccessError("The Enum '" + name() + "' has not been set to an Item at this time!");
        if (mDeprecated && !mWarned) {
            new Exception(this + " is now deprecated").printStackTrace(GT_Log.err);
            // warn only once
            mWarned = true;
        }
    }
}
