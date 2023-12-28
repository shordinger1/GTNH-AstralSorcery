/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.cape.impl;

import java.awt.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.ClientScheduler;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.EntityComplexFX;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.constellation.cape.CapeArmorEffect;
import shordinger.astralsorcery.common.lib.Constellations;
import shordinger.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeEffectAevitas
 * Created by HellFirePvP
 * Date: 10.10.2017 / 21:14
 */
public class CapeEffectAevitas extends CapeArmorEffect {

    private static final int ticksPerRound = 30;

    private static float range = 10F;
    private static float potency = 1F;
    private static float turnChance = 0.2F;

    private static float healPerCycle = 0.04F;
    private static float feedChancePerCycle = 0.01F;
    private static int foodLevelPerCycle = 1;
    private static float foodSaturationLevelPerCycle = 0.1F;

    public CapeEffectAevitas(NBTTagCompound cmp) {
        super(cmp, "aevitas");
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.aevitas;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playActiveParticleTick(EntityPlayer pl) {
        playConstellationCapeSparkles(pl, 0.1F);

        Color c = getAssociatedConstellation().getConstellationColor();
        if (c != null) {
            double x = pl.posX + (pl.width / 2);
            double y = pl.posY;
            double z = pl.posZ + (pl.width / 2);
            Vector3 centerOffset = new Vector3(x, y, z);
            float tick = (float) (ClientScheduler.getClientTick() % ticksPerRound);
            Vector3 axis = Vector3.RotAxis.Y_AXIS;
            Vector3 circleVec = axis.clone()
                .perpendicular()
                .normalize()
                .multiply(range * 0.9 * rand.nextFloat());
            double deg = 360D * (tick / (float) (ticksPerRound));
            Vector3 mov = circleVec.clone()
                .rotate(Math.toRadians(deg), axis.clone());

            Vector3 at = mov.clone()
                .add(centerOffset);

            EntityFXFacingParticle p;
            if (rand.nextFloat() < 0.2) {
                p = EffectHelper.genericFlareParticle(at);
                p.setColor(c)
                    .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                p.scale(rand.nextFloat() * 0.5F + 0.3F);
                if (rand.nextInt(3) == 0) {
                    p.setColor(IConstellation.weak);
                }
                p.setMaxAge(30 + rand.nextInt(20));

                if (rand.nextFloat() < 0.8F) {
                    p = EffectHelper.genericFlareParticle(at);
                    p.setColor(Color.WHITE)
                        .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                    p.scale(rand.nextFloat() * 0.2F + 0.1F);
                    p.setMaxAge(20 + rand.nextInt(10));
                }
            }

            if (rand.nextFloat() < 0.2) {
                deg += 180;
                mov = circleVec.clone()
                    .rotate(Math.toRadians(deg), axis.clone());

                at = mov.clone()
                    .add(centerOffset);

                p = EffectHelper.genericFlareParticle(at);
                p.setColor(c)
                    .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                p.scale(rand.nextFloat() * 0.5F + 0.3F);
                if (rand.nextInt(3) == 0) {
                    p.setColor(IConstellation.weak);
                }
                p.setMaxAge(30 + rand.nextInt(20));

                if (rand.nextFloat() < 0.8F) {
                    p = EffectHelper.genericFlareParticle(at);
                    p.setColor(Color.WHITE)
                        .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                    p.scale(rand.nextFloat() * 0.2F + 0.1F);
                    p.setMaxAge(20 + rand.nextInt(10));
                }
            }
        }
    }

    public float getTurnChance() {
        return turnChance;
    }

    public float getRange() {
        return range;
    }

    public float getPotency() {
        return potency;
    }

    public float getHealPerCycle() {
        return healPerCycle;
    }

    public int getFoodLevelPerCycle() {
        return foodLevelPerCycle;
    }

    public float getFoodSaturationLevelPerCycle() {
        return foodSaturationLevelPerCycle;
    }

    public float getFeedChancePerCycle() {
        return feedChancePerCycle;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        range = cfg.getFloat(
            getKey() + "Range",
            getConfigurationSection(),
            range,
            1,
            32,
            "Defines the radius (in blocks) for the aoe effect.");
        potency = cfg.getFloat(
            getKey() + "Potency",
            getConfigurationSection(),
            potency,
            0,
            1,
            "Defines the multiplier if the aoe will happen at all");
        turnChance = cfg.getFloat(
            getKey() + "PlantTransformChance",
            getConfigurationSection(),
            turnChance,
            0,
            1,
            "Defines the chance that the aoe will search for a plant to turn into another plant.");
        feedChancePerCycle = cfg.getFloat(
            getKey() + "FeedChancePerCycle",
            getConfigurationSection(),
            feedChancePerCycle,
            0,
            1F,
            "Defines the chance that food-level increasing effects will happen on a specific cape-effect-cycle/tick");
        healPerCycle = cfg.getFloat(
            getKey() + "HealPerCycle",
            getConfigurationSection(),
            healPerCycle,
            0F,
            4F,
            "Defines the amount of health that is regenerated per cape-effect-cycle/tick");
        foodLevelPerCycle = cfg.getInt(
            getKey() + "FoodLevelPerCycle",
            getConfigurationSection(),
            foodLevelPerCycle,
            0,
            5,
            "Defines the food-level that is 'fed' to the player per cape-effect-cycle/tick");
        foodSaturationLevelPerCycle = cfg.getFloat(
            getKey() + "FoodSaturationPerCycle",
            getConfigurationSection(),
            foodSaturationLevelPerCycle,
            0,
            5F,
            "Defines the amount of saturation that is 'fed' to the player per cape-effect-cycle/tick");
    }

}
