/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.altar.recipes;

import java.util.Random;

import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.client.effect.light.EffectLightbeam;
import shordinger.astralsorcery.common.block.BlockMarble;
import shordinger.astralsorcery.common.crafting.INighttimeRecipe;
import shordinger.astralsorcery.common.crafting.ISpecialCraftingEffects;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import shordinger.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipe;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.OreDictAlias;
import shordinger.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttunementAltarRecipe
 * Created by HellFirePvP
 * Date: 07.12.2016 / 13:47
 */
public class AttunementAltarRecipe extends AttunementRecipe implements INighttimeRecipe, ISpecialCraftingEffects {

    private static final Vector3[] offsetPillarsT2 = new Vector3[]{new Vector3(3, 2, 3), new Vector3(-3, 2, 3),
        new Vector3(3, 2, -3), new Vector3(-3, 2, -3)};
    private static final Vector3[] offsetPillarsT3 = new Vector3[]{new Vector3(4, 3, 4), new Vector3(-4, 3, 4),
        new Vector3(4, 3, -4), new Vector3(-4, 3, -4)};

    public AttunementAltarRecipe() {
        super(
            ShapedRecipe.Builder.newShapedRecipe("attunementaltar", BlocksAS.attunementAltar)
                .addPart(ItemHandle.getCrystalVariant(false, false), ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.ITEM_STARMETAL_INGOT, ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT)
                .addPart(BlocksAS.attunementRelay, ShapedRecipeSlot.LOWER_CENTER)
                .addPart(
                    BlockMarble.MarbleBlockType.RUNED.asStack(),
                    ShapedRecipeSlot.LOWER_LEFT,
                    ShapedRecipeSlot.LOWER_RIGHT)
                .unregisteredAccessibleShapedRecipe());
        setAttItem(OreDictAlias.ITEM_AQUAMARINE, AttunementAltarSlot.UPPER_LEFT, AttunementAltarSlot.UPPER_RIGHT);
        setAttItem(
            BlockMarble.MarbleBlockType.RUNED.asStack(),
            AttunementAltarSlot.LOWER_LEFT,
            AttunementAltarSlot.LOWER_RIGHT);
    }

    @Override
    public AbstractAltarRecipe copyNewEffectInstance() {
        return new AttunementAltarRecipe();
    }

    @Override
    public void onCraftClientTick(TileAltar altar, ActiveCraftingTask.CraftingState state, long tick, Random rand) {
        super.onCraftClientTick(altar, state, tick, rand);

        if (state == ActiveCraftingTask.CraftingState.ACTIVE) {
            Vector3 randomPos = new Vector3(altar);
            randomPos.add(rand.nextFloat() * 7 - 3, 0.1, rand.nextFloat() * 7 - 3);
            EntityFXFacingParticle p = EffectHelper
                .genericFlareParticle(randomPos.getX(), randomPos.getY(), randomPos.getZ());
            p.scale(0.7F)
                .gravity(0.02);
            if (tick % 50 == 0) {
                Vector3 vec = new Vector3(altar).add(0.5, 0.5, 0.5);
                switch (altar.getAltarLevel()) {
                    case DISCOVERY:
                        break;
                    case ATTUNEMENT:
                        for (Vector3 offset : offsetPillarsT2) {
                            EffectHandler.getInstance()
                                .lightbeam(
                                    offset.clone()
                                        .add(altar.getPos())
                                        .add(0.5, 0.5, 0.5),
                                    vec,
                                    1.2F);
                        }
                        break;
                    case CONSTELLATION_CRAFT:
                        for (Vector3 offset : offsetPillarsT3) {
                            EffectHandler.getInstance()
                                .lightbeam(
                                    offset.clone()
                                        .add(altar.getPos())
                                        .add(0.5, 0.5, 0.5),
                                    vec,
                                    1.2F);
                        }
                        break;
                    case TRAIT_CRAFT:
                        break;
                    case BRILLIANCE:
                        break;
                    default:
                        break;
                }
            }

            if (rand.nextInt(10) == 0) {
                Vector3 from = new Vector3(altar).add(0.5, -0.6, 0.5);
                MiscUtils.applyRandomOffset(from, rand, 1.8F);
                from.setY(
                    altar.getPos()
                        .getY() - 0.6
                        + 1 * rand.nextFloat() * (rand.nextBoolean() ? 1 : -1));
                EffectLightbeam lightbeam = EffectHandler.getInstance()
                    .lightbeam(
                        from.clone()
                            .addY(5 + rand.nextInt(3)),
                        from,
                        1);
                lightbeam.setMaxAge(64);
            }
        }

    }

}
