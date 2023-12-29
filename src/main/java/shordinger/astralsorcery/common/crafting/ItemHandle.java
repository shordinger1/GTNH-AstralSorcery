/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting;

import appeng.recipes.Ingredient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import shordinger.astralsorcery.common.crafting.helper.FluidIngredient;
import shordinger.astralsorcery.common.item.base.render.ItemGatedVisibility;
import shordinger.astralsorcery.common.lib.ItemsAS;
import shordinger.astralsorcery.common.util.ByteBufUtils;
import shordinger.astralsorcery.common.util.ItemComparator;
import shordinger.astralsorcery.common.util.ItemUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemHandle
 * Created by HellFirePvP
 * Date: 26.12.2016 / 15:13
 */
public final class ItemHandle {

    private static final Constructor<CompoundIngredient> COMPOUND_CTOR;

    public static ItemHandle EMPTY = new ItemHandle();
    public static boolean ignoreGatingRequirement = true;

    public final Type handleType;

    private final List<ItemStack> applicableItems = new LinkedList<>();
    private String oreDictName = null;
    private FluidStack fluidTypeAndAmount = null;

    /*
     * PLEASE do not use this without populating the respective fields...
     */
    private ItemHandle(Type type) {
        this.handleType = type;
    }

    private ItemHandle() {
        this(Type.STACK);
        applicableItems.add(null);
    }

    public ItemHandle(String oreDictName) {
        this.oreDictName = oreDictName;
        this.handleType = Type.OREDICT;
    }

    public ItemHandle(Fluid fluid) {
        this.fluidTypeAndAmount = new FluidStack(fluid, 1000);
        this.handleType = Type.FLUID;
    }

    public ItemHandle(Fluid fluid, int mbAmount) {
        this.fluidTypeAndAmount = new FluidStack(fluid, mbAmount);
        this.handleType = Type.FLUID;
    }

    public ItemHandle(FluidStack compareStack) {
        this.fluidTypeAndAmount = compareStack.copy();
        this.handleType = Type.FLUID;
    }

    public ItemHandle(@Nonnull ItemStack matchStack) {
        this.applicableItems.add(ItemUtils.copyStackWithSize(matchStack, matchStack.getCount()));
        this.handleType = Type.STACK;
    }

    public ItemHandle(@Nonnull ItemStack... matchStacks) {
        for (ItemStack stack : matchStacks) {
            this.applicableItems.add(ItemUtils.copyStackWithSize(stack, stack.getCount()));
        }
        this.handleType = Type.STACK;
    }

    public ItemHandle(NonNullList<ItemStack> matchStacks) {
        for (ItemStack stack : matchStacks) {
            this.applicableItems.add(ItemUtils.copyStackWithSize(stack, stack.getCount()));
        }
        this.handleType = Type.STACK;
    }

    public static ItemHandle of(Ingredient ingredient) {
        return new ItemHandle(ingredient.getMatchingStacks());
    }

    public static ItemHandle getCrystalVariant(boolean hasToBeTuned, boolean hasToBeCelestial) {
        if (hasToBeTuned) {
            if (hasToBeCelestial) {
                return new ItemHandle(new ItemStack(ItemsAS.tunedCelestialCrystal));
            }

            ItemHandle handle = new ItemHandle(new ItemStack(ItemsAS.tunedRockCrystal));
            handle.applicableItems.add(new ItemStack(ItemsAS.tunedCelestialCrystal));
            return handle;
        } else {
            if (hasToBeCelestial) {
                ItemHandle handle = new ItemHandle(new ItemStack(ItemsAS.celestialCrystal));
                handle.applicableItems.add(new ItemStack(ItemsAS.tunedCelestialCrystal));
                return handle;
            }

            ItemHandle handle = new ItemHandle(new ItemStack(ItemsAS.rockCrystal));
            handle.applicableItems.add(new ItemStack(ItemsAS.celestialCrystal));
            handle.applicableItems.add(new ItemStack(ItemsAS.tunedRockCrystal));
            handle.applicableItems.add(new ItemStack(ItemsAS.tunedCelestialCrystal));
            return handle;
        }
    }

