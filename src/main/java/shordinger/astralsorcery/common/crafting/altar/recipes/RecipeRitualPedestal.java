/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.altar.recipes;

import shordinger.astralsorcery.common.block.BlockMarble;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.util.OreDictAlias;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RecipeRitualPedestal
 * Created by HellFirePvP
 * Date: 01.11.2016 / 14:48
 */
public class RecipeRitualPedestal extends AttunementRecipe {

    public RecipeRitualPedestal() {
        super(
            shapedRecipe("ritualpedestal", BlocksAS.ritualPedestal)
                .addPart(
                    BlockMarble.MarbleBlockType.RUNED.asStack(),
                    ShapedRecipeSlot.LOWER_LEFT,
                    ShapedRecipeSlot.LOWER_CENTER,
                    ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(BlockMarble.MarbleBlockType.PILLAR.asStack(), ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT)
                .addPart(
                    BlockMarble.MarbleBlockType.CHISELED.asStack(),
                    ShapedRecipeSlot.UPPER_LEFT,
                    ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(ItemHandle.getCrystalVariant(false, false), ShapedRecipeSlot.UPPER_CENTER)
                .addPart(BlocksAS.fluidLiquidStarlight, ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe());
        setAttItem(OreDictAlias.ITEM_GOLD_INGOT, AttunementAltarSlot.UPPER_LEFT, AttunementAltarSlot.UPPER_RIGHT);
        setAttItem(
            BlockMarble.MarbleBlockType.PILLAR.asStack(),
            AttunementAltarSlot.LOWER_LEFT,
            AttunementAltarSlot.LOWER_RIGHT);
    }
}
