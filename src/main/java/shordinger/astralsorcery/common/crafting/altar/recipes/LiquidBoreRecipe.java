/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.altar.recipes;

import static hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe.Builder.newShapedRecipe;

import java.awt.*;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.EntityComplexFX;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.client.effect.light.EffectLightbeam;
import shordinger.astralsorcery.common.block.BlockMarble;
import shordinger.astralsorcery.common.crafting.ISpecialCraftingEffects;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import shordinger.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.item.ItemCraftingComponent;
import shordinger.astralsorcery.common.lib.Constellations;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.tile.TileBore;
import shordinger.astralsorcery.common.util.OreDictAlias;
import shordinger.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LiquidBoreRecipe
 * Created by HellFirePvP
 * Date: 21.02.2018 / 20:57
 */
public class LiquidBoreRecipe extends TraitRecipe implements ISpecialCraftingEffects {

    public LiquidBoreRecipe() {
        super(
            newShapedRecipe("internal/altar/bore_head_liquid", TileBore.BoreType.LIQUID.asStack())
                .addPart(ItemHandle.getCrystalVariant(false, false), ShapedRecipeSlot.UPPER_CENTER)
                .addPart(
                    BlockMarble.MarbleBlockType.RUNED.asStack(),
                    ShapedRecipeSlot.UPPER_RIGHT,
                    ShapedRecipeSlot.RIGHT,
                    ShapedRecipeSlot.LOWER_RIGHT,
                    ShapedRecipeSlot.UPPER_LEFT,
                    ShapedRecipeSlot.LEFT,
                    ShapedRecipeSlot.LOWER_LEFT)
                .addPart(
                    ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
                    ShapedRecipeSlot.CENTER,
                    ShapedRecipeSlot.LOWER_CENTER)
                .unregisteredAccessibleShapedRecipe());
        setCstItem(
            BlockMarble.MarbleBlockType.RUNED.asStack(),
            ConstellationRecipe.ConstellationAtlarSlot.UP_UP_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.UP_LEFT_LEFT,
            ConstellationRecipe.ConstellationAtlarSlot.UP_UP_RIGHT,
            ConstellationRecipe.ConstellationAtlarSlot.UP_RIGHT_RIGHT);
        setAttItem(
            OreDictAlias.ITEM_GOLD_INGOT,
            AttunementRecipe.AttunementAltarSlot.UPPER_LEFT,
            AttunementRecipe.AttunementAltarSlot.UPPER_RIGHT);
        setInnerTraitItem(
            OreDictAlias.ITEM_GOLD_INGOT,
            TraitRecipe.TraitRecipeSlot.LEFT_CENTER,
            TraitRecipe.TraitRecipeSlot.RIGHT_CENTER);
        setInnerTraitItem(BlockMarble.MarbleBlockType.RUNED.asStack(), TraitRecipe.TraitRecipeSlot.UPPER_CENTER);
        setInnerTraitItem(ItemCraftingComponent.MetaType.RESO_GEM.asStack(), TraitRecipe.TraitRecipeSlot.LOWER_CENTER);
        addOuterTraitItem(ItemCraftingComponent.MetaType.STARDUST.asStack());
        addOuterTraitItem(ItemCraftingComponent.MetaType.STARDUST.asStack());
        setPassiveStarlightRequirement(4400);
        setRequiredConstellation(Constellations.octans);
    }

    @Override
    public AbstractAltarRecipe copyNewEffectInstance() {
        return new LiquidBoreRecipe();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, ActiveCraftingTask.CraftingState state, long tick, Random rand) {
        super.onCraftClientTick(altar, state, tick, rand);

        if (state == ActiveCraftingTask.CraftingState.ACTIVE && tick % 10 == 0 && rand.nextBoolean()) {
            float height = 5;

            Vector3 position = new Vector3(altar).add(0.5, 0, 0.5);
            position.add(
                rand.nextFloat() * 3 * (rand.nextBoolean() ? 1 : -1),
                0,
                rand.nextFloat() * 3 * (rand.nextBoolean() ? 1 : -1));
            Vector3 target = position.clone()
                .addY(height);

            EffectLightbeam beam = EffectHandler.getInstance()
                .lightbeam(target, position, 0.8F);
            beam.setAlphaMultiplier(1F)
                .setMaxAge(20);
            beam.setAlphaFunction(EntityComplexFX.AlphaFunction.FADE_OUT);
            beam.setDistanceCapSq(Config.maxEffectRenderDistanceSq * 5);

            for (int i = 0; i < 170; i++) {
                float perc = rand.nextFloat();

                Vector3 mot = new Vector3(
                    rand.nextFloat() * 0.08 * (rand.nextBoolean() ? 1 : -1) * (1 - perc),
                    0,
                    rand.nextFloat() * 0.08 * (rand.nextBoolean() ? 1 : -1) * (1 - perc));

                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    position.clone()
                        .addY(height * perc));
                p.motion(mot.getX(), mot.getY(), mot.getZ())
                    .gravity(0.004);
                p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
                    .setAlphaMultiplier(1F);
                p.scale(0.2F + rand.nextFloat() * 0.1F)
                    .setMaxAge(20);
                if (rand.nextBoolean()) {
                    p.setColor(Color.WHITE);
                }
            }
        }
    }
}
