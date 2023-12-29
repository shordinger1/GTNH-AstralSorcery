/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.altar.recipes;

import static shordinger.astralsorcery.common.crafting.helper.ShapedRecipe.Builder.newShapedRecipe;

import java.awt.*;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.EntityComplexFX;
import shordinger.astralsorcery.client.effect.controller.ControllerNoisePlane;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.block.BlockMarble;
import shordinger.astralsorcery.common.crafting.ISpecialCraftingEffects;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import shordinger.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import shordinger.astralsorcery.common.item.ItemCraftingComponent;
import shordinger.astralsorcery.common.item.useables.ItemUsableDust;
import shordinger.astralsorcery.common.lib.Constellations;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.tile.TileBore;
import shordinger.astralsorcery.common.util.OreDictAlias;
import shordinger.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: VortexBoreRecipe
 * Created by HellFirePvP
 * Date: 21.02.2018 / 21:06
 */
public class VortexBoreRecipe extends TraitRecipe implements ISpecialCraftingEffects {

    public VortexBoreRecipe() {
        super(
            newShapedRecipe("internal/altar/bore_head_vortex", TileBore.BoreType.VORTEX.asStack())
                .addPart(ItemHandle.getCrystalVariant(false, false), ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.ITEM_STARMETAL_DUST, ShapedRecipeSlot.LOWER_LEFT, ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(
                    BlockMarble.MarbleBlockType.RUNED.asStack(),
                    ShapedRecipeSlot.UPPER_LEFT,
                    ShapedRecipeSlot.UPPER_RIGHT)
                .unregisteredAccessibleShapedRecipe());
        setInnerTraitItem(OreDictAlias.ITEM_STARMETAL_DUST, TraitRecipe.TraitRecipeSlot.LOWER_CENTER);
        setInnerTraitItem(
            BlockMarble.MarbleBlockType.RUNED.asStack(),
            TraitRecipe.TraitRecipeSlot.LEFT_CENTER,
            TraitRecipe.TraitRecipeSlot.RIGHT_CENTER,
            TraitRecipe.TraitRecipeSlot.UPPER_CENTER);
        setCstItem(BlockMarble.MarbleBlockType.RUNED.asStack(), ConstellationRecipe.ConstellationAtlarSlot.values());
        setAttItem(
            BlockMarble.MarbleBlockType.RUNED.asStack(),
            AttunementRecipe.AttunementAltarSlot.LOWER_LEFT,
            AttunementRecipe.AttunementAltarSlot.LOWER_RIGHT);
        setAttItem(
            OreDictAlias.ITEM_GOLD_INGOT,
            AttunementRecipe.AttunementAltarSlot.UPPER_LEFT,
            AttunementRecipe.AttunementAltarSlot.UPPER_RIGHT);
        addOuterTraitItem(ItemUsableDust.DustType.NOCTURNAL.asStack());
        addOuterTraitItem(ItemCraftingComponent.MetaType.RESO_GEM.asStack());
        addOuterTraitItem(ItemUsableDust.DustType.NOCTURNAL.asStack());
        addOuterTraitItem(ItemCraftingComponent.MetaType.RESO_GEM.asStack());
        addOuterTraitItem(ItemUsableDust.DustType.NOCTURNAL.asStack());
        setPassiveStarlightRequirement(4400);
        setRequiredConstellation(Constellations.vicio);
    }

    @Override
    public AbstractAltarRecipe copyNewEffectInstance() {
        return new VortexBoreRecipe();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, ActiveCraftingTask.CraftingState state, long tick, Random rand) {
        super.onCraftClientTick(altar, state, tick, rand);

        if (state == ActiveCraftingTask.CraftingState.ACTIVE) {
            ActiveCraftingTask current = this.getCurrentTask(altar);
            if (current != null) {
                Vector3 pos = new Vector3(altar).add(0.5, 0.5, 0.5);

                Vector3 target = new Vector3(altar).add(0.5, 4.5, 0.5);
                for (int i = 0; i < 4; i++) {
                    Vector3 p = new Vector3(altar).add(-3 + rand.nextFloat() * 7, 0.02, -3 + rand.nextFloat() * 7);
                    EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(p);
                    particle.gravity(0.004)
                        .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
                        .scale(rand.nextFloat() * 0.2F + 0.15F);
                    particle.setColor(Color.WHITE);
                    Vector3 mot = target.clone()
                        .subtract(p)
                        .normalize()
                        .multiply(0.09);
                    particle.motion(mot.getX(), mot.getY(), mot.getZ());
                }

                ControllerNoisePlane ctrl = current.getEffectContained(0, (index) -> new ControllerNoisePlane(1.2F));
                for (int i = 0; i < 2; i++) {
                    EntityFXFacingParticle p = ctrl.setupParticle();
                    p.updatePosition(pos.getX(), pos.getY() + 4, pos.getZ())
                        .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
                        .motion(
                            rand.nextFloat() * 0.005 * (rand.nextBoolean() ? 1 : -1),
                            rand.nextFloat() * 0.005 * (rand.nextBoolean() ? 1 : -1),
                            rand.nextFloat() * 0.005 * (rand.nextBoolean() ? 1 : -1))
                        .scale(0.15F + rand.nextFloat() * 0.05F)
                        .setMaxAge(30 + rand.nextInt(15));
                }
                ctrl = current.getEffectContained(1, (index) -> new ControllerNoisePlane(1.8F));
                for (int i = 0; i < 2; i++) {
                    EntityFXFacingParticle p = ctrl.setupParticle();
                    p.updatePosition(pos.getX(), pos.getY() + 4, pos.getZ())
                        .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
                        .motion(
                            rand.nextFloat() * 0.005 * (rand.nextBoolean() ? 1 : -1),
                            rand.nextFloat() * 0.005 * (rand.nextBoolean() ? 1 : -1),
                            rand.nextFloat() * 0.005 * (rand.nextBoolean() ? 1 : -1))
                        .scale(0.15F + rand.nextFloat() * 0.05F)
                        .setMaxAge(30 + rand.nextInt(15));
                }
            }
        }
    }
}
