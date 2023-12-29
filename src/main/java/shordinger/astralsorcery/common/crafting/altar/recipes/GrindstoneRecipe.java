/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.altar.recipes;

import shordinger.astralsorcery.common.block.BlockMachine;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import shordinger.astralsorcery.common.util.OreDictAlias;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GrindstoneRecipe
 * Created by HellFirePvP
 * Date: 23.10.2016 / 17:00
 */
public class GrindstoneRecipe extends DiscoveryRecipe {

    public GrindstoneRecipe() {
        super(
            shapedRecipe("grindstone", BlockMachine.MachineType.GRINDSTONE.asStack())
                .addPart(OreDictAlias.BLOCK_MARBLE, ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.BLOCK_WOOD_PLANKS, ShapedRecipeSlot.RIGHT, ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(OreDictAlias.ITEM_STICKS, ShapedRecipeSlot.LOWER_CENTER, ShapedRecipeSlot.LOWER_LEFT)
                .unregisteredAccessibleShapedRecipe());
    }

    @Override
    public boolean allowsForChaining() {
        return false;
    }
}
