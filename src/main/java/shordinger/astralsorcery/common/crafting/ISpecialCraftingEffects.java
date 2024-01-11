/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
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
//Interface to tell the internal recipe recovery that this has some special clientside effects.
public interface ISpecialCraftingEffects {

    public AbstractAltarRecipe copyNewEffectInstance();

    default public boolean needsStrictMatching() {
        return false;
    }

}
