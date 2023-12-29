/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.jei.altar;

import mezz.jei.api.recipe.IRecipeWrapper;
import shordinger.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import shordinger.astralsorcery.common.data.research.ResearchProgression;
import shordinger.astralsorcery.common.integrations.ModIntegrationJEI;
import shordinger.astralsorcery.common.integrations.mods.jei.base.JEIBaseHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarTraitRecipeHandler
 * Created by HellFirePvP
 * Date: 19.10.2017 / 22:59
 */
public class AltarTraitRecipeHandler extends JEIBaseHandler<TraitRecipe> {

    @Override
    public Class<TraitRecipe> getRecipeClass() {
        return TraitRecipe.class;
    }

    @Override
    public String getRecipeCategoryUid(TraitRecipe recipe) {
        return ModIntegrationJEI.idAltarTrait;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(TraitRecipe recipe) {
        return new AltarTraitRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(TraitRecipe recipe) {
        return recipe.getRequiredProgression() == ResearchProgression.RADIANCE;
    }

}
