/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.crafttweaker.tweaks;

import net.minecraft.item.ItemStack;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.oredict.IOreDictEntry;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.integrations.ModIntegrationCrafttweaker;
import shordinger.astralsorcery.common.integrations.mods.crafttweaker.BaseTweaker;
import shordinger.astralsorcery.common.integrations.mods.crafttweaker.network.GrindstoneRecipeAdd;
import shordinger.astralsorcery.common.integrations.mods.crafttweaker.network.GrindstoneRecipeRemove;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GrindstoneRecipe
 * Created by HellFirePvP
 * Date: 30.11.2017 / 16:55
 */
@ZenClass("mods.astralsorcery.Grindstone")
public class GrindstoneRecipe extends BaseTweaker {

    protected static final String name = "AstralSorcery Grindstone";

    @ZenMethod
    public static void addRecipe(IOreDictEntry oreDict, IItemStack output) {
        addRecipe(oreDict, output, 0F);
    }

    @ZenMethod
    public static void addRecipe(IOreDictEntry oreDict, IItemStack output, float doubleChance) {
        addRecipeInternal(oreDict, output, doubleChance);
    }

    @ZenMethod
    public static void addRecipe(IItemStack input, IItemStack output) {
        addRecipe(input, output, 0F);
    }

    @ZenMethod
    public static void addRecipe(IItemStack input, IItemStack output, float doubleChance) {
        addRecipeInternal(input, output, doubleChance);
    }

    private static void addRecipeInternal(IIngredient obj, IItemStack output, float doubleChance) {
        ItemHandle in = convertToHandle(obj);
        if (in == null || in.handleType == ItemHandle.Type.FLUID) { // No fluid inputs :thonk:
            CraftTweakerAPI.logError("[" + name + "] Skipping recipe-add due to invalid input itemstack.");
            return;
        }

        ItemStack out = convertToItemStack(output);
        if (out.isEmpty()) {
            CraftTweakerAPI.logError("[" + name + "] Skipping recipe-add due to invalid output itemstack.");
            return;
        }

        ModIntegrationCrafttweaker.recipeModifications.add(new GrindstoneRecipeAdd(in, out, doubleChance));
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output) {
        ItemStack out = convertToItemStack(output);
        if (out.isEmpty()) {
            CraftTweakerAPI.logError("[" + name + "] Skipping recipe-add due to invalid output itemstack.");
            return;
        }

        ModIntegrationCrafttweaker.recipeModifications.add(new GrindstoneRecipeRemove(out));
    }

    @ZenMethod
    @Deprecated
    public static void removeReipce(IItemStack output) {
        removeRecipe(output);
    }

}
