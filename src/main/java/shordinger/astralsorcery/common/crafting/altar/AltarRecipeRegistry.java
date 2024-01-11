/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.altar;

import com.google.common.collect.Lists;
import shordinger.astralsorcery.common.crafting.ISpecialCraftingEffects;
import shordinger.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.DiscoveryRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import shordinger.astralsorcery.common.crafting.helper.AccessibleRecipeAdapater;
import shordinger.astralsorcery.common.crafting.helper.CraftingAccessManager;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.util.ItemComparator;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarRecipeRegistry
 * Created by HellFirePvP
 * Date: 22.09.2016 / 13:13
 */
public class AltarRecipeRegistry {

    private static Map<ItemStack, ISpecialCraftingEffects> effectRecoveryMap = new HashMap<>();

    public static Map<TileAltar.AltarLevel, List<AbstractAltarRecipe>> mtRecipes = new HashMap<>();
    public static Map<TileAltar.AltarLevel, List<AbstractAltarRecipe>> recipes = new HashMap<>();
    private static AbstractAltarRecipe[] compiledRecipeArray = null;

    private static Map<TileAltar.AltarLevel, List<AbstractAltarRecipe>> localFallbackCache = new HashMap<>();

    // NEVER call this. this should only get called once at post init to compile all recipes for fast access.
    // After this is called, changes to recipe registry might break stuff.
    public static void compileRecipes() {
        compiledRecipeArray = null;

        int totalNeeded = 0;
        for (TileAltar.AltarLevel level : recipes.keySet()) {
            totalNeeded += recipes.get(level)
                .size();
        }
        for (TileAltar.AltarLevel level : mtRecipes.keySet()) {
            totalNeeded += mtRecipes.get(level)
                .size();
        }

        int i = 0;
        compiledRecipeArray = new AbstractAltarRecipe[totalNeeded];
        for (TileAltar.AltarLevel l : TileAltar.AltarLevel.values()) {
            List<AbstractAltarRecipe> recipeList = recipes.get(l);
            for (AbstractAltarRecipe rec : recipeList) {
                compiledRecipeArray[i] = rec;
                rec.updateUniqueId(i);
                i++;
            }
            recipeList = mtRecipes.get(l);
            for (AbstractAltarRecipe rec : recipeList) {
                compiledRecipeArray[i] = rec;
                rec.updateUniqueId(i);
                i++;
            }
        }
    }

    public static void cacheLocalRecipes() {
        if (localFallbackCache.isEmpty()) {
            for (TileAltar.AltarLevel al : TileAltar.AltarLevel.values()) {
                localFallbackCache.put(al, new LinkedList<>());
                localFallbackCache.get(al)
                    .addAll(recipes.get(al));
            }
        }
    }

    public static void loadFromFallback() {
        if (!localFallbackCache.isEmpty()) {
            for (TileAltar.AltarLevel al : TileAltar.AltarLevel.values()) {
                recipes.get(al)
                    .addAll(localFallbackCache.get(al));
            }
        }
    }

    @Nullable
    public static AbstractAltarRecipe getRecipe(int id) {
        if (id < 0 || id >= compiledRecipeArray.length) return null;
        return compiledRecipeArray[id];
    }

    @Nullable
    public static AbstractAltarRecipe getRecipeSlow(@Nullable ResourceLocation id) {
        if (id == null) {
            return null;
        }
        for (Collection<AbstractAltarRecipe> recipeList : recipes.values()) {
            for (AbstractAltarRecipe recipe : recipeList) {
                if (recipe.getNativeRecipe()
                    .getRegistryName()
                    .equals(id)) {
                    return recipe;
                }
            }
        }
        for (Collection<AbstractAltarRecipe> recipeList : mtRecipes.values()) {
            for (AbstractAltarRecipe recipe : recipeList) {
                if (recipe.getNativeRecipe()
                    .getRegistryName()
                    .equals(id)) {
                    return recipe;
                }
            }
        }
        return null;
    }

