/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.jei.altar;

import mezz.jei.api.recipe.IRecipeWrapper;
import shordinger.astralsorcery.common.crafting.altar.recipes.DiscoveryRecipe;
import shordinger.astralsorcery.common.data.research.ResearchProgression;
import shordinger.astralsorcery.common.integrations.ModIntegrationJEI;
import shordinger.astralsorcery.common.integrations.mods.jei.base.JEIBaseHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarDiscoveryRecipeHandler
 * Created by HellFirePvP
 * Date: 15.02.2017 / 16:54
 */
public class AltarDiscoveryRecipeHandler extends JEIBaseHandler<DiscoveryRecipe> {

    @Override
    public Class<DiscoveryRecipe> getRecipeClass() {
        return DiscoveryRecipe.class;
    }

    @Override
    public String getRecipeCategoryUid(DiscoveryRecipe recipe) {
        return ModIntegrationJEI.idAltarDiscovery;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(DiscoveryRecipe recipe) {
        return new AltarDiscoveryRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(DiscoveryRecipe recipe) {
        return recipe.getRequiredProgression() == ResearchProgression.BASIC_CRAFT; // Find a better way of filtering..
    }

}
