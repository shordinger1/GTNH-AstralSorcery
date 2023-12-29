/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.altar.recipes;

import java.awt.*;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.EntityComplexFX;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.crafting.ISpecialCraftingEffects;
import shordinger.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import shordinger.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import shordinger.astralsorcery.common.crafting.helper.AccessibleRecipe;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.migration.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EnchantmentAmuletRecipe
 * Created by HellFirePvP
 * Date: 23.02.2018 / 20:21
 */
public class EnchantmentAmuletRecipe extends ConstellationRecipe implements ISpecialCraftingEffects {

    public EnchantmentAmuletRecipe(AccessibleRecipe recipe) {
        super(recipe);
    }

    @Override
    public AbstractAltarRecipe copyNewEffectInstance() {
        return new EnchantmentAmuletRecipe(this.getNativeRecipe());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, ActiveCraftingTask.CraftingState state, long tick, Random rand) {
        super.onCraftClientTick(altar, state, tick, rand);

        if (state == ActiveCraftingTask.CraftingState.ACTIVE) {
            Vector3 altarPos = new Vector3(altar);
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                altarPos.getX() - 3 + rand.nextFloat() * 7,
                altarPos.getY(),
                altarPos.getZ() - 3 + rand.nextFloat() * 7);
            p.gravity(0.004)
                .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
                .scale(rand.nextFloat() * 0.1F + 0.25F);
            p.setColor(new Color(Color.HSBtoRGB(rand.nextFloat() * 360, 1F, 1F)));

            float percCycle = (float) ((((float) (tick % 200)) / 200F) * 2 * Math.PI);
            int parts = 6;
            for (int i = 0; i < parts; i++) {

                // inner
                float angleSwirl = 110F;
                Vector3 center = new Vector3(altar).add(0.5, 1.1, 0.5);
                Vector3 v = new Vector3(1, 0, 0);
                float originalAngle = (((float) i) / ((float) parts)) * 360F;
                double angle = originalAngle + (MathHelper.sin(percCycle) * angleSwirl);
                v.rotate(Math.toRadians(angle), Vector3.RotAxis.Y_AXIS)
                    .normalize()
                    .multiply(2.2);
                Vector3 pos = center.clone()
                    .add(v);

                Vector3 mot = center.clone()
                    .subtract(pos)
                    .normalize()
                    .multiply(0.09);

                EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(pos);
                particle.gravity(0.004)
                    .enableAlphaFade(EntityComplexFX.AlphaFunction.PYRAMID)
                    .scale(rand.nextFloat() * 0.4F + 0.15F);
                particle.setColor(Color.WHITE);
                particle.motion(mot.getX(), mot.getY(), mot.getZ());

                // outer
                angleSwirl = 180F;
                center = new Vector3(altar).add(0.5, 0.1, 0.5);
                v = new Vector3(1, 0, 0);
                originalAngle = (((float) i) / ((float) parts)) * 360F;
                angle = originalAngle + (MathHelper.sin(percCycle) * angleSwirl);
                v.rotate(-Math.toRadians(angle), Vector3.RotAxis.Y_AXIS)
                    .normalize()
                    .multiply(4);
                pos = center.clone()
                    .add(v);

                mot = center.clone()
                    .subtract(pos)
                    .normalize()
                    .multiply(0.15);

                particle = EffectHelper.genericFlareParticle(pos);
                particle.gravity(0.004)
                    .enableAlphaFade(EntityComplexFX.AlphaFunction.PYRAMID)
                    .scale(rand.nextFloat() * 0.4F + 0.27F);
                particle.setMaxAge(50);
                particle.setColor(Color.WHITE);
                particle.motion(mot.getX(), mot.getY(), mot.getZ());
            }
        }
    }
}