    public static List<AbstractAltarRecipe> getAltarRecipesByOutput(ItemStack output, TileAltar.AltarLevel altarLevel) {
        List<AbstractAltarRecipe> list = new LinkedList<>();
        for (AbstractAltarRecipe recipe : recipes.get(altarLevel)) {
            ItemStack out = recipe.getOutputForMatching();
            if (!out.isEmpty() && ItemComparator.compare(out, output, ItemComparator.Clause.Sets.ITEMSTACK_STRICT)) {
                list.add(recipe);
            }
        }
        for (AbstractAltarRecipe recipe : mtRecipes.get(altarLevel)) {
            ItemStack out = recipe.getOutputForMatching();
            if (!out.isEmpty() && ItemComparator.compare(out, output, ItemComparator.Clause.Sets.ITEMSTACK_STRICT)) {
                list.add(recipe);
            }
        }
        return list;
    }

    /*
     * Returns the Recipe that was removed if successful.
     */
    @Nullable
    @Deprecated
    public static AbstractAltarRecipe removeFindRecipeByOutputAndLevel(ItemStack output,
                                                                       TileAltar.AltarLevel altarLevel) {
        Iterator<AbstractAltarRecipe> iterator = recipes.get(altarLevel)
            .iterator();
        while (iterator.hasNext()) {
            AbstractAltarRecipe rec = iterator.next();
            ItemStack out = rec.getOutputForMatching();
            if (!out.isEmpty() && ItemComparator.compare(
                rec.getOutputForMatching(),
                output,
                ItemComparator.Clause.ITEM,
                ItemComparator.Clause.META_STRICT)) {
                iterator.remove();
                return rec;
            }
        }
        return null;
    }

    /*
     * Returns the Recipe that was removed if successful.
     */
    @Nullable
    public static AbstractAltarRecipe removeRecipeFromCache(@Nullable AbstractAltarRecipe recipe) {
        if (recipe == null) {
            return null;
        }
        for (TileAltar.AltarLevel al : recipes.keySet()) {
            Iterator<AbstractAltarRecipe> iterator = recipes.get(al)
                .iterator();
            while (iterator.hasNext()) {
                AbstractAltarRecipe regRecipe = iterator.next();
                if (regRecipe.getNativeRecipe()
                    .getRegistryName()
                    .equals(
                        recipe.getNativeRecipe()
                            .getRegistryName())) {
                    iterator.remove();
                    return regRecipe;
                }
            }
        }
        return null;
    }

    public static TraitRecipe registerTraitRecipe(AccessibleRecipeAdapater recipe) {
        TraitRecipe tr = new TraitRecipe(recipe);
        registerAltarRecipe(tr);
        return tr;
    }

    public static ConstellationRecipe registerConstellationRecipe(AccessibleRecipeAdapater recipe) {
        ConstellationRecipe dr = new ConstellationRecipe(recipe);
        registerAltarRecipe(dr);
        return dr;
    }

    public static AttunementRecipe registerAttenuationRecipe(AccessibleRecipeAdapater recipe) {
        AttunementRecipe dr = new AttunementRecipe(recipe);
        registerAltarRecipe(dr);
        return dr;
    }

    public static DiscoveryRecipe registerDiscoveryRecipe(AccessibleRecipeAdapater recipe) {
        DiscoveryRecipe dr = new DiscoveryRecipe(recipe);
        registerAltarRecipe(dr);
        return dr;
    }

    public static <T extends AbstractAltarRecipe> T registerAltarRecipe(T recipe) {
        TileAltar.AltarLevel level = recipe.getNeededLevel();
        recipes.get(level)
            .add(recipe);
        if (recipe instanceof ISpecialCraftingEffects) {
            registerSpecialEffects(recipe);
        }
        if (CraftingAccessManager.hasCompletedSetup()) {
            CraftingAccessManager.compile();
        }
        return recipe;
    }

