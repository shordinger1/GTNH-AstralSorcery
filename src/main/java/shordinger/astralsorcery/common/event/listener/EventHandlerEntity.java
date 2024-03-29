/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.event.listener;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.EntityComplexFX;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.auxiliary.StarlightNetworkDebugHandler;
import shordinger.astralsorcery.common.auxiliary.SwordSharpenHelper;
import shordinger.astralsorcery.common.base.Mods;
import shordinger.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import shordinger.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.integrations.ModIntegrationDraconicEvolution;
import shordinger.astralsorcery.common.item.ItemBlockStorage;
import shordinger.astralsorcery.common.item.tool.wand.ItemWand;
import shordinger.astralsorcery.common.item.tool.wand.WandAugment;
import shordinger.astralsorcery.common.item.wearable.ItemCape;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.client.PktClearBlockStorageStack;
import shordinger.astralsorcery.common.network.packet.server.PktParticleEvent;
import shordinger.astralsorcery.common.registry.RegistryPotions;
import shordinger.astralsorcery.common.util.EntityUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.TickTokenizedMap;
import shordinger.astralsorcery.common.util.data.TimeoutList;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.common.util.data.WorldBlockPos;
import shordinger.wrapper.net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.entity.*;
import shordinger.wrapper.net.minecraft.entity.item.EntityItem;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityArrow;
import shordinger.wrapper.net.minecraft.init.MobEffects;
import shordinger.wrapper.net.minecraft.inventory.EntityEquipmentSlot;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.potion.PotionEffect;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.math.AxisAlignedBB;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.WorldServer;
import shordinger.wrapper.net.minecraft.world.storage.loot.LootContext;
import shordinger.wrapper.net.minecraft.world.storage.loot.LootTable;
import shordinger.wrapper.net.minecraftforge.event.entity.EntityJoinWorldEvent;
import shordinger.wrapper.net.minecraftforge.event.entity.living.*;
import shordinger.wrapper.net.minecraftforge.event.entity.player.PlayerInteractEvent;
import shordinger.wrapper.net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.Event;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.EventPriority;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerEntity
 * Created by HellFirePvP
 * Date: 01.08.2017 / 20:28
 */
public class EventHandlerEntity {

    private static final Random rand = new Random();
    private static final Color discidiaWandColor = new Color(0x880100);

    public static int spawnSkipId = -1;
    public static TickTokenizedMap<WorldBlockPos, TickTokenizedMap.SimpleTickToken<Double>> spawnDenyRegions = new TickTokenizedMap<>(
        TickEvent.Type.SERVER);
    public static TimeoutList<EntityPlayer> invulnerabilityCooldown = new TimeoutList<>(null, TickEvent.Type.SERVER);
    public static TimeoutList<EntityPlayer> ritualFlight = new TimeoutList<>(player -> {
        if (player instanceof EntityPlayerMP && ((EntityPlayerMP) player).interactionManager.getGameType()
            .isSurvivalOrAdventure()) {
            player.capabilities.allowFlying = false;
            player.capabilities.isFlying = false;
            player.sendPlayerAbilities();
        }
    }, TickEvent.Type.SERVER);
    public static Map<Integer, EntityAttackStack> attackStack = new HashMap<>();

    @SubscribeEvent
    public void onTarget(LivingSetAttackTargetEvent event) {
        EntityLivingBase living = event.getTarget();
        if (living != null && !living.isDead && living instanceof EntityPlayer) {
            if (invulnerabilityCooldown.contains((EntityPlayer) living)) {
                event.getEntityLiving()
                    .setRevengeTarget(null);
                if (event.getEntityLiving() instanceof EntityLiving) {
                    ((EntityLiving) event.getEntityLiving()).setAttackTarget(null);
                }
            }
        }
    }

    @SubscribeEvent
    public void onSleep(PlayerSleepInBedEvent event) {
        WorldSkyHandler wsh = ConstellationSkyHandler.getInstance()
            .getWorldHandler(
                event.getEntityPlayer()
                    .getEntityWorld());
        if (wsh != null && wsh.dayOfSolarEclipse && wsh.solarEclipse) {
            if (event.getResultStatus() == null) {
                event.setResult(EntityPlayer.SleepResult.NOT_POSSIBLE_NOW);
            }
        }
    }

