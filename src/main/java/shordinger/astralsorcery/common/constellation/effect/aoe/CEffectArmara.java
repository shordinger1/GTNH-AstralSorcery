/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.effect.aoe;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.effect.controller.orbital.OrbitalEffectController;
import shordinger.astralsorcery.client.effect.controller.orbital.OrbitalPropertiesRitualArmara;
import shordinger.astralsorcery.common.constellation.IMinorConstellation;
import shordinger.astralsorcery.common.constellation.effect.CEffectEntityCollect;
import shordinger.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import shordinger.astralsorcery.common.entities.EntityTechnicalAmbient;
import shordinger.astralsorcery.common.event.listener.EventHandlerEntity;
import shordinger.astralsorcery.common.item.crystal.base.ItemTunedCrystalBase;
import shordinger.astralsorcery.common.lib.Constellations;
import shordinger.astralsorcery.common.registry.RegistryPotions;
import shordinger.astralsorcery.common.tile.TileRitualPedestal;
import shordinger.astralsorcery.common.util.ILocatable;
import shordinger.astralsorcery.common.util.data.TickTokenizedMap;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.common.util.data.WorldBlockPos;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.MathHelper;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectArmara
 * Created by HellFirePvP
 * Date: 03.01.2017 / 14:49
 */
public class CEffectArmara extends CEffectEntityCollect<EntityMob> {

    public static boolean enabled = true;
    public static double potencyMultiplier = 1;
    public static int protectionRange = 32;

    private int rememberedTimeout = 0;
    public static int potionAmplifier = 0;

    public CEffectArmara(@Nullable ILocatable origin) {
        super(
            origin,
            Constellations.armara,
            "armara",
            protectionRange,
            EntityMob.class,
            (entity) -> !entity.isDead && !(entity instanceof EntityTechnicalAmbient));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility,
                                 boolean extendedEffects) {
        if (pedestal.getTicksExisted() % 20 == 0) {
            OrbitalPropertiesRitualArmara luc = new OrbitalPropertiesRitualArmara();
            OrbitalEffectController ctrl = EffectHandler.getInstance()
                .orbital(luc, luc, luc);
            ctrl.setOffset(new Vector3(pos).add(0.5, 0.5, 0.5));
            ctrl.setOrbitRadius(0.8 + rand.nextFloat() * 0.7);
            ctrl.setOrbitAxis(Vector3.RotAxis.Y_AXIS);
            ctrl.setTicksPerRotation(20 + rand.nextInt(20));
        }
        ItemStack socket = pedestal.getCatalystCache();
        if (!socket.isEmpty() && socket.getItem() instanceof ItemTunedCrystalBase) {
            IMinorConstellation trait = ItemTunedCrystalBase.getTrait(socket);
            ConstellationEffectProperties prop = provideProperties(0);
            prop.modify(trait);
            if (prop.isCorrupted()) {
                return;
            }
        }
        List projectiles = world.getEntitiesWithinAABB(
            Entity.class,
            new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(pos)
                .grow(protectionRange));
        if (!projectiles.isEmpty()) {
            for (Entity e : projectiles) {
                if (!e.isDead && !(e instanceof EntityTechnicalAmbient)) {
                    if (e instanceof IProjectile) {
                        double xRatio = (pos.getX() + 0.5) - e.posX;
                        double zRatio = (pos.getZ() + 0.5) - e.posZ;
                        float f = MathHelper.sqrt(xRatio * xRatio + zRatio * zRatio);
                        e.motionX /= 2.0D;
                        e.motionZ /= 2.0D;
                        e.motionX -= xRatio / f * 0.4;
                        e.motionZ -= zRatio / f * 0.4;
                        ((IProjectile) e).shoot(e.motionX, e.motionY, e.motionZ, 1F, 0F);
                    }
                }
            }
        }
    }

