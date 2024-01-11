package shordinger.wrapper.net.minecraft.dispenser;

import shordinger.wrapper.net.minecraft.item.ItemStack;

public interface IBehaviorDispenseItem {

    IBehaviorDispenseItem DEFAULT_BEHAVIOR = new IBehaviorDispenseItem() {

        /**
         * Dispenses the specified ItemStack from a dispenser.
         */
        public ItemStack dispense(IBlockSource source, ItemStack stack) {
            return stack;
        }
    };

    /**
     * Dispenses the specified ItemStack from a dispenser.
     */
    ItemStack dispense(IBlockSource source, ItemStack stack);
}