    @SubscribeEvent
    public void onSpawnDropCloud(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityAreaEffectCloud && MiscUtils.iterativeSearch(
            ((EntityAreaEffectCloud) event.getEntity()).effects,
            (pEffect) -> pEffect.getPotion()
                .equals(RegistryPotions.potionDropModifier))
            != null) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onAttack(LivingHurtEvent event) {
        if (event.getEntity()
            .getEntityWorld().isRemote) return;

        DamageSource source = event.getSource();
        if (source.getTrueSource() != null) {
            EntityLivingBase entitySource = null;
            if (source.getTrueSource() instanceof EntityLivingBase) {
                entitySource = (EntityLivingBase) source.getTrueSource();
            } else if (source.getTrueSource() instanceof EntityArrow) {
                Entity shooter = ((EntityArrow) source.getTrueSource()).shootingEntity;
                if (shooter != null && shooter instanceof EntityLivingBase) {
                    entitySource = (EntityLivingBase) shooter;
                }
            }
            if (entitySource != null) {
                WandAugment foundAugment = null;
                ItemStack stack = entitySource.getHeldItemMainhand();
                if (!stack.isEmpty() && stack.getItem() instanceof ItemWand) {
                    foundAugment = ItemWand.getAugment(stack);
                }
                stack = entitySource.getHeldItemOffhand();
                if (foundAugment == null && !stack.isEmpty() && stack.getItem() instanceof ItemWand) {
                    foundAugment = ItemWand.getAugment(stack);
                }
                if (foundAugment != null && foundAugment.equals(WandAugment.DISCIDIA)) {
                    EntityAttackStack attack = attackStack.get(entitySource.getEntityId());
                    if (attack == null) {
                        attack = new EntityAttackStack();
                        attackStack.put(entitySource.getEntityId(), attack);
                    }
                    EntityLivingBase entity = event.getEntityLiving();
                    float multiplier = attack.getAndUpdateMultipler(entity);
                    event.setAmount(event.getAmount() * (1F + multiplier));
                    PktParticleEvent ev = new PktParticleEvent(
                        PktParticleEvent.ParticleEventType.DISCIDIA_ATTACK_STACK,
                        entity.posX,
                        entity.posY,
                        entity.posZ);
                    ev.setAdditionalData(multiplier);
                    PacketChannel.CHANNEL.sendToAllAround(
                        ev,
                        PacketChannel.pointFromPos(
                            event.getEntity().world,
                            event.getEntity()
                                .getPosition(),
                            64));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDeathInform(LivingDeathEvent event) {
        attackStack.remove(
            event.getEntity()
                .getEntityId());
    }

    @SubscribeEvent
    public void onLivingDestroyBlock(LivingDestroyBlockEvent event) {
        if (event.getEntityLiving()
            .isPotionActive(RegistryPotions.potionTimeFreeze)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onDrops(LivingDropsEvent event) {
        if (event.getEntityLiving().world == null || event.getEntityLiving().world.isRemote
            || !(event.getEntityLiving().world instanceof WorldServer)) {
            return;
        }
        if (event.getEntityLiving() instanceof EntityPlayer || !(event.getEntityLiving() instanceof EntityLiving))
            return;
        EntityLiving el = (EntityLiving) event.getEntityLiving();
        WorldServer ws = (WorldServer) el.world;

        PotionEffect pe = el.getActivePotionEffect(RegistryPotions.potionDropModifier);
        if (pe != null) {
            el.removeActivePotionEffect(RegistryPotions.potionDropModifier);
            int ampl = pe.getAmplifier();
            if (ampl == 0) {
                event.getDrops()
                    .clear();
            } else {
                LootTable lootTableRef = EntityUtils.getLootTable(el);
                LootContext.Builder builder = new LootContext.Builder(ws).withLootedEntity(el)
                    .withDamageSource(event.getSource())
                    .withLuck(0);
                if (lootTableRef != null) {
                    for (int i = 0; i < ampl; i++) {
                        for (ItemStack stack : lootTableRef.generateLootForPools(rand, builder.build())) {
                            if (stack.isEmpty()) continue;

                            EntityItem ei = new EntityItem(ws, el.posX, el.posY, el.posZ, stack);
                            ei.setDefaultPickupDelay();
                            event.getDrops()
                                .add(ei);
                        }
                    }
                }
            }
        }
    }

    // Just... do the clear.
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        ItemStack held = event.getItemStack();
        if (!event.getWorld().isRemote && !held.isEmpty() && held.getItem() instanceof ItemBlockStorage) {
            ItemBlockStorage.tryClearContainerFor(event.getEntityPlayer());
        }
    }

    // Send clear to server
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLeftClickAir(PlayerInteractEvent.LeftClickEmpty event) {
        ItemStack held = event.getItemStack();
        if (!held.isEmpty() && held.getItem() instanceof ItemBlockStorage) {
            PacketChannel.CHANNEL.sendToServer(new PktClearBlockStorageStack());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRightClickDebug(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntityPlayer()
            .isCreative() && !event.getWorld().isRemote) {
            if (StarlightNetworkDebugHandler.INSTANCE
                .beginDebugFor(event.getWorld(), event.getPos(), event.getEntityPlayer())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDamage(LivingHurtEvent event) {
        EntityLivingBase living = event.getEntityLiving();
        if (living == null || living.getEntityWorld().isRemote) return;

        if (!living.isDead && living instanceof EntityPlayer) {
            if (invulnerabilityCooldown.contains((EntityPlayer) living)) {
                event.setCanceled(true);
                return;
            }
        }

        DamageSource source = event.getSource();
        if (Mods.DRACONICEVOLUTION.isPresent()) {
            ItemStack chest = living.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            if (!chest.isEmpty() && chest.getItem() instanceof ItemCape
                && ModIntegrationDraconicEvolution.isChaosDamage(source)) {
                if (living instanceof EntityPlayer && ((EntityPlayer) living).isCreative()) {
                    event.setCanceled(true);
                    return;
                }
                event.setAmount(event.getAmount() * (1F - Config.capeChaosResistance));
                if (event.getAmount() <= 1E-4) {
                    event.setCanceled(true);
                    return;
                }
            }
        }

        lblIn:
        if (source.getTrueSource() != null) {
            EntityPlayer p;
            if (source.getTrueSource() instanceof EntityPlayer) {
                p = (EntityPlayer) source.getTrueSource();
            } else if (source.getTrueSource() instanceof EntityArrow) {
                Entity shooter = ((EntityArrow) source.getTrueSource()).shootingEntity;
                if (shooter != null && shooter instanceof EntityPlayer) {
                    p = (EntityPlayer) shooter;
                } else {
                    break lblIn;
                }
            } else {
                break lblIn;
            }
            ItemStack held = p.getHeldItemMainhand();
            if (SwordSharpenHelper.isSwordSharpened(held)) {
                // YEEEAAAA i know this flat multiplies all damage.. but w/e..
                // There's no great way to test for item here.
                event.setAmount(event.getAmount() * (1 + ((float) Config.swordSharpMultiplier)));
            }
        }
        EntityLivingBase entity = event.getEntityLiving();
        if (entity != null) {
            ItemStack active = entity.getActiveItemStack();
            if (!active.isEmpty() && active.getItem() instanceof ItemWand) {
                WandAugment wa = ItemWand.getAugment(active);
                if (wa != null && wa.equals(WandAugment.ARMARA)) {
                    PotionEffect potion = new PotionEffect(MobEffects.RESISTANCE, 100, 0);
                    if (entity.isPotionApplicable(potion)) {
                        entity.addPotionEffect(potion);
                    }
                    potion = new PotionEffect(MobEffects.ABSORPTION, 100, 1);
                    if (entity.isPotionApplicable(potion)) {
                        entity.addPotionEffect(potion);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onSpawnTest(LivingSpawnEvent.CheckSpawn event) {
        if (event.getResult() == Event.Result.DENY) return; // Already denied anyway.
        if (event.getWorld().isRemote) return;
        if (event.isSpawner()) return; // FINE, i'll allow spawners.

        EntityLivingBase toTest = event.getEntityLiving();
        if (spawnSkipId != -1 && toTest.getEntityId() == spawnSkipId) {
            return;
        }

        Vector3 at = Vector3.atEntityCorner(toTest);
        boolean mayDeny = Config.doesMobSpawnDenyDenyEverything
            || toTest.isCreatureType(EnumCreatureType.MONSTER, false);
        if (mayDeny) {
            for (Map.Entry<WorldBlockPos, TickTokenizedMap.SimpleTickToken<Double>> entry : spawnDenyRegions
                .entrySet()) {
                if (!entry.getKey()
                    .getWorld()
                    .equals(toTest.getEntityWorld())) continue;
                if (at.distance(entry.getKey()) <= entry.getValue()
                    .getValue()) {
                    event.setResult(Event.Result.DENY);
                    return;
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void playDiscidiaStackAttackEffects(PktParticleEvent pkt) {
        Vector3 at = pkt.getVec();
        World w = Minecraft.getMinecraft().theWorld;
        EntityLivingBase found = null;
        if (w != null) {
            EntityLivingBase e = EntityUtils.selectClosest(
                w.getEntitiesWithinAABB(
                    EntityLivingBase.class,
                    Block.FULL_BLOCK_AABB.offset(at.getX() - 0.5, at.getY() - 0.5, at.getZ() - 0.5)),
                (ent) -> ent.getDistance(at.getX(), at.getY(), at.getZ()));
            if (e != null) {
                found = e;
            }
        }
        if (found != null) {
            AxisAlignedBB box = found.getEntityBoundingBox();
            for (int i = 0; i < 24; i++) {
                if (rand.nextFloat() < pkt.getAdditionalData()) {
                    Vector3 pos = new Vector3(
                        box.minX + ((box.maxX - box.minX) * rand.nextFloat()),
                        box.minY + ((box.maxY - box.minY) * rand.nextFloat()),
                        box.minZ + ((box.maxZ - box.minZ) * rand.nextFloat()));
                    EntityFXFacingParticle p = EffectHelper.genericFlareParticle(pos.getX(), pos.getY(), pos.getZ());
                    p.setColor(discidiaWandColor)
                        .setMaxAge(25 + rand.nextInt(10));
                    p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
                        .setAlphaMultiplier(1F);
                    p.gravity(0.004)
                        .scale(0.15F + rand.nextFloat() * 0.1F);
                    Vector3 motion = new Vector3();
                    MiscUtils.applyRandomOffset(motion, rand, 0.03F);
                    p.motion(motion.getX(), motion.getY(), motion.getZ());
                }
            }
        }
    }

    private static class EntityAttackStack {

        private static long stackMsDuration = 5000;

        private int entityStackId = -1;
        private long lastStackMs = 0;
        private int stack = 0;

        public float getMultiplier(Entity attackedEntity) {
            return getMultiplier(attackedEntity.getEntityId());
        }

        public float getMultiplier(int attackedEntityId) {
            if (entityStackId != attackedEntityId) {
                return 0F;
            }
            return (((float) stack) / ((float) Config.discidiaStackCap)) * Config.discidiaStackMultiplier;
        }

        public float getAndUpdateMultipler(Entity attackedEntity) {
            return getAndUpdateMultipler(attackedEntity.getEntityId());
        }

        public float getAndUpdateMultipler(int attackedEntityId) {
            if (attackedEntityId != entityStackId) {
                entityStackId = attackedEntityId;
                lastStackMs = System.currentTimeMillis();
                stack = 0;
            } else {
                long current = System.currentTimeMillis();
                long diff = current - lastStackMs;
                lastStackMs = current;
                if (diff < stackMsDuration) {
                    stack = MathHelper.clamp(stack + 1, 0, Config.discidiaStackCap);
                } else {
                    stack = MathHelper.clamp(stack - ((int) (diff / stackMsDuration)), 0, Config.discidiaStackCap);
                }
            }
            return (((float) stack) / ((float) Config.discidiaStackCap)) * Config.discidiaStackMultiplier;
        }

    }

}
