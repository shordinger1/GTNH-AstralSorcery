/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.helper;

import java.awt.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import shordinger.astralsorcery.common.base.LightOreTransmutations;
import shordinger.astralsorcery.common.base.LiquidInteraction;
import shordinger.astralsorcery.common.base.Mods;
import shordinger.astralsorcery.common.base.WellLiquefaction;
import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import shordinger.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import shordinger.astralsorcery.common.crafting.grindstone.GrindstoneRecipe;
import shordinger.astralsorcery.common.crafting.grindstone.GrindstoneRecipeRegistry;
import shordinger.astralsorcery.common.crafting.infusion.AbstractInfusionRecipe;
import shordinger.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry;
import shordinger.astralsorcery.common.integrations.ModIntegrationJEI;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.migration.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CraftingAccessManager
 * Created by HellFirePvP
 * Date: 15.02.2017 / 14:23
 */
public class CraftingAccessManager {

    private static final List<Object> lastReloadRemovedRecipes = new LinkedList<>();

    private static boolean completed = false;
    public static boolean ignoreJEI = true;

    public static boolean hasCompletedSetup() {
        return completed;
    }

    /*
     * Called whenever the underlying cached recipes change
     * -> at post startup
     * -> when MT changes happen (/mt reload and the like)
     */
    public static void compile() {
        AltarRecipeRegistry.compileRecipes();
        InfusionRecipeRegistry.compileRecipes();
        completed = true;
    }

    public static void clearModifications() {
        // Unregister changes from JEI
        removeAll(InfusionRecipeRegistry.mtRecipes);
        removeAll(LightOreTransmutations.mtTransmutations);
        removeAll(WellLiquefaction.mtLiquefactions.values());
        removeAll(GrindstoneRecipeRegistry.mtRecipes);
        for (TileAltar.AltarLevel al : TileAltar.AltarLevel.values()) {
            removeAll(AltarRecipeRegistry.mtRecipes.get(al));
        }

        // Clear dirty maps
        InfusionRecipeRegistry.mtRecipes.clear();
        InfusionRecipeRegistry.recipes.clear();
        LightOreTransmutations.mtTransmutations.clear();
        WellLiquefaction.mtLiquefactions.clear();
        LiquidInteraction.mtInteractions.clear();
        GrindstoneRecipeRegistry.mtRecipes.clear();
        AltarRecipeRegistry.mtRecipes.clear();
        AltarRecipeRegistry.recipes.clear();

        // Add removed recipes back to JEI
        for (Object removedPreviously : lastReloadRemovedRecipes) {
            addRecipe(removedPreviously);
        }
        lastReloadRemovedRecipes.clear();

        // Setup registry maps again
        for (TileAltar.AltarLevel al : TileAltar.AltarLevel.values()) {
            AltarRecipeRegistry.mtRecipes.put(al, new LinkedList<>());
            AltarRecipeRegistry.recipes.put(al, new LinkedList<>());
        }

        // Loading default configurations how it'd be without Minetweaker
        InfusionRecipeRegistry.loadFromFallback();
        AltarRecipeRegistry.loadFromFallback();
        LightOreTransmutations.loadFromFallback();
        WellLiquefaction.loadFromFallback();
        LiquidInteraction.loadFromFallback();
        GrindstoneRecipeRegistry.loadFromFallback();
    }

    public static void registerMTInfusion(AbstractInfusionRecipe recipe) {
        InfusionRecipeRegistry.mtRecipes.add(recipe);
        addRecipe(recipe);
    }

    public static void registerMTAltarRecipe(AbstractAltarRecipe recipe) {
        tryRemoveAltarRecipe(
            recipe.getNativeRecipe()
                .getRegistryName());

        TileAltar.AltarLevel al = recipe.getNeededLevel();
        AltarRecipeRegistry.mtRecipes.get(al)
            .add(recipe);
        addRecipe(recipe);
    }

    public static void tryRemoveInfusionByOutput(ItemStack output) {
        markForRemoval(InfusionRecipeRegistry.removeFindRecipeByOutput(output));
    }

