/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.grindstone;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import shordinger.astralsorcery.common.item.crystal.CrystalProperties;
import shordinger.astralsorcery.common.item.crystal.base.ItemRockCrystalBase;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalSharpeningRecipe
 * Created by HellFirePvP
 * Date: 19.11.2017 / 10:50
 */
public class CrystalSharpeningRecipe extends GrindstoneRecipe {

    public CrystalSharpeningRecipe(int chance) {
        super(null, null, chance);
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
