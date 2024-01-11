/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.altar.recipes;

import javax.annotation.Nonnull;

import shordinger.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import shordinger.astralsorcery.common.constellation.IMinorConstellation;
import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.crafting.helper.ShapeMap;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import shordinger.astralsorcery.common.item.ItemCraftingComponent;
import shordinger.astralsorcery.common.item.block.ItemCollectorCrystal;
import shordinger.astralsorcery.common.item.crystal.CrystalProperties;
import shordinger.astralsorcery.common.item.crystal.base.ItemTunedCrystalBase;
import shordinger.astralsorcery.common.item.useables.ItemUsableDust;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.lib.ItemsAS;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.util.OreDictAlias;
import shordinger.wrapper.net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CollectorCrystalRecipe
 * Created by HellFirePvP
 * Date: 14.11.2016 / 02:03
 */
public class CollectorCrystalRecipe extends ConstellationRecipe {

    private final boolean celestial;
    private final ItemStack renderOutput;

    public CollectorCrystalRecipe(boolean celestial) {
        super(
            shapedRecipe(
                (celestial ? "celestial" : "") + "collectorcrystal",
                celestial ? BlocksAS.celestialCollectorCrystal : BlocksAS.collectorCrystal)
                .addPart(
                    (celestial ? ItemsAS.tunedCelestialCrystal : ItemsAS.tunedRockCrystal),
                    ShapedRecipeSlot.CENTER)
                .forceEmptySpaces()
                .unregisteredAccessibleShapedRecipe());
        setAttItem(ItemCraftingComponent.MetaType.RESO_GEM.asStack(), AttunementAltarSlot.values());
        setCstItem(
            OreDictAlias.ITEM_STARMETAL_DUST,
            ConstellationAtlarSlot.UP_UP_LEFT,
            ConstellationAtlarSlot.UP_UP_RIGHT,
            ConstellationAtlarSlot.DOWN_DOWN_LEFT,
            ConstellationAtlarSlot.DOWN_DOWN_RIGHT);
        setCstItem(
            ItemUsableDust.DustType.ILLUMINATION.asStack(),
            ConstellationAtlarSlot.UP_LEFT_LEFT,
            ConstellationAtlarSlot.UP_RIGHT_RIGHT,
            ConstellationAtlarSlot.DOWN_LEFT_LEFT,
            ConstellationAtlarSlot.DOWN_RIGHT_RIGHT);
        this.celestial = celestial;
        ItemStack stack = new ItemStack(celestial ? BlocksAS.celestialCollectorCrystal : BlocksAS.collectorCrystal);
        ItemCollectorCrystal.setType(
            stack,
            celestial ? BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL
                : BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL);
        this.renderOutput = stack;
        setPassiveStarlightRequirement(2600);
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar altar) {
        ItemStack center = centralGridMap.get(ShapedRecipeSlot.CENTER)
            .getApplicableItems()
            .get(0);
        if (center.isEmpty() || !(center.getItem() instanceof ItemTunedCrystalBase)) return ItemStack.EMPTY;
        ItemStack out = super.getOutput(centralGridMap, altar);
        IWeakConstellation attuned = ItemTunedCrystalBase.getMainConstellation(center);
        IMinorConstellation trait = ItemTunedCrystalBase.getTrait(center);
        CrystalProperties prop = CrystalProperties.getCrystalProperties(center);
        if (attuned == null || prop == null) return ItemStack.EMPTY;
        ItemCollectorCrystal.setConstellation(out, attuned);
        ItemCollectorCrystal.setTraitConstellation(out, trait);
        CrystalProperties.applyCrystalProperties(out, prop);
        ItemCollectorCrystal.setType(
            out,
            celestial ? BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL
                : BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL);
        return out;
    }

    @Nonnull
    @Override
    public ItemStack getOutputForRender() {
        return renderOutput;
    }

}
