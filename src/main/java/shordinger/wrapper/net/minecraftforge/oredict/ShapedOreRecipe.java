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

package shordinger.wrapper.net.minecraftforge.oredict;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.inventory.InventoryCrafting;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.item.crafting.IRecipe;
import shordinger.wrapper.net.minecraft.item.crafting.Ingredient;
import shordinger.wrapper.net.minecraft.util.JsonUtils;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.common.crafting.CraftingHelper;
import shordinger.wrapper.net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import shordinger.wrapper.net.minecraftforge.common.crafting.IShapedRecipe;
import shordinger.wrapper.net.minecraftforge.common.crafting.JsonContext;
import shordinger.wrapper.net.minecraftforge.registries.IForgeRegistryEntry;

public class ShapedOreRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IShapedRecipe {

    @Deprecated
    public static final int MAX_CRAFT_GRID_WIDTH = 3;
    @Deprecated
    public static final int MAX_CRAFT_GRID_HEIGHT = 3;

    @Nonnull
    protected ItemStack output = ItemStack.EMPTY;
    protected NonNullList<Ingredient> input = null;
    protected int width = 0;
    protected int height = 0;
    protected boolean mirrored = true;
    protected ResourceLocation group;

    public ShapedOreRecipe(ResourceLocation group, Block result, Object... recipe) {
        this(group, new ItemStack(result), recipe);
    }

    public ShapedOreRecipe(ResourceLocation group, Item result, Object... recipe) {
        this(group, new ItemStack(result), recipe);
    }

    public ShapedOreRecipe(ResourceLocation group, @Nonnull ItemStack result, Object... recipe) {
        this(group, result, CraftingHelper.parseShaped(recipe));
    }

    public ShapedOreRecipe(ResourceLocation group, @Nonnull ItemStack result, ShapedPrimer primer) {
        this.group = group;
        output = result.copy();
        this.width = primer.width;
        this.height = primer.height;
        this.input = primer.input;
        this.mirrored = primer.mirrored;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    @Nonnull
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1) {
        return output.copy();
    }

    /**
     * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
     * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
     */
    @Override
    @Nonnull
    public ItemStack getRecipeOutput() {
        return output;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world) {
        for (int x = 0; x <= inv.getWidth() - width; x++) {
            for (int y = 0; y <= inv.getHeight() - height; ++y) {
                if (checkMatch(inv, x, y, false)) {
                    return true;
                }

                if (mirrored && checkMatch(inv, x, y, true)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Based on {@link net.minecraft.item.crafting.ShapedRecipes#checkMatch(InventoryCrafting, int, int, boolean)}
     */
    protected boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror) {
        for (int x = 0; x < inv.getWidth(); x++) {
            for (int y = 0; y < inv.getHeight(); y++) {
                int subX = x - startX;
                int subY = y - startY;
                Ingredient target = Ingredient.EMPTY;

                if (subX >= 0 && subY >= 0 && subX < width && subY < height) {
                    if (mirror) {
                        target = input.get(width - subX - 1 + subY * width);
                    } else {
                        target = input.get(subX + subY * width);
                    }
                }

                if (!target.apply(inv.getStackInRowAndColumn(x, y))) {
                    return false;
                }
            }
        }

        return true;
    }

    public ShapedOreRecipe setMirrored(boolean mirror) {
        mirrored = mirror;
        return this;
    }

    @Override
    @Nonnull
    public NonNullList<Ingredient> getIngredients() {
        return this.input;
    }

    @Deprecated // Use IShapedRecipe.getRecipeWidth
    public int getWidth() {
        return width;
    }

    @Override
    public int getRecipeWidth() {
        return this.getWidth();
    }

    @Deprecated // Use IShapedRecipe.getRecipeHeight
    public int getHeight() {
        return height;
    }

    @Override
    public int getRecipeHeight() {
        return this.getHeight();
    }

    /**
     * Recipes with equal group are combined into one button in the recipe book
     */
    @Override
    @Nonnull
    public String getGroup() {
        return this.group == null ? "" : this.group.toString();
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canFit(int width, int height) {
        return width >= this.width && height >= this.height;
    }

    public static ShapedOreRecipe factory(JsonContext context, JsonObject json) {
        String group = JsonUtils.getString(json, "group", "");
        // if (!group.isEmpty() && group.indexOf(':') == -1)
        // group = context.getModId() + ":" + group;

        Map<Character, Ingredient> ingMap = Maps.newHashMap();
        for (Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "key")
            .entrySet()) {
            if (entry.getKey()
                .length() != 1)
                throw new JsonSyntaxException(
                    "Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            if (" ".equals(entry.getKey()))
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");

            ingMap.put(
                entry.getKey()
                    .toCharArray()[0],
                CraftingHelper.getIngredient(entry.getValue(), context));
        }

        ingMap.put(' ', Ingredient.EMPTY);

        JsonArray patternJ = JsonUtils.getJsonArray(json, "pattern");

        if (patternJ.size() == 0) throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");

        String[] pattern = new String[patternJ.size()];
        for (int x = 0; x < pattern.length; ++x) {
            String line = JsonUtils.getString(patternJ.get(x), "pattern[" + x + "]");
            if (x > 0 && pattern[0].length() != line.length())
                throw new JsonSyntaxException("Invalid pattern: each row must  be the same width");
            pattern[x] = line;
        }

        ShapedPrimer primer = new ShapedPrimer();
        primer.width = pattern[0].length();
        primer.height = pattern.length;
        primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
        primer.input = NonNullList.withSize(primer.width * primer.height, Ingredient.EMPTY);

        Set<Character> keys = Sets.newHashSet(ingMap.keySet());
        keys.remove(' ');

        int x = 0;
        for (String line : pattern) {
            for (char chr : line.toCharArray()) {
                Ingredient ing = ingMap.get(chr);
                if (ing == null) throw new JsonSyntaxException(
                    "Pattern references symbol '" + chr + "' but it's not defined in the key");
                primer.input.set(x++, ing);
                keys.remove(chr);
            }
        }

        if (!keys.isEmpty()) throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + keys);

        ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
        return new ShapedOreRecipe(group.isEmpty() ? null : new ResourceLocation(group), result, primer);
    }
}