    @Deprecated
    public static void tryRemoveAltarRecipeByOutputAndLevel(ItemStack output, TileAltar.AltarLevel altarLevel) {
        markForRemoval(AltarRecipeRegistry.removeFindRecipeByOutputAndLevel(output, altarLevel));
    }

    public static boolean tryRemoveAltarRecipe(ResourceLocation recipeRegistryName) {
        AbstractAltarRecipe recipe = AltarRecipeRegistry.getRecipeSlow(recipeRegistryName);
        recipe = AltarRecipeRegistry.removeRecipeFromCache(recipe);
        markForRemoval(recipe);
        return recipe != null;
    }

    public static void addMTTransmutation(ItemStack in, ItemStack out, double cost, @Nullable IWeakConstellation cst) {
        IBlockState stateIn = ItemUtils.createBlockState(in);
        IBlockState stateOut = ItemUtils.createBlockState(out);
        if (stateIn != null && stateOut != null) {
            LightOreTransmutations.Transmutation tr = new LightOreTransmutations.Transmutation(
                stateIn,
                stateOut,
                in,
                out,
                cost);
            if (cst != null) {
                tr.setRequiredType(cst);
            }
            tr = LightOreTransmutations.registerTransmutation(tr);
            if (tr != null) {
                // addRecipe(tr); Is picked up by default logic
                LightOreTransmutations.mtTransmutations.add(tr);
            }
        }
    }

    public static void removeMTTransmutation(ItemStack match, boolean matchMeta) {
        markForRemoval(LightOreTransmutations.tryRemoveTransmutation(match, matchMeta));
    }

    public static void addMTLiquefaction(ItemStack catalystIn, Fluid producedIn, float productionMultiplier,
                                         float shatterMultiplier, Color color) {
        if (WellLiquefaction.getLiquefactionEntry(catalystIn) != null) {
            return;
        }
        WellLiquefaction.LiquefactionEntry le = new WellLiquefaction.LiquefactionEntry(
            catalystIn,
            producedIn,
            productionMultiplier,
            shatterMultiplier,
            color);
        WellLiquefaction.mtLiquefactions.put(catalystIn, le);
        addRecipe(le);
    }

    public static void removeMTLiquefaction(ItemStack match, @Nullable Fluid fluid) {
        markForRemoval(WellLiquefaction.tryRemoveLiquefaction(match, fluid));
    }

    public static void addLiquidInteraction(int weight, FluidStack component1, FluidStack component2, float chance1,
                                            float chance2, ItemStack output) {
        LiquidInteraction interaction = new LiquidInteraction(
            weight,
            component1,
            component2,
            LiquidInteraction.createItemDropAction(chance1, chance2, output));
        LiquidInteraction.mtInteractions.add(interaction);
    }

    public static void removeLiquidInteraction(Fluid comp1, Fluid comp2, @Nullable ItemStack output) {
        LiquidInteraction.removeInteraction(comp1, comp2, output);
    }

    public static void addGrindstoneRecipe(ItemHandle in, ItemStack out, int chance, float doubleChance) {
        GrindstoneRecipe gr = new GrindstoneRecipe(in, out, chance, doubleChance);

        GrindstoneRecipeRegistry.mtRecipes.add(gr);
        addRecipe(gr);
    }

    public static void removeGrindstoneRecipe(ItemStack out) {
        markForRemoval(GrindstoneRecipeRegistry.tryRemoveGrindstoneRecipe(out));
    }

    /*
     ****************************************** JEI interact
     */
    private static void addRecipe(Object o) {
        if (!ignoreJEI && Mods.JEI.isPresent()) {
            ModIntegrationJEI.addRecipe(o);
        }
    }

    private static void removeAll(Collection objects) {
        if (!ignoreJEI && Mods.JEI.isPresent()) {
            for (Object o : objects) {
                ModIntegrationJEI.removeRecipe(o);
            }
        }
    }

    private static void markForRemoval(Object o) {
        if (!ignoreJEI && o != null) {
            lastReloadRemovedRecipes.add(o);
            if (Mods.JEI.isPresent()) {
                ModIntegrationJEI.removeRecipe(o);
            }
        }
    }
}
