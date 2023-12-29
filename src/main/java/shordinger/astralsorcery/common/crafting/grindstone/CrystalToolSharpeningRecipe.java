/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.grindstone;

import net.minecraft.item.ItemStack;
import shordinger.astralsorcery.common.item.crystal.ToolCrystalProperties;
import shordinger.astralsorcery.common.item.tool.ItemCrystalSword;
import shordinger.astralsorcery.common.item.tool.ItemCrystalToolBase;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalToolSharpeningRecipe
 * Created by HellFirePvP
 * Date: 19.11.2017 / 10:50
 */
public class CrystalToolSharpeningRecipe extends GrindstoneRecipe {

    public CrystalToolSharpeningRecipe(int chance) {
        super(null, null, chance);
    }

    @Override
    public boolean matches(ItemStack stackIn) {
        return !stackIn.isEmpty()
            && (stackIn.getItem() instanceof ItemCrystalToolBase || stackIn.getItem() instanceof ItemCrystalSword);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Nonnull
    @Override
    public GrindResult grind(ItemStack stackIn) {
        ToolCrystalProperties prop = ItemCrystalToolBase.getToolProperties(stackIn);
        ToolCrystalProperties result = prop.grindCopy(rand);
        if (result == null) {
            return GrindResult.failBreakItem();
        }
        ItemCrystalToolBase.setToolProperties(stackIn, result);
        if (result.getSize() <= 0) {
            return GrindResult.failBreakItem();
        }
        return GrindResult.success();
    }

}
