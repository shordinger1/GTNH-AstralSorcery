/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.tree.nodes.key;

import com.google.common.collect.Lists;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.CommonProxy;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.data.config.entry.ConfigEntry;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.entities.EntityFlare;
import shordinger.astralsorcery.common.entities.EntityTechnicalAmbient;
import shordinger.astralsorcery.common.util.DamageUtil;
import shordinger.astralsorcery.common.util.EntityUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.math.AxisAlignedBB;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.common.config.Configuration;
import shordinger.wrapper.net.minecraftforge.event.entity.living.LivingHurtEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.EventPriority;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;

import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyLightningArc
 * Created by HellFirePvP
 * Date: 20.07.2018 / 22:22
 */
public class KeyLightningArc extends KeyPerk {

    private static boolean chainingDamage = false;

    private float arcChance = 0.6F;
    private float arcPercent = 0.75F;
    private int arcTicks = 3;

    private static float distanceSearch = 7F;
    private static int arcBaseChains = 3;

    public KeyLightningArc(String name, int x, int y) {
        super(name, x, y);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, name) {
            @Override
            public void loadFromConfig(Configuration cfg) {
                arcChance = cfg.getFloat("Chance", getConfigurationSection(), arcChance, 0F, 1F,
                        "Sets the chance to spawn a damage-arc effect when an enemy is hit (value is in percent)");
                arcPercent = cfg.getFloat("DamagePercent", getConfigurationSection(), arcPercent, 0.1F, 64F,
                        "Defines the damage-multiplier which gets added to the damage dealt initially.");
                distanceSearch = cfg.getFloat("Distance", getConfigurationSection(), distanceSearch, 0.2F, 16F,
                        "Defines the distance for how far a single arc can jump/search for nearby entities");
                arcTicks = cfg.getInt("DamageTicks", getConfigurationSection(), arcTicks, 1, 128,
                        "Defines the amount of times an arc will repetitively chain between the mobs and deal damage after initially spawned/triggered");
            }
        });
    }

    @Override
    protected void applyEffectMultiplier(double multiplier) {
        super.applyEffectMultiplier(multiplier);

        this.arcChance *= multiplier;
        this.arcPercent *= multiplier;
        this.arcTicks = MathHelper.ceil(this.arcTicks * multiplier);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onAttack(LivingHurtEvent event) {
        if (chainingDamage) return;

        DamageSource source = event.getSource();
        if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) source.getTrueSource();
            Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (side == Side.SERVER && prog.hasPerkEffect(this)) {
                float chance = PerkAttributeHelper.getOrCreateMap(player, side)
                        .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, arcChance);
                if (rand.nextFloat() < chance) {
                    float dmg = event.getAmount();
                    dmg = Math.max(MathHelper.sqrt(dmg), 1.5F);
                    new RepetitiveArcEffect(player.world, player, arcTicks, event.getEntityLiving().getEntityId(), dmg).fire();
                }
            }
        }
    }

    static class RepetitiveArcEffect {

        private World world;
        private EntityPlayer player;
        private int count;
        private int entityStartId;
        private float damage;

        public RepetitiveArcEffect(World world, EntityPlayer player, int count, int entityStartId, float damage) {
            this.world = world;
            this.player = player;
            this.count = count;
            this.entityStartId = entityStartId;
            this.damage = damage;
        }

        void fire() {
            if (player.isDead) {
                return;
            }

            Color c = new Color(0x0195FF);
            int chainTimes = Math.round(PerkAttributeHelper.getOrCreateMap(player, Side.SERVER)
                    .modifyValue(player, ResearchManager.getProgress(player, Side.SERVER), AttributeTypeRegistry.ATTR_TYPE_ARC_CHAINS, arcBaseChains));
            List<EntityLivingBase> visitedEntities = Lists.newArrayList();
            Entity start = world.getEntityByID(entityStartId);
            if (start != null && start instanceof EntityLivingBase && !start.isDead) {
                AxisAlignedBB box = new AxisAlignedBB(-distanceSearch, -distanceSearch, -distanceSearch,
                        distanceSearch, distanceSearch, distanceSearch);

                EntityLivingBase last = null;
                EntityLivingBase entity = (EntityLivingBase) start;
                while (entity != null && !entity.isDead && chainTimes > 0) {
                    visitedEntities.add(entity);
                    chainTimes--;

                    if (last != null) {
                        AstralSorcery.proxy.fireLightning(entity.getEntityWorld(), Vector3.atEntityCenter(last), Vector3.atEntityCenter(entity), c);
                        AstralSorcery.proxy.fireLightning(entity.getEntityWorld(), Vector3.atEntityCenter(entity), Vector3.atEntityCenter(last), c);
                    }
                    List<EntityLivingBase> entities = entity.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, box.offset(entity.getPositionVector()), EntityUtils.selectEntities(EntityLivingBase.class));
                    entities.remove(entity);
                    if (last != null) {
                        entities.remove(last);
                    }
                    if (player != null) {
                        entities.remove(player);
                    }
                    entities.removeAll(visitedEntities);
                    entities.removeIf(e -> e instanceof EntityTechnicalAmbient || e instanceof EntityFlare);
                    entities.removeIf(e -> !MiscUtils.canPlayerAttackServer(player, e));

                    if(!entities.isEmpty()) {
                        EntityLivingBase tmpEntity = entity; //Final for lambda
                        EntityLivingBase closest = EntityUtils.selectClosest(entities, (e) -> (double) e.getDistance(tmpEntity));
                        if(closest != null && !closest.isDead) {
                            last = entity;
                            entity = closest;
                        } else {
                            entity = null;
                        }
                    } else {
                        entity = null;
                    }
                }

                if (visitedEntities.size() > 1) {
                    visitedEntities.forEach((e) -> {
                        chainingDamage = true;
                        DamageUtil.attackEntityFrom(e, CommonProxy.dmgSourceStellar, damage, player);
                        chainingDamage = false;
                    });
                }
            }

            if (count > 0) {
                count--;
                AstralSorcery.proxy.scheduleDelayed(this::fire, 12);
            }
        }
    }
}
