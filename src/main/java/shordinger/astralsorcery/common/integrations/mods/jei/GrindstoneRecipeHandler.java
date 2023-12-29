/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import shordinger.astralsorcery.common.crafting.grindstone.GrindstoneRecipe;
import shordinger.astralsorcery.common.integrations.ModIntegrationJEI;
import shordinger.astralsorcery.common.integrations.mods.jei.base.JEIBaseHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GrindstoneRecipeHandler
 * Created by HellFirePvP
 * Date: 23.11.2017 / 20:00
 */
public class GrindstoneRecipeHandler extends JEIBaseHandler<GrindstoneRecipe> {

    @Override
    public Class<GrindstoneRecipe> getRecipeClass() {
        return GrindstoneRecipe.class;
    }

    @Override
    public String getRecipeCategoryUid(GrindstoneRecipe recipe) {
        return ModIntegrationJEI.idGrindstone;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(GrindstoneRecipe recipe) {
        return new GrindstoneRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(GrindstoneRecipe recipe) {
        return true;
    }

}
