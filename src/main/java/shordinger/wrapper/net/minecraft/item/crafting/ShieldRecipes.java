package shordinger.wrapper.net.minecraft.item.crafting;

import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.inventory.InventoryCrafting;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.world.World;

public class ShieldRecipes {

    public static class Decoration extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe>
        implements IRecipe {

        /**
         * Used to check if a recipe matches current crafting inventory
         */
        public boolean matches(InventoryCrafting inv, World worldIn) {
            ItemStack itemstack = ItemStack.EMPTY;
            ItemStack itemstack1 = ItemStack.EMPTY;

            for (int i = 0; i < inv.getSizeInventory(); ++i) {
                ItemStack itemstack2 = inv.getStackInSlot(i);

                if (!itemstack2.isEmpty()) {
                    if (itemstack2.getItem() == Items.BANNER) {
                        if (!itemstack1.isEmpty()) {
                            return false;
                        }

                        itemstack1 = itemstack2;
                    } else {
                        if (itemstack2.getItem() != Items.SHIELD) {
                            return false;
                        }

                        if (!itemstack.isEmpty()) {
                            return false;
                        }

                        if (itemstack2.getSubCompound("BlockEntityTag") != null) {
                            return false;
                        }

                        itemstack = itemstack2;
                    }
                }
            }

            if (!itemstack.isEmpty() && !itemstack1.isEmpty()) {
                return true;
            } else {
                return false;
            }
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
                    if (itemstack2.getItem() == Items.BANNER) {
                        itemstack = itemstack2;
                    } else if (itemstack2.getItem() == Items.SHIELD) {
                        itemstack1 = itemstack2.copy();
                    }
                }
            }

            if (itemstack1.isEmpty()) {
                return itemstack1;
            } else {
                NBTTagCompound nbttagcompound = itemstack.getSubCompound("BlockEntityTag");
                NBTTagCompound nbttagcompound1 = nbttagcompound == null ? new NBTTagCompound() : nbttagcompound.copy();
                nbttagcompound1.setInteger("Base", itemstack.getMetadata() & 15);
                itemstack1.setTagInfo("BlockEntityTag", nbttagcompound1);
                return itemstack1;
            }
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
