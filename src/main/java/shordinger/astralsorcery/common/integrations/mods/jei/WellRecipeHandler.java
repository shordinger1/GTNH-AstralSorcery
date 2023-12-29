/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import shordinger.astralsorcery.common.base.WellLiquefaction;
import shordinger.astralsorcery.common.integrations.ModIntegrationJEI;
import shordinger.astralsorcery.common.integrations.mods.jei.base.JEIBaseHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WellRecipeHandler
 * Created by HellFirePvP
 * Date: 27.02.2017 / 23:33
 */
public class WellRecipeHandler extends JEIBaseHandler<WellLiquefaction.LiquefactionEntry> {

    @Override
    public Class<WellLiquefaction.LiquefactionEntry> getRecipeClass() {
        return WellLiquefaction.LiquefactionEntry.class;
    }

    @Override
    public String getRecipeCategoryUid(WellLiquefaction.LiquefactionEntry recipe) {
        return ModIntegrationJEI.idWell;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(WellLiquefaction.LiquefactionEntry recipe) {
        return new WellRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(WellLiquefaction.LiquefactionEntry recipe) {
        return true;
    }

}
