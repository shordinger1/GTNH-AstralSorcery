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
import shordinger.astralsorcery.common.CommonProxy;
import shordinger.astralsorcery.common.constellation.IMinorConstellation;
import shordinger.astralsorcery.common.constellation.effect.CEffectEntityCollect;
import shordinger.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import shordinger.astralsorcery.common.entities.EntityTechnicalAmbient;
import shordinger.astralsorcery.common.lib.Constellations;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.server.PktParticleEvent;
import shordinger.astralsorcery.common.tile.TileRitualPedestal;
import shordinger.astralsorcery.common.util.DamageSourceUtil;
import shordinger.astralsorcery.common.util.DamageUtil;
import shordinger.astralsorcery.common.util.ILocatable;
import shordinger.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.entity.EntityLiving;
import shordinger.wrapper.net.minecraft.entity.monster.IMob;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.MobEffects;
import shordinger.wrapper.net.minecraft.potion.PotionEffect;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.common.config.Configuration;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectOrion
 * Created by HellFirePvP
 * Date: 07.11.2016 / 22:30
 */
public class CEffectDiscidia extends CEffectEntityCollect<EntityLiving> {

    public static double potencyMultiplier = 1;
    public static float damage = 6.5F;

    public CEffectDiscidia(@Nullable ILocatable origin) {
        super(
            origin,
            Constellations.discidia,
            "discidia",
            16D,
            EntityLiving.class,
            (entity) -> !entity.isDead && !(entity instanceof EntityTechnicalAmbient) && entity instanceof IMob);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility,
                                 boolean extendedEffects) {
        EntityFXFacingParticle p = EffectHelper
            .genericFlareParticle(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        p.motion(
            (rand.nextFloat() * 0.1F) * (rand.nextBoolean() ? 1 : -1),
            (rand.nextFloat() * 0.1F) * (rand.nextBoolean() ? 1 : -1),
            (rand.nextFloat() * 0.1F) * (rand.nextBoolean() ? 1 : -1));
        p.scale(0.25F)
            .setColor(Color.DARK_GRAY);
    }

    @Override
    public boolean playEffect(World world, BlockPos pos, float percStrength, ConstellationEffectProperties modified,
                              @Nullable IMinorConstellation possibleTraitEffect) {
        if (world.getTotalWorldTime() % 20 != 0) return false;

        percStrength *= potencyMultiplier;
        if (percStrength < 1) {
            if (world.rand.nextFloat() > percStrength) return false;
        }
        boolean did = false;
        float actDamageDealt = percStrength * damage;
        List<EntityLiving> entities = collectEntities(world, pos, modified);
        if (!entities.isEmpty()) {
            EntityPlayer owner = getOwningPlayerInWorld(world, pos);
            DamageSource dmgSource = owner == null ? CommonProxy.dmgSourceStellar
                : DamageSourceUtil.withEntityDirect(CommonProxy.dmgSourceStellar, owner);
            if (modified.isCorrupted() && owner != null
                && owner.getDistanceSq(pos) <= (modified.getSize() * modified.getSize())) {
                DamageUtil.attackEntityFrom(owner, CommonProxy.dmgSourceStellar, 1.2F * percStrength);
                did = true;
            }
            for (EntityLiving entity : entities) {
                if (modified.isCorrupted()) {
                    entity.heal(actDamageDealt);
                    entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 30, 2));
                    did = true;
                } else {
                    int hrTime = entity.hurtResistantTime;
                    entity.hurtResistantTime = 0;
                    try {
                        if (DamageUtil.attackEntityFrom(entity, dmgSource, actDamageDealt)) {
                            PktParticleEvent ev = new PktParticleEvent(
                                PktParticleEvent.ParticleEventType.CE_DMG_ENTITY,
                                entity.posX,
                                entity.posY + entity.height / 2,
                                entity.posZ);
                            PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, pos, 16));
                        }
                    } finally {
                        entity.hurtResistantTime = hrTime;
                    }
                }
            }
        }
        return did;
    }

    @Override
    public ConstellationEffectProperties provideProperties(int mirrorCount) {
        return new ConstellationEffectProperties(this.range);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        super.loadFromConfig(cfg);

        damage = cfg.getFloat(
            getKey() + "DamageDealt",
            getConfigurationSection(),
            damage,
            0.1F,
            400F,
            "Defines the max. possible damage dealt per damage tick.");
        potencyMultiplier = cfg.getFloat(
            getKey() + "PotencyMultiplier",
            getConfigurationSection(),
            1.0F,
            0.01F,
            100F,
            "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");
    }

    @SideOnly(Side.CLIENT)
    public static void playParticles(PktParticleEvent event) {
        if (!Minecraft.isFancyGraphicsEnabled()) return;
        Vector3 pos = event.getVec();
        EntityFXFacingParticle p;
        for (int i = 0; i < 6; i++) {
            p = EffectHelper.genericFlareParticle(pos.getX(), pos.getY(), pos.getZ());
            p.motion(
                (rand.nextFloat() * 0.05F) * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.05F) * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.05F) * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.25F)
                .setColor(Color.RED);
        }
    }
}
