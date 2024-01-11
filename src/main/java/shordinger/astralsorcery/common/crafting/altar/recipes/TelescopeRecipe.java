/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.altar.recipes;

import shordinger.astralsorcery.common.block.BlockMachine;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import shordinger.astralsorcery.common.lib.ItemsAS;
import shordinger.astralsorcery.common.util.OreDictAlias;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TelescopeRecipe
 * Created by HellFirePvP
 * Date: 26.09.2016 / 13:54
 */
public class TelescopeRecipe extends AttunementRecipe {

    public TelescopeRecipe() {
        super(
            shapedRecipe("telescope", BlockMachine.MachineType.TELESCOPE.asStack())
                .addPart(ItemsAS.handTelescope, ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.BLOCK_WOOD_PLANKS, ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.ITEM_GOLD_INGOT, ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT)
                .addPart(
                    OreDictAlias.ITEM_STICKS,
                    ShapedRecipeSlot.LOWER_LEFT,
                    ShapedRecipeSlot.LOWER_CENTER,
                    ShapedRecipeSlot.LOWER_RIGHT)
                .unregisteredAccessibleShapedRecipe());
    }

    @Override
    public boolean allowsForChaining() {
        return false;
    }

}
