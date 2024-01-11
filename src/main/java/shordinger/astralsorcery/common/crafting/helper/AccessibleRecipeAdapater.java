/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.helper;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.wrapper.net.minecraft.inventory.InventoryCrafting;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.item.crafting.IRecipe;
import shordinger.wrapper.net.minecraft.item.crafting.Ingredient;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.oredict.OreDictionary;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AccessibleRecipeAdapater
 * Created by HellFirePvP
 * Date: 06.10.2016 / 14:26
 */
public class AccessibleRecipeAdapater extends AccessibleRecipe {

    private final IRecipe parent;
    private final AbstractRecipeAccessor abstractRecipe;

    public AccessibleRecipeAdapater(IRecipe parent, AbstractRecipeAccessor abstractRecipe) {
        super(parent.getRegistryName());
        this.parent = parent;
        this.abstractRecipe = abstractRecipe;
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public NonNullList<ItemStack> getExpectedStackForRender(int row, int column) {
        ItemHandle handle = abstractRecipe.getExpectedStack(row, column);
        if (handle == null) return NonNullList.create();
        return refactorSubItems(handle.getApplicableItemsForRender());
    }

    @Nullable
    public ItemHandle getExpectedStackHandle(int row, int column) {
        return abstractRecipe.getExpectedStack(row, column);
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public NonNullList<ItemStack> getExpectedStackForRender(ShapedRecipeSlot slot) {
        ItemHandle handle = abstractRecipe.getExpectedStack(slot);
        if (handle == null) return NonNullList.create();
        return refactorSubItems(handle.getApplicableItemsForRender());
    }

    @Nullable
    public ItemHandle getExpectedStackHandle(ShapedRecipeSlot slot) {
        return abstractRecipe.getExpectedStack(slot);
    }

    @SideOnly(Side.CLIENT)
    private NonNullList<ItemStack> refactorSubItems(NonNullList<ItemStack> applicableItems) {
        NonNullList<ItemStack> out = NonNullList.create();
        for (ItemStack oreDictIn : applicableItems) {
            if (oreDictIn.getItemDamage() == OreDictionary.WILDCARD_VALUE && !oreDictIn.isItemStackDamageable()) {
                oreDictIn.getItem()
                    .getSubItems(
                        oreDictIn.getItem()
                            .getCreativeTab(),
                        out);
            } else {
                out.add(oreDictIn);
            }
        }
        return out;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return parent.getIngredients();
    }

    @Override
    public String getGroup() {
        return parent.getGroup();
    }

    @Override
    public boolean isDynamic() {
        return parent.isDynamic();
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        return parent.matches(inv, worldIn);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return parent.getCraftingResult(inv);
    }

    @Override
    public boolean canFit(int width, int height) {
        return parent.canFit(width, height);
    }

    @Override
    public ItemStack getRecipeOutput() {
        return parent.getRecipeOutput();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        return parent.getRemainingItems(inv);
    }

    public IRecipe getParentRecipe() {
        return parent;
    }
}
