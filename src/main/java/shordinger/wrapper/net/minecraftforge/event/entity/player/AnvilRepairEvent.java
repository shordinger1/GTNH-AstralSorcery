/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package shordinger.wrapper.net.minecraftforge.event.entity.player;

import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Fired when the player removes a "repaired" item from the Anvil's Output slot.
 * <p>
 * breakChance specifies as a percentage the chance that the anvil will be "damaged" when used.
 * <p>
 * ItemStacks are the inputs/output from the anvil. They cannot be edited.
 */
public class AnvilRepairEvent extends PlayerEvent {

    @Nonnull
    private final ItemStack left; // The left side of the input
    @Nonnull
    private final ItemStack right; // The right side of the input
    @Nonnull
    private final ItemStack output; // Set this to set the output stack
    private float breakChance; // Anvil's chance to break (reduced by 1 durability) when this is complete. Default is
    // 12% (0.12f)

    public AnvilRepairEvent(EntityPlayer player, @Nonnull ItemStack left, @Nonnull ItemStack right,
                            @Nonnull ItemStack output) {
        super(player);
        this.output = output;
        this.left = left;
        this.right = right;
        this.setBreakChance(0.12f);
    }

    /**
     * Deprecated in favour of {@link #getItemInput()} - this is actually the output slot of the anvil
     *
     * @return the output slot
     */
    @Deprecated
    @Nonnull
    public ItemStack getLeft() {
        return output;
    }

    /**
     * Deprecated in favour of {@link #getIngredientInput()}} - this is actually the first input slot of the anvil
     *
     * @return the first input slot
     */
    @Deprecated
    @Nonnull
    public ItemStack getRight() {
        return left;
    }

    /**
     * Deprecated in favour of {@link #getItemResult()} - this is actually the second input slot of the anvil
     *
     * @return the second input slot
     */
    @Deprecated
    @Nonnull
    public ItemStack getOutput() {
        return right;
    }

    /**
     * Get the output result from the anvil
     *
     * @return the output
     */
    @Nonnull
    public ItemStack getItemResult() {
        return output;
    }

    /**
     * Get the first item input into the anvil
     *
     * @return the first input slot
     */
    @Nonnull
    public ItemStack getItemInput() {
        return left;
    }

    /**
     * Get the second item input into the anvil
     *
     * @return the second input slot
     */
    @Nonnull
    public ItemStack getIngredientInput() {
        return right;
    }

    public float getBreakChance() {
        return breakChance;
    }

    public void setBreakChance(float breakChance) {
        this.breakChance = breakChance;
    }
}
