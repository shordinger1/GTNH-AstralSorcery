/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.altar.recipes;

import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.util.SpriteLibrary;
import shordinger.astralsorcery.common.block.BlockInfusedWood;
import shordinger.astralsorcery.common.block.BlockMarble;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import shordinger.astralsorcery.common.crafting.helper.ShapeMap;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import shordinger.astralsorcery.common.item.ItemCraftingComponent;
import shordinger.astralsorcery.common.item.crystal.CrystalProperties;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.util.OreDictAlias;
import shordinger.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PrismLensRecipe
 * Created by HellFirePvP
 * Date: 02.11.2016 / 21:07
 */
public class PrismLensRecipe extends ConstellationRecipe {

    public PrismLensRecipe() {
        super(
            shapedRecipe("crystalprism", BlocksAS.lensPrism)
                .addPart(
                    OreDictAlias.BLOCK_GLASS_PANE_NOCOLOR,
                    ShapedRecipeSlot.UPPER_LEFT,
                    ShapedRecipeSlot.LOWER_LEFT,
                    ShapedRecipeSlot.UPPER_RIGHT,
                    ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(
                    ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                    ShapedRecipeSlot.LEFT,
                    ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.ITEM_STARMETAL_DUST, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.LOWER_CENTER)
                .addPart(ItemHandle.getCrystalVariant(false, false), ShapedRecipeSlot.CENTER)
                .unregisteredAccessibleShapedRecipe());

        setAttItem(
            BlockMarble.MarbleBlockType.RUNED.asStack(),
            AttunementAltarSlot.LOWER_LEFT,
            AttunementAltarSlot.LOWER_RIGHT);
        setCstItem(
            BlockMarble.MarbleBlockType.RUNED.asStack(),
            ConstellationAtlarSlot.DOWN_DOWN_LEFT,
            ConstellationAtlarSlot.DOWN_DOWN_RIGHT);
        setCstItem(
            ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
            ConstellationAtlarSlot.UP_UP_LEFT,
            ConstellationAtlarSlot.UP_UP_RIGHT);
        setCstItem(
            BlockInfusedWood.WoodType.ENGRAVED.asStack(),
            ConstellationAtlarSlot.DOWN_RIGHT_RIGHT,
            ConstellationAtlarSlot.DOWN_LEFT_LEFT);
        setCstItem(
            OreDictAlias.ITEM_GOLD_INGOT,
            ConstellationAtlarSlot.UP_LEFT_LEFT,
            ConstellationAtlarSlot.UP_RIGHT_RIGHT);
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar altar) {
        ItemStack lens = super.getOutput(centralGridMap, altar);
        CrystalProperties crystalProp = CrystalProperties.getCrystalProperties(
            centralGridMap.get(ShapedRecipeSlot.CENTER)
                .getApplicableItems()
                .get(0));
        CrystalProperties.applyCrystalProperties(lens, crystalProp);
        return lens;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, ActiveCraftingTask.CraftingState state, long tick, Random rand) {
        super.onCraftClientTick(altar, state, tick, rand);

        if (state == ActiveCraftingTask.CraftingState.ACTIVE) {
            Vector3 altarVec = new Vector3(altar);
            if (tick % 48 == 0 && rand.nextBoolean()) {
                EffectHandler.getInstance()
                    .textureSpritePlane(SpriteLibrary.spriteCraftBurst, Vector3.RotAxis.Y_AXIS.clone())
                    .setPosition(altarVec.add(0.5, 0.05, 0.5))
                    .setScale(5 + rand.nextInt(2))
                    .setNoRotation(rand.nextInt(360));
            }
        }
    }
}
