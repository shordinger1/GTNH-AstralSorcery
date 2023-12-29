/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.helper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.migration.NonNullList;

import javax.annotation.Nullable;

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
