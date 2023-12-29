/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.altar.recipes;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import shordinger.astralsorcery.common.block.BlockInfusedWood;
import shordinger.astralsorcery.common.block.BlockMarble;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.crafting.helper.ShapeMap;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import shordinger.astralsorcery.common.item.ItemCraftingComponent;
import shordinger.astralsorcery.common.item.crystal.CrystalProperties;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.OreDictAlias;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LensRecipe
 * Created by HellFirePvP
 * Date: 02.11.2016 / 20:27
 */
public class LensRecipe extends AttunementRecipe {

    public LensRecipe() {
        super(
            shapedRecipe("crystallens", BlocksAS.lens)
                .addPart(
                    ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                    ShapedRecipeSlot.UPPER_CENTER,
                    ShapedRecipeSlot.LEFT,
                    ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.ITEM_AQUAMARINE, ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(ItemHandle.getCrystalVariant(false, false), ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.ITEM_GOLD_INGOT, ShapedRecipeSlot.LOWER_CENTER)
                .addPart(
                    BlockInfusedWood.WoodType.ENGRAVED.asStack(),
                    ShapedRecipeSlot.LOWER_LEFT,
                    ShapedRecipeSlot.LOWER_RIGHT)
                .unregisteredAccessibleShapedRecipe());

        setAttItem(
            BlockMarble.MarbleBlockType.RUNED.asStack(),
            AttunementAltarSlot.LOWER_LEFT,
            AttunementAltarSlot.LOWER_RIGHT);
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar altar) {
        ItemStack lens = super.getOutput(centralGridMap, altar);
        lens = ItemUtils.copyStackWithSize(lens, 1);
        CrystalProperties crystalProp = CrystalProperties.getCrystalProperties(
            centralGridMap.get(ShapedRecipeSlot.CENTER)
                .getApplicableItems()
                .get(0));
        if (crystalProp != null) {
            CrystalProperties.applyCrystalProperties(lens, crystalProp);
        }
        lens.setCount(Math.max(1, crystalProp.getSize() / 80));
        return lens;
    }

}
