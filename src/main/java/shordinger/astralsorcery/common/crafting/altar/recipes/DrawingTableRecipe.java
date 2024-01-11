/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.altar.recipes;

import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.EntityComplexFX;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.client.effect.light.EffectLightbeam;
import shordinger.astralsorcery.common.block.BlockInfusedWood;
import shordinger.astralsorcery.common.block.BlockMarble;
import shordinger.astralsorcery.common.crafting.ISpecialCraftingEffects;
import shordinger.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import shordinger.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import shordinger.astralsorcery.common.item.ItemCraftingComponent;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.OreDictAlias;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DrawingTableRecipe
 * Created by HellFirePvP
 * Date: 24.04.2017 / 08:30
 */
public class DrawingTableRecipe extends ConstellationRecipe implements ISpecialCraftingEffects {

    public DrawingTableRecipe() {
        super(shapedRecipe("drawingtable", BlocksAS.drawingTable)
                .addPart(OreDictAlias.ITEM_STARMETAL_INGOT,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_CENTER,
                        ShapedRecipeSlot.LOWER_RIGHT)
        .unregisteredAccessibleShapedRecipe());
        setAttItem(OreDictAlias.ITEM_DYE_ALL,
                AttunementAltarSlot.UPPER_LEFT,
                AttunementAltarSlot.UPPER_RIGHT);
        setAttItem(BlockMarble.MarbleBlockType.RUNED.asStack(),
                AttunementAltarSlot.LOWER_LEFT,
                AttunementAltarSlot.LOWER_RIGHT);
        setCstItem(BlockInfusedWood.WoodType.COLUMN.asStack(),
                ConstellationAtlarSlot.UP_LEFT_LEFT,
                ConstellationAtlarSlot.DOWN_LEFT_LEFT,
                ConstellationAtlarSlot.UP_RIGHT_RIGHT,
                ConstellationAtlarSlot.DOWN_RIGHT_RIGHT);
    }

    @Override
    public AbstractAltarRecipe copyNewEffectInstance() {
        return new DrawingTableRecipe();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, ActiveCraftingTask.CraftingState state, long tick, Random rand) {
        super.onCraftClientTick(altar, state, tick, rand);

        if(state == ActiveCraftingTask.CraftingState.ACTIVE) {
            Vector3 altarPos = new Vector3(altar);
            for (int i = 0; i < 2; i++) {
                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                        altarPos.getX() - 3 + rand.nextFloat() * 7,
                        altarPos.getY(),
                        altarPos.getZ() - 3 + rand.nextFloat() * 7
                );
                p.gravity(0.004).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(rand.nextFloat() * 0.2F + 0.1F);
                p.setColor(new Color(Color.HSBtoRGB(rand.nextFloat() * 360, 1F, 1F)));
            }

            if(rand.nextInt(10) == 0) {
                Vector3 from = new Vector3(altar).add(0.5, 0.3, 0.5);
                MiscUtils.applyRandomOffset(from, rand, 0.4F);
                EffectLightbeam lightbeam = EffectHandler.getInstance().lightbeam(from.clone().addY(4 + rand.nextInt(2)), from, 1);
                lightbeam.setMaxAge(64);
                lightbeam.setColorOverlay(new Color(Color.HSBtoRGB(rand.nextFloat() * 360, 1F, 1F)));
            }
        }
    }

}
