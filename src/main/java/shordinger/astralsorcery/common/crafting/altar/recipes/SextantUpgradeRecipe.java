/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.altar.recipes;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import shordinger.astralsorcery.common.block.BlockInfusedWood;
import shordinger.astralsorcery.common.crafting.helper.ShapeMap;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipe;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import shordinger.astralsorcery.common.item.ItemColoredLens;
import shordinger.astralsorcery.common.item.ItemCraftingComponent;
import shordinger.astralsorcery.common.item.tool.sextant.ItemSextant;
import shordinger.astralsorcery.common.lib.ItemsAS;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.OreDictAlias;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SextantUpgradeRecipe
 * Created by HellFirePvP
 * Date: 20.04.2018 / 13:13
 */
public class SextantUpgradeRecipe extends ConstellationRecipe {

    public SextantUpgradeRecipe() {
        super(
            ShapedRecipe.Builder.newShapedRecipe("internal/altar/sextant/upgrade", ItemsAS.sextant)
                .addPart(ItemsAS.sextant, ShapedRecipeSlot.LOWER_CENTER)
                .addPart(
                    ItemColoredLens.ColorType.SPECTRAL.asStack(),
                    ShapedRecipeSlot.CENTER,
                    ShapedRecipeSlot.UPPER_CENTER)
                .addPart(
                    OreDictAlias.ITEM_STARMETAL_INGOT,
                    ShapedRecipeSlot.LEFT,
                    ShapedRecipeSlot.UPPER_LEFT,
                    ShapedRecipeSlot.RIGHT,
                    ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(
                    ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
                    ShapedRecipeSlot.LOWER_LEFT,
                    ShapedRecipeSlot.LOWER_RIGHT)
                .unregisteredAccessibleShapedRecipe());
        setCstItem(
            OreDictAlias.ITEM_STICKS,
            ConstellationAtlarSlot.UP_RIGHT_RIGHT,
            ConstellationAtlarSlot.UP_LEFT_LEFT,
            ConstellationAtlarSlot.DOWN_RIGHT_RIGHT,
            ConstellationAtlarSlot.DOWN_LEFT_LEFT);
        setCstItem(
            BlockInfusedWood.WoodType.INFUSED.asStack(),
            ConstellationAtlarSlot.DOWN_DOWN_RIGHT,
            ConstellationAtlarSlot.DOWN_DOWN_LEFT);
    }

    @Nonnull
    @Override
    public ItemStack getOutputForMatching() {
        return new ItemStack(ItemsAS.sextant);
    }

    @Nonnull
    @Override
    public ItemStack getOutputForRender() {
        ItemStack adv = new ItemStack(ItemsAS.sextant);
        ItemSextant.setAdvanced(adv);
        return adv;
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar altar) {
        ItemStack sextant = altar.getInventoryHandler()
            .getStackInSlot(ShapedRecipeSlot.LOWER_CENTER.getSlotID());
        sextant = ItemUtils.copyStackWithSize(sextant, sextant.getCount());
        ItemSextant.setAdvanced(sextant);
        return sextant;
    }

    @Override
    public boolean matches(TileAltar altar, TileReceiverBaseInventory.ItemHandlerTile invHandler,
                           boolean ignoreStarlightRequirement) {
        ItemStack sextant = invHandler.getStackInSlot(ShapedRecipeSlot.LOWER_CENTER.getSlotID());
        if (ItemSextant.isAdvanced(sextant)) {
            return false;
        }
        return super.matches(altar, invHandler, ignoreStarlightRequirement);
    }
}