    public NonNullList<ItemStack> getApplicableItems() {
        if (oreDictName != null) {
            NonNullList<ItemStack> stacks = OreDictionary.getOres(oreDictName);

            NonNullList<ItemStack> out = NonNullList.create();
            for (ItemStack oreDictIn : stacks) {
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
        } else if (fluidTypeAndAmount != null) {
            return NonNullList.withSize(
                1,
                UniversalBucket
                    .getFilledBucket(ForgeModContainer.getInstance().universalBucket, fluidTypeAndAmount.getFluid()));
        } else {
            NonNullList<ItemStack> l = NonNullList.create();
            l.addAll(applicableItems);
            return l;
        }
    }

    @Deprecated
    public Object getObjectForRecipe() {
        if (oreDictName != null) {
            return oreDictName;
        }
        if (fluidTypeAndAmount != null) {
            return UniversalBucket
                .getFilledBucket(ForgeModContainer.getInstance().universalBucket, fluidTypeAndAmount.getFluid());
        }
        return applicableItems;
    }

    @SideOnly(Side.CLIENT)
    public NonNullList<ItemStack> getApplicableItemsForRender() {
        NonNullList<ItemStack> applicable = getApplicableItems();
        Iterator<ItemStack> iterator = applicable.iterator();
        while (iterator.hasNext()) {
            ItemStack stack = iterator.next();
            if (stack.isEmpty()) continue;
            Item i = stack.getItem();
            if (!ignoreGatingRequirement && i instanceof ItemGatedVisibility) {
                if (!((ItemGatedVisibility) i).isSupposedToSeeInRender(stack)) {
                    iterator.remove();
                }
            }
        }
        return applicable;
    }

    // CACHE THIS !!!111ELEVEN11!
    public Ingredient getRecipeIngredient() {
        switch (handleType) {
            case OREDICT -> {
                return new OreIngredient(this.oreDictName);
            }
            case FLUID -> {
                return new FluidIngredient(new FluidStack(fluidTypeAndAmount.getFluid(), 1000));
            }
            default -> {
                List<Ingredient> ingredients = new ArrayList<>();
                for (ItemStack stack : this.applicableItems) {
                    if (!stack.isEmpty()) {
                        Ingredient i = new HandleIngredient(stack);
                        if (!i.equals(Ingredient.EMPTY)) {
                            ingredients.add(i);
                        }
                    }
                }
                try {
                    return COMPOUND_CTOR.newInstance(ingredients);
                } catch (Exception ignored) {
                }
            }
        }
        return Ingredient.EMPTY;
    }

    @Nullable
    public String getOreDictName() {
        return oreDictName;
    }

    @Nullable
    public FluidStack getFluidTypeAndAmount() {
        return fluidTypeAndAmount;
    }

    public boolean matchCrafting(ItemStack stack) {
        if (stack.isEmpty()) return false;

        switch (handleType) {
            case OREDICT -> {
                for (int id : OreDictionary.getOreIDs(stack)) {
                    String name = OreDictionary.getOreName(id);
                    if (name != null && name.equals(oreDictName)) {
                        return true;
                    }
                }
                return false;
            }
            case STACK -> {
                for (ItemStack applicable : applicableItems) {
                    if (ItemComparator.compare(
                        applicable,
                        stack,
                        ItemComparator.Clause.ITEM,
                        ItemComparator.Clause.META_WILDCARD,
                        ItemComparator.Clause.NBT_LEAST)) {
                        return true;
                    }
                }
                return false;
            }
            case FLUID -> {
                FluidStack contained = FluidUtil.getFluidContained(stack);
                if (contained == null || contained.getFluid() == null
                    || !contained.getFluid()
                    .equals(fluidTypeAndAmount.getFluid())) {
                    return false;
                }
                return ItemUtils.drainFluidFromItem(stack, fluidTypeAndAmount, false)
                    .isSuccess();
            }
            default -> {
            }
        }
        return false;
    }

    public void serialize(ByteBuf byteBuf) {
        byteBuf.writeInt(handleType.ordinal());
        switch (handleType) {
            case OREDICT -> ByteBufUtils.writeString(byteBuf, this.oreDictName);
            case STACK -> {
                byteBuf.writeInt(this.applicableItems.size());
                for (ItemStack applicableItem : this.applicableItems) {
                    ByteBufUtils.writeItemStack(byteBuf, applicableItem);
                }
            }
            case FLUID -> ByteBufUtils.writeFluidStack(byteBuf, this.fluidTypeAndAmount);
            default -> {
            }
        }
    }

    public static ItemHandle deserialize(ByteBuf byteBuf) {
        Type type = Type.values()[byteBuf.readInt()];
        ItemHandle handle = new ItemHandle(type);
        switch (type) {
            case OREDICT -> handle.oreDictName = ByteBufUtils.readString(byteBuf);
            case STACK -> {
                int amt = byteBuf.readInt();
                for (int i = 0; i < amt; i++) {
                    handle.applicableItems.add(ByteBufUtils.readItemStack(byteBuf));
                }
            }
            case FLUID -> handle.fluidTypeAndAmount = ByteBufUtils.readFluidStack(byteBuf);
            default -> {
            }
        }
        return handle;
    }

    static {
        Constructor<CompoundIngredient> ctor;
        try {
            ctor = CompoundIngredient.class.getDeclaredConstructor(Collection.class);
        } catch (Exception exc) {
            throw new IllegalStateException(
                "Could not find CompoundIngredient Constructor! Recipes can't be created; Exiting execution! Try with AS and forge alone first! Please report this along with exact forge version and other mods.");
        }
        ctor.setAccessible(true);
        COMPOUND_CTOR = ctor;
    }

    public static class HandleIngredient extends Ingredient {

        private HandleIngredient(ItemStack stack) {
            super(stack);
        }

        @Override
        public boolean apply(@Nullable ItemStack other) {
            if (other == null) {
                return false;
            }

            for (ItemStack thisStack : this.matchingStacks) {
                if (ItemComparator.compare(
                    thisStack,
                    other,
                    ItemComparator.Clause.ITEM,
                    ItemComparator.Clause.META_WILDCARD,
                    ItemComparator.Clause.NBT_LEAST)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static enum Type {

        OREDICT,
        STACK,
        FLUID

    }

}
