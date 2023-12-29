/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.altar.recipes;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import shordinger.astralsorcery.common.crafting.helper.ShapeMap;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipe;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import shordinger.astralsorcery.common.item.ItemCraftingComponent;
import shordinger.astralsorcery.common.item.tool.ItemSkyResonator;
import shordinger.astralsorcery.common.item.useables.ItemUsableDust;
import shordinger.astralsorcery.common.lib.ItemsAS;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.OreDictAlias;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ResonatorStructureRecipe
 * Created by HellFirePvP
 * Date: 26.08.2018 / 21:48
 */
public class ResonatorStructureRecipe extends AttunementRecipe {

    public ResonatorStructureRecipe() {
        super(
            ShapedRecipe.Builder
                .newShapedRecipe(
                    "internal/altar/resonator/structure",
                    ItemSkyResonator.setCurrentUpgradeUnsafe(
                        ItemSkyResonator.setUpgradeUnlocked(
                            ItemSkyResonator.setEnhanced(new ItemStack(ItemsAS.skyResonator)),
                            ItemSkyResonator.ResonatorUpgrade.AREA_SIZE),
                        ItemSkyResonator.ResonatorUpgrade.AREA_SIZE))
                .addPart(ItemsAS.skyResonator, ShapedRecipeSlot.CENTER)
                .addPart(
                    ItemUsableDust.DustType.ILLUMINATION.asStack(),
                    ShapedRecipeSlot.UPPER_LEFT,
                    ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(OreDictAlias.ITEM_STARMETAL_DUST, ShapedRecipeSlot.LOWER_LEFT, ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(
                    ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                    ShapedRecipeSlot.LEFT,
                    ShapedRecipeSlot.RIGHT)
                .unregisteredAccessibleShapedRecipe());

        setAttItem(OreDictAlias.ITEM_STARMETAL_DUST, AttunementAltarSlot.LOWER_LEFT, AttunementAltarSlot.LOWER_RIGHT);
        setAttItem(
            ItemUsableDust.DustType.ILLUMINATION.asStack(),
            AttunementAltarSlot.UPPER_LEFT,
            AttunementAltarSlot.UPPER_RIGHT);

        setPassiveStarlightRequirement(900);
    }

    @Override
    public boolean matches(TileAltar altar, TileReceiverBaseInventory.ItemHandlerTile invHandler,
                           boolean ignoreStarlightRequirement) {
        ItemStack center = invHandler.getStackInSlot(ShapedRecipeSlot.CENTER.getSlotID());
        if (center.isEmpty() || !(center.getItem() instanceof ItemSkyResonator)) {
            return false;
        }
        List<ItemSkyResonator.ResonatorUpgrade> out = ItemSkyResonator.getUpgrades(center);
        return !out.contains(ItemSkyResonator.ResonatorUpgrade.AREA_SIZE)
            && super.matches(altar, invHandler, ignoreStarlightRequirement);
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar altar) {
        ItemStack reso = new ItemStack(ItemsAS.skyResonator);
        ItemStack center = altar.getInventoryHandler()
            .getStackInSlot(ShapedRecipeSlot.CENTER.getSlotID());
        if (!center.isEmpty() && center.getItem() instanceof ItemSkyResonator) {
            reso = ItemUtils.copyStackWithSize(center, center.getCount());
        }
        ItemSkyResonator.setEnhanced(reso);
        ItemSkyResonator.setUpgradeUnlocked(reso, ItemSkyResonator.ResonatorUpgrade.AREA_SIZE);
        return reso;
    }

}
