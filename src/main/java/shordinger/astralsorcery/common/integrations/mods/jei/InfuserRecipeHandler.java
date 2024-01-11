/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import shordinger.astralsorcery.common.crafting.infusion.AbstractInfusionRecipe;
import shordinger.astralsorcery.common.integrations.ModIntegrationJEI;
import shordinger.astralsorcery.common.integrations.mods.jei.base.JEIBaseHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: InfuserRecipeHandler
 * Created by HellFirePvP
 * Date: 11.01.2017 / 00:58
 */
public class InfuserRecipeHandler extends JEIBaseHandler<AbstractInfusionRecipe> {

    @Override
    public Class<AbstractInfusionRecipe> getRecipeClass() {
        return AbstractInfusionRecipe.class;
    }

    @Override
    public String getRecipeCategoryUid(AbstractInfusionRecipe recipe) {
        return ModIntegrationJEI.idInfuser;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(AbstractInfusionRecipe recipe) {
        return new InfuserRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(AbstractInfusionRecipe recipe) {
        // return ModIntegrationJEI.jeiRegistrationPhase ||
        // ResearchManager.clientProgress.getTierReached().isThisLaterOrEqual(ProgressionTier.ATTUNEMENT);
        return true;
    }

}
