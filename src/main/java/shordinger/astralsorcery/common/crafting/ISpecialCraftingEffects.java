/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting;

import shordinger.astralsorcery.common.crafting.altar.AbstractAltarRecipe;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ISpecialCraftingEffects
 * Created by HellFirePvP
 * Date: 30.10.2017 / 11:06
 */
// Interface to tell the internal recipe recovery that this has some special clientside effects.
public interface ISpecialCraftingEffects {

    public AbstractAltarRecipe copyNewEffectInstance();

    default public boolean needsStrictMatching() {
        return false;
    }

}
