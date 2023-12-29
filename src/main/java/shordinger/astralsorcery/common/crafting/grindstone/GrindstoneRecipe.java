/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.grindstone;

import java.util.Objects;
import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.migration.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GrindstoneRecipe
 * Created by HellFirePvP
 * Date: 19.11.2017 / 10:22
 */
public class GrindstoneRecipe {

    protected static final Random rand = new Random();

    protected final ItemHandle input;
    protected final ItemStack output;
    protected final int chance;
    protected final float doubleChance;

    public GrindstoneRecipe(ItemStack input, ItemStack output, int chance) {
        this(input, output, chance, 0F);
    }

    public GrindstoneRecipe(ItemHandle input, ItemStack output, int chance) {
        this(input, output, chance, 0F);
    }

    public GrindstoneRecipe(ItemStack input, ItemStack output, int chance, float doubleChance) {
        this.input = new ItemHandle(input);
        this.output = output;
        this.chance = chance;
        this.doubleChance = doubleChance;
    }

    public GrindstoneRecipe(ItemHandle input, ItemStack output, int chance, float doubleChance) {
        this.input = input;
        this.output = output;
        this.chance = chance;
        this.doubleChance = doubleChance;
    }

    public boolean matches(ItemStack stackIn) {
        return this.input.matchCrafting(stackIn);
    }

    public boolean isValid() {
        return this.input.getApplicableItems()
            .size() > 0 && !this.output.isEmpty();
    }

    public float getChanceToDoubleOutput() {
        return MathHelper.clamp(this.doubleChance, 0, 1);
    }

    @Nonnull
    public GrindResult grind(ItemStack stackIn) {
        if (rand.nextInt(chance) == 0) {
            int out = this.output.stackSize;
            if (rand.nextFloat() <= getChanceToDoubleOutput()) {
                out *= 2;
            }
            return GrindResult.itemChange(Objects.requireNonNull(ItemUtils.copyStackWithSize(this.output, out)));
        }
        return GrindResult.failNoOp();
    }

    @Nonnull
    public ItemStack getOutputForMatching() {
        return this.output;
    }

    @Nonnull
    public ItemHandle getOutputForRender() {
        return new ItemHandle(this.output);
    }

    @Nonnull
    public ItemHandle getInputForRender() {
        return this.input;
    }

    public static class GrindResult {

        private final ResultType type;
        private final ItemStack stack;

        private GrindResult(ResultType type, ItemStack stack) {
            this.type = type;
            this.stack = stack;
        }

        public ResultType getType() {
            return type;
        }

        @Nonnull
        public ItemStack getStack() {
            return stack;
        }

        public static GrindResult success() {
            return new GrindResult(ResultType.SUCCESS, null);
        }

        public static GrindResult itemChange(@Nonnull ItemStack newStack) {
            return new GrindResult(ResultType.ITEMCHANGE, newStack);
        }

        public static GrindResult failNoOp() {
            return new GrindResult(ResultType.FAIL_SILENT, null);
        }

        public static GrindResult failBreakItem() {
            return new GrindResult(ResultType.FAIL_BREAK_ITEM, null);
        }

    }

    public static enum ResultType {

        SUCCESS, // Successfully grinded something
        ITEMCHANGE, // Successfully grinded something, other item now on the grindstone
        FAIL_SILENT, // Did nothing, but nothing went wrong. just.. uuuh.. nothing.
        FAIL_BREAK_ITEM // The item broke while grinding.

    }
}
