/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.jei.altar;

import mezz.jei.api.recipe.IRecipeWrapper;
import shordinger.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import shordinger.astralsorcery.common.data.research.ResearchProgression;
import shordinger.astralsorcery.common.integrations.ModIntegrationJEI;
import shordinger.astralsorcery.common.integrations.mods.jei.base.JEIBaseHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarAttunementRecipeHandler
 * Created by HellFirePvP
 * Date: 15.02.2017 / 18:11
 */
public class AltarAttunementRecipeHandler extends JEIBaseHandler<AttunementRecipe> {

    @Override
    public Class<AttunementRecipe> getRecipeClass() {
        return AttunementRecipe.class;
    }

    @Override
    public String getRecipeCategoryUid(AttunementRecipe recipe) {
        return ModIntegrationJEI.idAltarAttunement;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(AttunementRecipe recipe) {
        return new AltarAttunementRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(AttunementRecipe recipe) {
        return recipe.getRequiredProgression() == ResearchProgression.ATTUNEMENT;
    }

}
