/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import shordinger.astralsorcery.common.base.LightOreTransmutations;
import shordinger.astralsorcery.common.integrations.ModIntegrationJEI;
import shordinger.astralsorcery.common.integrations.mods.jei.base.JEIBaseHandler;
import shordinger.wrapper.net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TransmutationRecipeHandler
 * Created by HellFirePvP
 * Date: 15.02.2017 / 16:00
 */
public class TransmutationRecipeHandler extends JEIBaseHandler<LightOreTransmutations.Transmutation> {

    @Override
    public Class<LightOreTransmutations.Transmutation> getRecipeClass() {
        return LightOreTransmutations.Transmutation.class;
    }

    @Override
    public String getRecipeCategoryUid(LightOreTransmutations.Transmutation recipe) {
        return ModIntegrationJEI.idTransmutation;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(LightOreTransmutations.Transmutation recipe) {
        return new TransmutationRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(LightOreTransmutations.Transmutation recipe) {
        ItemStack inStack = recipe.getInputDisplayStack();
        ItemStack outStack = recipe.getOutputDisplayStack();
        return !inStack.isEmpty() && !outStack.isEmpty();
    }
}