    private static void registerSpecialEffects(AbstractAltarRecipe ar) {
        ItemStack out = ar.getOutputForMatching();
        if (out.isEmpty()) return; // Well....

        boolean has = false;
        for (ItemStack i : effectRecoveryMap.keySet()) {
            if (ItemComparator.compare(out, i, ItemComparator.Clause.ITEM, ItemComparator.Clause.META_STRICT)) {
                has = true;
            }
        }
        if (!has) {
            effectRecoveryMap.put(out, (ISpecialCraftingEffects) ar);
        }
    }

    // null === false
    @Nullable
    public static ISpecialCraftingEffects shouldHaveSpecialEffects(AbstractAltarRecipe ar) {
        if (ar == null || ar instanceof ISpecialCraftingEffects) return null;
        ItemStack match = ar.getOutputForMatching();
        if (match.isEmpty()) return null;
        for (Map.Entry<ItemStack, ISpecialCraftingEffects> effectEntry : effectRecoveryMap.entrySet()) {
            if (effectEntry.getValue()
                .needsStrictMatching()
                ? ItemComparator.compare(match, effectEntry.getKey(), ItemComparator.Clause.Sets.ITEMSTACK_STRICT)
                : ItemComparator.compare(
                match,
                effectEntry.getKey(),
                ItemComparator.Clause.ITEM,
                ItemComparator.Clause.META_STRICT)) {
                return effectEntry.getValue();
            }
        }
        return null;
    }

    public static Collection<AbstractAltarRecipe> getRecipesForLevel(TileAltar.AltarLevel al) {
        List<AbstractAltarRecipe> cache = Lists.newLinkedList();
        cache.addAll(recipes.get(al));
        cache.addAll(mtRecipes.get(al));
        return cache;
    }

    @Nullable
    public static AbstractAltarRecipe findMatchingRecipe(TileAltar ta, boolean ignoreStarlightRequirement) {
        TileAltar.AltarLevel lowestAllowed = ta.matchDownMultiblocks(TileAltar.AltarLevel.DISCOVERY);
        if (lowestAllowed == null) {
            lowestAllowed = TileAltar.AltarLevel.DISCOVERY;
        }
        for (int i = lowestAllowed.ordinal(); i >= 0; i--) {
            TileAltar.AltarLevel lvl = TileAltar.AltarLevel.values()[i];
            List<AbstractAltarRecipe> validRecipes = recipes.get(lvl);
            if (validRecipes != null) {
                for (AbstractAltarRecipe rec : validRecipes) {
                    if (ta.doesRecipeMatch(rec, ignoreStarlightRequirement)) {
                        return rec;
                    }
                }
            }
            validRecipes = mtRecipes.get(lvl);
            if (validRecipes != null) {
                for (AbstractAltarRecipe rec : validRecipes) {
                    if (ta.doesRecipeMatch(rec, ignoreStarlightRequirement)) {
                        return rec;
                    }
                }
            }
        }
        return null;
        /*
         * List<TileAltar.AltarLevel> levels = new ArrayList<>();
         * List<AbstractAltarRecipe> validRecipes = new LinkedList<>();
         * for (int i = 0; i < level.ordinal() + 1; i++) {
         * levels.add(TileAltar.AltarLevel.values()[i]);
         * }
         * for (TileAltar.AltarLevel valid : levels) {
         * validRecipes.addAll(recipes.get(valid));
         * }
         * for (AbstractAltarRecipe recipe : validRecipes) {
         * if(recipe.matches(ta)) {
         * return recipe;
         * }
         * }
         */
    }

    static {
        for (TileAltar.AltarLevel al : TileAltar.AltarLevel.values()) {
            recipes.put(al, new LinkedList<>());
            mtRecipes.put(al, new LinkedList<>());
        }
    }

}
