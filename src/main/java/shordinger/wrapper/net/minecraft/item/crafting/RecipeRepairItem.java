package shordinger.wrapper.net.minecraft.item.crafting;

import java.util.List;

import com.google.common.collect.Lists;

import shordinger.wrapper.net.minecraft.inventory.InventoryCrafting;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.world.World;

public class RecipeRepairItem extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe>
    implements IRecipe {

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(InventoryCrafting inv, World worldIn) {
        List<ItemStack> list = Lists.<ItemStack>newArrayList();

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);

            if (!itemstack.isEmpty()) {
                list.add(itemstack);

                if (list.size() > 1) {
                    ItemStack itemstack1 = list.get(0);

                    if (itemstack.getItem() != itemstack1.getItem() || itemstack1.getCount() != 1
                        || itemstack.getCount() != 1
                        || !itemstack1.getItem()
                        .isRepairable()) {
                        return false;
                    }
                }
            }
        }

        return list.size() == 2;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        List<ItemStack> list = Lists.<ItemStack>newArrayList();

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);

            if (!itemstack.isEmpty()) {
                list.add(itemstack);

                if (list.size() > 1) {
                    ItemStack itemstack1 = list.get(0);

                    if (itemstack.getItem() != itemstack1.getItem() || itemstack1.getCount() != 1
                        || itemstack.getCount() != 1
                        || !itemstack1.getItem()
                        .isRepairable()) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        if (list.size() == 2) {
            ItemStack itemstack2 = list.get(0);
            ItemStack itemstack3 = list.get(1);

            if (itemstack2.getItem() == itemstack3.getItem() && itemstack2.getCount() == 1
                && itemstack3.getCount() == 1
                && itemstack2.getItem()
                .isRepairable()) {
                // FORGE: Make itemstack sensitive // Item item = itemstack2.getItem();
                int j = itemstack2.getMaxDamage() - itemstack2.getItemDamage();
                int k = itemstack2.getMaxDamage() - itemstack3.getItemDamage();
                int l = j + k + itemstack2.getMaxDamage() * 5 / 100;
                int i1 = itemstack2.getMaxDamage() - l;

                if (i1 < 0) {
                    i1 = 0;
                }

                return new ItemStack(itemstack2.getItem(), 1, i1);
            }
        }

        return ItemStack.EMPTY;
    }

    /**
     * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
     * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
     */
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            nonnulllist.set(i, net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
        }

        return nonnulllist;
    }

    /**
     * If true, this recipe does not appear in the recipe book and does not respect recipe unlocking (and the
     * doLimitedCrafting gamerule)
     */
    public boolean isDynamic() {
        return true;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }
}
