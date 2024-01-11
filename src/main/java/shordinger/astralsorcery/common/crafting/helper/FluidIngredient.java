/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import shordinger.wrapper.net.minecraft.client.util.RecipeItemHelper;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.item.crafting.Ingredient;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraftforge.fluids.Fluid;
import shordinger.wrapper.net.minecraftforge.fluids.FluidStack;
import shordinger.wrapper.net.minecraftforge.fluids.FluidUtil;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FluidIngredient
 * Created by HellFirePvP
 * Date: 28.12.2018 / 15:07
 */
public class FluidIngredient extends Ingredient {

    private final List<FluidStack> fluidsIn;
    private IntList itemIds = null;
    private ItemStack[] itemArray = null;
    private int cacheItemStacks = -1, cacheItemIds = -1;

    public FluidIngredient(FluidStack... fluids) {
        super(0);
        this.fluidsIn = Arrays.asList(fluids);
    }

    public FluidIngredient(Fluid... fluids) {
        super(0);
        this.fluidsIn = new ArrayList<>(fluids.length);
        for (Fluid f : fluids) {
            fluidsIn.add(new FluidStack(f, Fluid.BUCKET_VOLUME));
        }
    }

    @Override
    @Nonnull
    public ItemStack[] getMatchingStacks() {
        if (itemArray == null || this.cacheItemStacks != this.fluidsIn.size()) {
            NonNullList<ItemStack> lst = NonNullList.create();

            for (FluidStack fluid : this.fluidsIn) {
                lst.add(FluidUtil.getFilledBucket(fluid));
            }

            this.itemArray = lst.toArray(new ItemStack[lst.size()]);
            this.cacheItemStacks = this.fluidsIn.size();
        }
        return this.itemArray;
    }

    @Override
    @Nonnull
    public IntList getValidItemStacksPacked() {
        if (this.itemIds == null || this.cacheItemIds != fluidsIn.size()) {
            this.itemIds = new IntArrayList(this.fluidsIn.size());

            for (FluidStack fluid : this.fluidsIn) {
                ItemStack bucketFluid = FluidUtil.getFilledBucket(fluid);
                this.itemIds.add(RecipeItemHelper.pack(bucketFluid));
            }

            this.itemIds.sort(IntComparators.NATURAL_COMPARATOR);
            this.cacheItemIds = this.fluidsIn.size();
        }

        return this.itemIds;
    }

    @Override
    public boolean apply(@Nullable ItemStack input) {
        if (input == null) {
            return false;
        }

        FluidStack contained = FluidUtil.getFluidContained(input);
        if (contained == null || contained.amount <= 0) {
            return false;
        }

        for (FluidStack target : this.fluidsIn) {
            if (contained.containsFluid(target)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void invalidate() {
        this.itemIds = null;
        this.itemArray = null;
    }

    @Override
    public boolean isSimple() {
        return false;
    }
}
