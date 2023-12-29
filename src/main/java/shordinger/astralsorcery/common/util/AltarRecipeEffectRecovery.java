/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util;

import shordinger.astralsorcery.common.crafting.ISpecialCraftingEffects;
import shordinger.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import shordinger.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import shordinger.astralsorcery.common.tile.TileAltar;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarRecipeEffectRecovery
 * Created by HellFirePvP
 * Date: 29.10.2017 / 14:01
 */
public class AltarRecipeEffectRecovery {

    public static void attemptRecipeRecovery() {
        for (TileAltar.AltarLevel al : TileAltar.AltarLevel.values()) {
            for (AbstractAltarRecipe ar : AltarRecipeRegistry.getRecipesForLevel(al)) {
                if (!(ar instanceof ISpecialCraftingEffects)) {
                    ISpecialCraftingEffects eff = AltarRecipeRegistry.shouldHaveSpecialEffects(ar);
                    if (eff != null) {
                        ar.setSpecialEffectRecovery(eff.copyNewEffectInstance());
                    }
                }
            }
        }
    }

}
