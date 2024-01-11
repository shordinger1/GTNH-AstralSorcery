/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.grindstone;

import shordinger.astralsorcery.common.auxiliary.SwordSharpenHelper;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.wrapper.net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SwordSharpeningRecipe
 * Created by HellFirePvP
 * Date: 10.12.2017 / 15:05
 */
public class SwordSharpeningRecipe extends GrindstoneRecipe {

    public SwordSharpeningRecipe() {
        super(ItemStack.EMPTY, ItemStack.EMPTY, 40);
    }

    @Override
    public boolean matches(ItemStack stackIn) {
        return !stackIn.isEmpty() && SwordSharpenHelper.canBeSharpened(stackIn) && !SwordSharpenHelper.isSwordSharpened(stackIn);
    }

    @Nonnull
    @Override
    public GrindResult grind(ItemStack stackIn) {
        if(SwordSharpenHelper.canBeSharpened(stackIn) && rand.nextInt(chance) == 0) {
            ItemStack copy = ItemUtils.copyStackWithSize(stackIn, stackIn.getCount());
            SwordSharpenHelper.setSwordSharpened(copy);
            return GrindResult.itemChange(copy);
        }
        return GrindResult.failNoOp();
    }
}
