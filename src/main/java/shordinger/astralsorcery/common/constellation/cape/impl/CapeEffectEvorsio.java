/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.cape.impl;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.EntityComplexFX;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.client.util.RenderingUtils;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.constellation.cape.CapeArmorEffect;
import shordinger.astralsorcery.common.lib.Constellations;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.server.PktParticleEvent;
import shordinger.astralsorcery.common.util.DamageUtil;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.particle.ParticleManager;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.EnumCreatureType;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.math.AxisAlignedBB;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.common.config.Configuration;

import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeEffectEvorsio
 * Created by HellFirePvP
 * Date: 14.10.2017 / 17:07
 */
public class CapeEffectEvorsio extends CapeArmorEffect {

    private static float percDamageAppliedNearby = 0.5F;
    private static float rangeDeathAOE = 4F;

    public CapeEffectEvorsio(NBTTagCompound cmp) {
        super(cmp, "evorsio");
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.evorsio;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playActiveParticleTick(EntityPlayer pl) {
        playConstellationCapeSparkles(pl, 0.15F);
    }

    @SideOnly(Side.CLIENT)
    public static void playBlockBreakParticles(PktParticleEvent event) {
        Vector3 at = event.getVec();
        if (!Minecraft.getMinecraft().thePlayer.isCreative() && event.getAdditionalDataLong() != 0) {
            int stateId = (int) event.getAdditionalDataLong();
            IBlockState state = Block.getStateById(stateId);
            if (state != Blocks.AIR.getDefaultState()) {
                ParticleManager pm = Minecraft.getMinecraft().effectRenderer;
                World world = Minecraft.getMinecraft().theWorld;
                try {
                    if (!state.getBlock()
                            .addDestroyEffects(world, at.toBlockPos(), pm)) {
                        RenderingUtils.playBlockBreakParticles(at.toBlockPos(), state);
                    }
                } catch (Exception ignored) {
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            double x = at.getX() + rand.nextFloat();
            double y = at.getY() + rand.nextFloat();
            double z = at.getZ() + rand.nextFloat();
            float scale = rand.nextFloat() * 0.2F + 0.3F;
            float mX = rand.nextFloat() * 0.01F * (rand.nextBoolean() ? 1 : -1);
            float mY = rand.nextFloat() * 0.01F * (rand.nextBoolean() ? 1 : -1);
            float mZ = rand.nextFloat() * 0.01F * (rand.nextBoolean() ? 1 : -1);

            Color c = Constellations.evorsio.getConstellationColor();
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(x, y, z);
            p.setColor(c)
                    .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.scale(scale);
            if (rand.nextInt(6) == 0) {
                p.setColor(IConstellation.weak);
            }
            p.setMaxAge(30 + rand.nextInt(10));
            p.motion(mX, mY, mZ);

            if (rand.nextFloat() < 0.4F) {
                p = EffectHelper.genericFlareParticle(x, y, z);
                p.setColor(Color.WHITE)
                        .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                p.scale(scale * 0.35F);
                p.setMaxAge(20 + rand.nextInt(10));
                p.motion(mX, mY, mZ);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void playAreaDamageParticles(PktParticleEvent event) {
        Vector3 offset = event.getVec();
        Color c = Constellations.evorsio.getConstellationColor();
        EntityFXFacingParticle p;
        for (int i = 0; i < 45; i++) {
            Vector3 dir = Vector3.random();
            dir.setY(dir.getY() * 0.2F)
                    .normalize()
                    .multiply(rangeDeathAOE / 2F);
            Vector3 off = dir.clone()
                    .multiply(0.1F + rand.nextFloat() * 0.4F);
            Vector3 mov = dir.clone()
                    .multiply(0.01F);
            float scale = rand.nextFloat() * 0.2F + 0.2F;

            p = EffectHelper.genericFlareParticle(
                    offset.getX() + off.getX(),
                    offset.getY() + off.getY(),
                    offset.getZ() + off.getZ());
            p.setColor(c)
                    .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.scale(scale);
            p.setMaxAge(30 + rand.nextInt(10));
            p.motion(mov.getX(), mov.getY(), mov.getZ());

            if (rand.nextFloat() < 0.4F) {
                p = EffectHelper.genericFlareParticle(
                        offset.getX() + off.getX(),
                        offset.getY() + off.getY(),
                        offset.getZ() + off.getZ());
                p.setColor(Color.WHITE)
                        .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                p.scale(scale * 0.35F);
                p.setMaxAge(20 + rand.nextInt(10));
                p.motion(mov.getX(), mov.getY(), mov.getZ());
            }
        }
        for (int i = 0; i < 65; i++) {
            Vector3 dir = Vector3.random();
            dir.setY(dir.getY() * 0.3F)
                    .normalize()
                    .multiply(rangeDeathAOE / 2F);
            Vector3 off = dir.clone()
                    .multiply(0.5F + rand.nextFloat() * 0.4F);
            Vector3 mov = dir.clone()
                    .multiply(0.015F);
            float scale = rand.nextFloat() * 0.2F + 0.2F;

            p = EffectHelper.genericFlareParticle(
                    offset.getX() + off.getX(),
                    offset.getY() + off.getY(),
                    offset.getZ() + off.getZ());
            p.setColor(IConstellation.weak)
                    .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.scale(scale);
            p.setMaxAge(30 + rand.nextInt(10));
            p.motion(mov.getX(), mov.getY(), mov.getZ());

            if (rand.nextFloat() < 0.4F) {
                p = EffectHelper.genericFlareParticle(
                        offset.getX() + off.getX(),
                        offset.getY() + off.getY(),
                        offset.getZ() + off.getZ());
                p.setColor(Color.WHITE)
                        .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                p.scale(scale * 0.35F);
                p.setMaxAge(20 + rand.nextInt(10));
                p.motion(mov.getX(), mov.getY(), mov.getZ());
            }
        }
    }

    public void deathAreaDamage(DamageSource ds, EntityLivingBase entityLiving) {
        if (percDamageAppliedNearby > 0) {
            float damage = entityLiving.getMaxHealth() * percDamageAppliedNearby;

            float r = rangeDeathAOE;
            List<EntityLivingBase> eList = entityLiving.world.getEntitiesWithinAABB(
                    EntityLivingBase.class,
                    new AxisAlignedBB(-r, -r, -r, r, r, r).offset(entityLiving.getPosition()),
                    e -> e != null && !e.isDead && e.getHealth() > 0 && e.isCreatureType(EnumCreatureType.MONSTER, false));
            for (EntityLivingBase el : eList) {
                int preTime = el.hurtResistantTime;
                el.hurtResistantTime = 0;
                DamageUtil.attackEntityFrom(el, ds, damage);
                el.hurtResistantTime = Math.max(preTime, el.hurtResistantTime);
            }

            PktParticleEvent ev = new PktParticleEvent(
                    PktParticleEvent.ParticleEventType.CAPE_EVORSIO_AOE,
                    Vector3.atEntityCenter(entityLiving));
            PacketChannel.CHANNEL
                    .sendToAllAround(ev, PacketChannel.pointFromPos(entityLiving.world, entityLiving.getPosition(), 16));
        }
    }

    public void breakBlocksPlaneVertical(EntityPlayerMP player, EnumFacing sideBroken, World world, BlockPos at) {
        for (int xx = -2; xx <= 2; xx++) {
            if (sideBroken.getDirectionVec()
                    .getX() != 0 && xx != 0) continue;
            for (int yy = -1; yy <= 3; yy++) {
                if (sideBroken.getDirectionVec()
                        .getY() != 0 && yy != 0) continue;
                for (int zz = -2; zz <= 2; zz++) {
                    if (sideBroken.getDirectionVec()
                            .getZ() != 0 && zz != 0) continue;
                    BlockPos other = at.add(xx, yy, zz);
                    if (world.getTileEntity(other) == null && world.getBlockState(other)
                            .getBlockHardness(world, other) != -1) {
                        IBlockState present = world.getBlockState(other);
                        if (MiscUtils.breakBlockWithPlayer(other, player)) {
                            PktParticleEvent ev = new PktParticleEvent(
                                    PktParticleEvent.ParticleEventType.CAPE_EVORSIO_BREAK,
                                    other);
                            ev.setAdditionalDataLong(Block.getStateId(present));
                            PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, other, 16));
                        }
                    }
                }
            }
        }
    }

    public void breakBlocksPlaneHorizontal(EntityPlayerMP player, EnumFacing sideBroken, World world, BlockPos at) {
        for (int xx = -2; xx <= 2; xx++) {
            if (sideBroken.getDirectionVec()
                    .getX() != 0 && xx != 0) continue;
            for (int zz = -2; zz <= 2; zz++) {
                if (sideBroken.getDirectionVec()
                        .getZ() != 0 && zz != 0) continue;
                BlockPos other = at.add(xx, 0, zz);
                if (world.getTileEntity(other) == null && world.getBlockState(other)
                        .getBlockHardness(world, other) != -1) {
                    IBlockState present = world.getBlockState(other);
                    if (MiscUtils.breakBlockWithPlayer(other, player)) {
                        PktParticleEvent ev = new PktParticleEvent(
                                PktParticleEvent.ParticleEventType.CAPE_EVORSIO_BREAK,
                                other);
                        ev.setAdditionalDataLong(Block.getStateId(present));
                        PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, other, 16));
                    }
                }
            }
        }
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        percDamageAppliedNearby = cfg.getFloat(
                getKey() + "PercentLifeDamage",
                getConfigurationSection(),
                percDamageAppliedNearby,
                0,
                10,
                "Defines the multiplier how much of the dead entity's max-life should be dealt as AOE damage to mobs nearby.");
        rangeDeathAOE = cfg.getFloat(
                getKey() + "DeathAOERange",
                getConfigurationSection(),
                rangeDeathAOE,
                0.5F,
                50,
                "Defines the Range of the death-AOE effect of when a mob gets killed by a player with this cape on.");
    }
}
