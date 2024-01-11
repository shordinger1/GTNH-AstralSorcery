/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.effect.aoe;

import java.awt.*;
import java.util.List;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.constellation.IMinorConstellation;
import shordinger.astralsorcery.common.constellation.effect.ConstellationEffect;
import shordinger.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import shordinger.astralsorcery.common.constellation.effect.ConstellationEffectStatus;
import shordinger.astralsorcery.common.event.listener.EventHandlerEntity;
import shordinger.astralsorcery.common.lib.Constellations;
import shordinger.astralsorcery.common.tile.TileRitualPedestal;
import shordinger.astralsorcery.common.util.ILocatable;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.init.MobEffects;
import shordinger.wrapper.net.minecraft.potion.PotionEffect;
import shordinger.wrapper.net.minecraft.util.math.AxisAlignedBB;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.common.config.Configuration;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectVicio
 * Created by HellFirePvP
 * Date: 10.01.2017 / 21:52
 */
public class CEffectVicio extends ConstellationEffect implements ConstellationEffectStatus {

    public static boolean enabled = true;
    public static float effectRange = 24;
    public static int effectRangePerLens = 16;

    public CEffectVicio(@Nullable ILocatable origin) {
        super(origin, Constellations.vicio, "vicio");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility,
                                 boolean extendedEffects) {
        if (rand.nextInt(3) == 0) {
            Vector3 r = new Vector3(
                pos.getX() + rand.nextFloat() * 3 * (rand.nextBoolean() ? 1 : -1) + 0.5,
                pos.getY() + rand.nextFloat() * 2 + 0.5,
                pos.getZ() + rand.nextFloat() * 3 * (rand.nextBoolean() ? 1 : -1) + 0.5);
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(r);
            p.motion(
                (rand.nextFloat() * 0.07F) * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.07F) * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.07F) * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.45F)
                .setColor(new Color(200, 200, 255))
                .gravity(0.008)
                .setMaxAge(25);
            p = EffectHelper.genericFlareParticle(r);
            p.motion(0, rand.nextFloat() * 0.07F, 0);
            p.scale(0.45F)
                .setColor(new Color(200, 200, 255))
                .gravity(0.008)
                .setMaxAge(25);
        }
    }

    @Override
    public boolean runEffect(World world, BlockPos pos, int mirrorAmount, ConstellationEffectProperties modified,
                             @Nullable IMinorConstellation possibleTraitEffect) {
        if (!enabled) return false;
        boolean foundPlayer = false;
        double range = modified.getSize();
        if (modified.isCorrupted()) {
            List<EntityLivingBase> entities = world.getEntitiesWithinAABB(
                EntityLivingBase.class,
                new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(pos)
                    .grow(range));
            for (EntityLivingBase entity : entities) {
                if (entity instanceof EntityPlayerMP) {
                    EntityPlayerMP pl = (EntityPlayerMP) entity;
                    if (pl.interactionManager.getGameType()
                        .isSurvivalOrAdventure()) {
                        boolean prev = pl.capabilities.allowFlying;
                        pl.capabilities.allowFlying = false;
                        if (prev) {
                            pl.sendPlayerAbilities();
                        }
                    }
                }
                foundPlayer = true;
                entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 9));
                entity.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 200, 9));
            }
        } else {
            List<EntityPlayerMP> entities = world.getEntitiesWithinAABB(
                EntityPlayerMP.class,
                new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(pos)
                    .grow(range));
            for (EntityPlayerMP pl : entities) {
                if (EventHandlerEntity.ritualFlight.setOrAddTimeout(40, pl)) {
                    boolean prev = pl.capabilities.allowFlying;
                    pl.capabilities.allowFlying = true;
                    foundPlayer = true;
                    if (!prev) {
                        pl.sendPlayerAbilities();
                    }
                }
            }
        }
        return foundPlayer;
    }

    @Override
    @Deprecated
    public boolean playEffect(World world, BlockPos pos, float percStrength, ConstellationEffectProperties modified,
                              @Nullable IMinorConstellation possibleTraitEffect) {
        return false;
    }

    @Override
    public ConstellationEffectProperties provideProperties(int mirrorCount) {
        return new ConstellationEffectProperties(
            CEffectVicio.effectRange + mirrorCount * CEffectVicio.effectRangePerLens);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        effectRange = cfg.getFloat(
            getKey() + "Range",
            getConfigurationSection(),
            effectRange,
            1,
            512,
            "Defines the radius (in blocks) in which the ritual will allow the players to fly in.");
        effectRangePerLens = cfg.getInt(
            getKey() + "RangeIncreasePerLens",
            getConfigurationSection(),
            effectRangePerLens,
            1,
            128,
            "Defines the increase in radius the ritual will get per active lens enhancing the ritual.");
        enabled = cfg.getBoolean(
            getKey() + "Enabled",
            getConfigurationSection(),
            true,
            "Set to false to disable this ConstellationEffect.");
    }

}
