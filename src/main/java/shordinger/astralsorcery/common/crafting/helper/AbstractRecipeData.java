/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.helper;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AbstractRecipeData
 * Created by HellFirePvP
 * Date: 10.08.2016 / 15:21
 */
public abstract class AbstractRecipeData {

    private final ItemStack output;

    public AbstractRecipeData(@Nonnull ItemStack output) {
        this.output = output;
    }

    @Nonnull
    public ItemStack getOutput() {
        return output;
    }

}