    @Override
    public boolean playEffect(World world, BlockPos pos, float percStrength, ConstellationEffectProperties modified,
                              @Nullable IMinorConstellation possibleTraitEffect) {
        if (!enabled) return false;
        percStrength *= potencyMultiplier;
        if (percStrength < 1) {
            if (world.rand.nextFloat() > percStrength) return false;
        }

        int toAdd = 1 + rand.nextInt(3);
        WorldBlockPos at = new WorldBlockPos(world, pos);
        TickTokenizedMap.SimpleTickToken<Double> token = EventHandlerEntity.spawnDenyRegions.get(at);
        if (token != null) {
            int next = token.getRemainingTimeout() + toAdd;
            if (next > 400) next = 400;
            token.setTimeout(next);
            rememberedTimeout = next;
        } else {
            rememberedTimeout = Math.min(400, rememberedTimeout + toAdd);
            EventHandlerEntity.spawnDenyRegions
                .put(at, new TickTokenizedMap.SimpleTickToken<>(modified.getSize(), rememberedTimeout));
        }

        EntityPlayer owner = getOwningPlayerInWorld(world, pos);

        boolean foundEntity = false;
        if (!modified.isCorrupted()) {
            List<Entity> projectiles = world.getEntitiesWithinAABB(
                Entity.class,
                new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(pos)
                    .grow(modified.getSize()));
            if (!projectiles.isEmpty()) {
                for (Entity e : projectiles) {
                    if (!e.isDead && !(e instanceof EntityTechnicalAmbient)) {
                        if (e instanceof IProjectile) {
                            double xRatio = (pos.getX() + 0.5) - e.posX;
                            double zRatio = (pos.getZ() + 0.5) - e.posZ;
                            float f = MathHelper.sqrt(xRatio * xRatio + zRatio * zRatio);
                            e.motionX /= 2.0D;
                            e.motionZ /= 2.0D;
                            e.motionX -= xRatio / f * 0.4;
                            e.motionZ -= zRatio / f * 0.4;
                            ((IProjectile) e).shoot(e.motionX, e.motionY, e.motionZ, 1F, 0F);
                        } else if (e instanceof EntityMob) {
                            ((EntityLivingBase) e).knockBack(
                                owner == null ? e : owner,
                                0.4F,
                                (pos.getX() + 0.5) - e.posX,
                                (pos.getZ() + 0.5) - e.posZ);
                        }
                        foundEntity = true;
                    }
                }
            }
        }
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(
            EntityLivingBase.class,
            new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(pos)
                .grow(modified.getSize()));
        for (EntityLivingBase entity : entities) {
            if (!entity.isDead && (entity instanceof EntityMob || entity instanceof EntityPlayer)) {
                if (modified.isCorrupted()) {
                    if (entity instanceof EntityPlayer) continue;

                    entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100, potionAmplifier + 4));
                    entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, potionAmplifier + 4));
                    entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 100, potionAmplifier + 2));
                    entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100, potionAmplifier + 4));
                    entity.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 100, potionAmplifier + 4));
                    entity.addPotionEffect(new PotionEffect(MobEffects.HASTE, 100, potionAmplifier + 4));
                    entity.addPotionEffect(new PotionEffect(RegistryPotions.potionDropModifier, 40000, 6));
                } else {
                    entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 30, potionAmplifier));
                    if (entity instanceof EntityPlayer) {
                        entity.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 30, potionAmplifier));
                    }
                }
                foundEntity = true;
            }
        }

        return foundEntity;
    }

    @Override
    public ConstellationEffectProperties provideProperties(int mirrorCount) {
        return new ConstellationEffectProperties(CEffectArmara.protectionRange);
    }

    @Override
    public void readFromNBT(NBTTagCompound cmp) {
        super.readFromNBT(cmp);

        this.rememberedTimeout = cmp.getInteger("rememberedTimeout");
    }

    @Override
    public void writeToNBT(NBTTagCompound cmp) {
        super.writeToNBT(cmp);

        cmp.setInteger("rememberedTimeout", rememberedTimeout);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        protectionRange = cfg.getInt(
            getKey() + "Range",
            getConfigurationSection(),
            32,
            1,
            128,
            "Defines the radius (in blocks) in which the ritual will stop mob spawning and projectiles.");
        enabled = cfg.getBoolean(
            getKey() + "Enabled",
            getConfigurationSection(),
            true,
            "Set to false to disable this ConstellationEffect.");
        potionAmplifier = cfg.getInt(
            getKey() + "ResistanceAmplifier",
            getConfigurationSection(),
            0,
            0,
            Short.MAX_VALUE,
            "Set the amplifier for the resistance potion effect.");
        potencyMultiplier = cfg.getFloat(
            getKey() + "PotencyMultiplier",
            getConfigurationSection(),
            1.0F,
            0.01F,
            100F,
            "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");
    }

}
