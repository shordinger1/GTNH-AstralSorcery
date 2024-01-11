/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.grindstone;

import javax.annotation.Nonnull;

import shordinger.astralsorcery.common.item.crystal.CrystalProperties;
import shordinger.astralsorcery.common.item.crystal.base.ItemRockCrystalBase;
import shordinger.wrapper.net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalSharpeningRecipe
 * Created by HellFirePvP
 * Date: 19.11.2017 / 10:50
 */
public class CrystalSharpeningRecipe extends GrindstoneRecipe {

    public CrystalSharpeningRecipe(int chance) {
        super(ItemStack.EMPTY, ItemStack.EMPTY, chance);
    }

    @Override
    public boolean matches(ItemStack stackIn) {
        return !stackIn.isEmpty() && (stackIn.getItem() instanceof ItemRockCrystalBase);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Nonnull
    @Override
    public GrindResult grind(ItemStack stackIn) {
        CrystalProperties prop = CrystalProperties.getCrystalProperties(stackIn);
        CrystalProperties result = prop.grindCopy(rand);
        if (result == null) {
            return GrindResult.failBreakItem();
        }
        CrystalProperties.applyCrystalProperties(stackIn, result);
        if (result.getSize() <= 0) {
            return GrindResult.failBreakItem();
        }
        return GrindResult.success();

    }
}
