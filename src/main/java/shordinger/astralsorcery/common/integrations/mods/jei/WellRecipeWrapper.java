/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.jei;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.Lists;

import mezz.jei.api.ingredients.IIngredients;
import shordinger.astralsorcery.common.base.WellLiquefaction;
import shordinger.astralsorcery.common.integrations.mods.jei.base.JEIBaseWrapper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WellRecipeWrapper
 * Created by HellFirePvP
 * Date: 27.02.2017 / 23:29
 */
public class WellRecipeWrapper extends JEIBaseWrapper {

    private final WellLiquefaction.LiquefactionEntry recipe;

    public WellRecipeWrapper(WellLiquefaction.LiquefactionEntry recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, recipe.catalyst.copy());
        ingredients.setOutput(FluidStack.class, new FluidStack(recipe.producing, Fluid.BUCKET_VOLUME));
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
    }

    @Nullable
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Lists.newArrayList();
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }

}
