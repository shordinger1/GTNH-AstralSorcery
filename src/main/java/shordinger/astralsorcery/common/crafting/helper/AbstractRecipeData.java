/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.helper;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

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
