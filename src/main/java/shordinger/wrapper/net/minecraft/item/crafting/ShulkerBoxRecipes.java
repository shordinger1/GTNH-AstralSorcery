package shordinger.wrapper.net.minecraft.item.crafting;

import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.BlockShulkerBox;
import shordinger.wrapper.net.minecraft.inventory.InventoryCrafting;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.world.World;

public class ShulkerBoxRecipes {

    public static class ShulkerBoxColoring extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe>
        implements IRecipe {

        /**
         * Used to check if a recipe matches current crafting inventory
         */
        public boolean matches(InventoryCrafting inv, World worldIn) {
            int i = 0;
            int j = 0;

            for (int k = 0; k < inv.getSizeInventory(); ++k) {
                ItemStack itemstack = inv.getStackInSlot(k);

                if (!itemstack.isEmpty()) {
                    if (Block.getBlockFromItem(itemstack.getItem()) instanceof BlockShulkerBox) {
                        ++i;
                    } else {
                        if (!net.minecraftforge.oredict.DyeUtils.isDye(itemstack)) {
                            return false;
                        }

                        ++j;
                    }

                    if (j > 1 || i > 1) {
                        return false;
                    }
                }
            }

            return i == 1 && j == 1;
        }

        /**
         * Returns an Item that is the result of this recipe
         */
        public ItemStack getCraftingResult(InventoryCrafting inv) {
            ItemStack itemstack = ItemStack.EMPTY;
            ItemStack itemstack1 = ItemStack.EMPTY;

            for (int i = 0; i < inv.getSizeInventory(); ++i) {
                ItemStack itemstack2 = inv.getStackInSlot(i);

                if (!itemstack2.isEmpty()) {
                    if (Block.getBlockFromItem(itemstack2.getItem()) instanceof BlockShulkerBox) {
                        itemstack = itemstack2;
                    } else if (net.minecraftforge.oredict.DyeUtils.isDye(itemstack2)) {
                        itemstack1 = itemstack2;
                    }
                }
            }

            ItemStack itemstack3 = BlockShulkerBox.getColoredItemStack(
                net.minecraftforge.oredict.DyeUtils.colorFromStack(itemstack1)
                    .get());

            if (itemstack.hasTagCompound()) {
                itemstack3.setTagCompound(
                    itemstack.getTagCompound()
                        .copy());
            }

            return itemstack3;
        }

        /**
         * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more
         * than one possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
         */
        public ItemStack getRecipeOutput() {
            return ItemStack.EMPTY;
        }

        public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
            NonNullList<ItemStack> nonnulllist = NonNullList
                .<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);

            for (int i = 0; i < nonnulllist.size(); ++i) {
                ItemStack itemstack = inv.getStackInSlot(i);

                if (itemstack.getItem()
                    .hasContainerItem()) {
                    nonnulllist.set(
                        i,
                        new ItemStack(
                            itemstack.getItem()
                                .getContainerItem()));
                }
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
}
