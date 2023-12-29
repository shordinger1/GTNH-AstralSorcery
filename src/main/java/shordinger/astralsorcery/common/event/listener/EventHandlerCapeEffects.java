/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.event.listener;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import shordinger.astralsorcery.common.CommonProxy;
import shordinger.astralsorcery.common.auxiliary.tick.ITickHandler;
import shordinger.astralsorcery.common.base.Plants;
import shordinger.astralsorcery.common.constellation.cape.CapeArmorEffect;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectAevitas;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectArmara;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectBootes;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectDiscidia;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectEvorsio;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectFornax;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectHorologium;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectLucerna;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectMineralis;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectOctans;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectPelotrio;
import shordinger.astralsorcery.common.constellation.cape.impl.CapeEffectVicio;
import shordinger.astralsorcery.common.constellation.perk.tree.nodes.key.KeyMantleFlight;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.entities.EntitySpectralTool;
import shordinger.astralsorcery.common.item.wearable.ItemCape;
import shordinger.astralsorcery.common.lib.Constellations;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.client.PktElytraCapeState;
import shordinger.astralsorcery.common.network.packet.server.PktParticleEvent;
import shordinger.astralsorcery.common.util.CropHelper;
import shordinger.astralsorcery.common.util.DamageUtil;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.core.ASMCallHook;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerCapeEffects
 * Created by HellFirePvP
 * Date: 10.10.2017 / 00:34
 */
public class EventHandlerCapeEffects implements ITickHandler {

    private static final Random rand = new Random();
    public static EventHandlerCapeEffects INSTANCE = new EventHandlerCapeEffects();

    private static final List<UUID> vicioMantleFlightPlayers = Lists.newArrayList();

    // Propagate player in tick for octans anti-knockback effect.
    public static EntityPlayer currentPlayerInTick = null;

    // Prevent event overflow
    private static boolean discidiaChainingAttack = false;
    private static boolean evorsioChainingBreak = false;

    // To propagate elytra states
    private static boolean updateElytraBuffer = false;
    public static boolean inElytraCheck = false;

    private EventHandlerCapeEffects() {
    }

    @SubscribeEvent
    public void breakBlock(BlockEvent.BreakEvent event) {
        if (event.world.isRemote) return;
        if (evorsioChainingBreak) return;

        EntityPlayer pl = event.getPlayer();
        if (!(pl instanceof EntityPlayerMP)) return;
        if (MiscUtils.isPlayerFakeMP((EntityPlayerMP) pl)) return;

        IBlockState state = event.getState();
        ItemStack held = pl.getHeldItemMainhand();

        CapeEffectPelotrio pel = ItemCape.getCapeEffect(pl, Constellations.pelotrio);
        if (pel != null) {
            if (("pickaxe".equalsIgnoreCase(
                state.getBlock()
                    .getHarvestTool(state))
                || (!state.getMaterial()
                .isToolNotRequired() && Items.DIAMOND_PICKAXE.canHarvestBlock(state)))
                && !pl.getHeldItemMainhand()
                .isEmpty()
                && pl.getHeldItemMainhand()
                .getItem()
                .getToolClasses(held)
                .contains("pickaxe")) {
                if (rand.nextFloat() < pel.getChanceSpawnPick()) {
                    BlockPos at = pl.getPosition()
                        .up();
                    EntitySpectralTool esp = new EntitySpectralTool(
                        event.world,
                        at,
                        new ItemStack(Items.DIAMOND_PICKAXE),
                        EntitySpectralTool.ToolTask.createPickaxeTask());
                    event.world.spawnEntity(esp);
                    return;
                }
            }
            if ((state.getBlock()
                .isWood(event.world, event.getPos())
                || state.getBlock()
                .isLeaves(state, event.world, event.getPos()))
                && !pl.getHeldItemMainhand()
                .isEmpty()
                && pl.getHeldItemMainhand()
                .getItem()
                .getToolClasses(held)
                .contains("axe")) {
                if (rand.nextFloat() < pel.getChanceSpawnAxe()) {
                    BlockPos at = pl.getPosition()
                        .up();
                    EntitySpectralTool esp = new EntitySpectralTool(
                        event.world,
                        at,
                        new ItemStack(Items.DIAMOND_AXE),
                        EntitySpectralTool.ToolTask.createLogTask());
                    event.world.spawnEntity(esp);
                }
            }
        }
        CapeEffectEvorsio ev = ItemCape.getCapeEffect(pl, Constellations.evorsio);
        if (ev != null && !pl.getHeldItemMainhand()
            .isEmpty()
            && !pl.getHeldItemMainhand()
            .getItem()
            .getToolClasses(pl.getHeldItemMainhand())
            .isEmpty()
            && !pl.isSneaking()) {
            evorsioChainingBreak = true;
            try {
                RayTraceResult rtr = MiscUtils.rayTraceLook(pl);
                if (rtr != null) {
                    EnumFacing faceHit = rtr.sideHit;
                    if (faceHit != null) {
                        if (faceHit.getAxis() == EnumFacing.Axis.Y) {
                            ev.breakBlocksPlaneHorizontal((EntityPlayerMP) pl, faceHit, event.world, event.getPos());
                        } else {
                            ev.breakBlocksPlaneVertical((EntityPlayerMP) pl, faceHit, event.world, event.getPos());
                        }
                    }
                }
            } finally {
                evorsioChainingBreak = false;
            }
        }
    }

    @SubscribeEvent
    public void playerUpdatePre(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            currentPlayerInTick = event.player;
        } else {
            currentPlayerInTick = null;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onHurt(LivingHurtEvent event) {
        if (event.entityLiving.worldObj.isRemote) return;

        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer pl = (EntityPlayer) event.entityLiving;
            CapeEffectDiscidia cd = ItemCape.getCapeEffect(pl, Constellations.discidia);
            if (cd != null) {
                cd.writeLastAttackDamage(event.ammount);
            }
            CapeEffectArmara ca = ItemCape.getCapeEffect(pl, Constellations.armara);
            if (ca != null) {
                if (ca.shouldPreventDamage(event.source, false)) {
                    event.setCanceled(true);
                    return;
                }
            }
            CapeEffectBootes bo = ItemCape.getCapeEffect(pl, Constellations.bootes);
            if (bo != null && event.source.getTrueSource() != null) {
                Entity source = event.source.getTrueSource();
                if (source instanceof EntityLivingBase) {
                    bo.onPlayerDamagedByEntity(pl, (EntityLivingBase) source);
                }
            }
            if (event.source.isFireDamage()) {
                CapeEffectFornax cf = ItemCape.getCapeEffect(pl, Constellations.fornax);
                if (cf != null) {
                    cf.healFor(pl, event.ammount);
                    float mul = cf.getDamageMultiplier();
                    if (mul <= 0) {
                        event.setCanceled(true);
                        return;
                    } else {
                        event.setAmount(event.ammount * mul);
                    }
                }
            } else {
                CapeEffectHorologium horo = ItemCape.getCapeEffect(pl, Constellations.horologium);
                if (horo != null) {
                    horo.onHurt(pl);
                }
            }
        }
    }

    @SubscribeEvent
    public void onKill(LivingDeathEvent event) {
        if (event.entity.worldObj.isRemote) return;

        DamageSource ds = event.source;
        if (ds.getTrueSource() != null && ds.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer pl = (EntityPlayer) ds.getTrueSource();
            if (!(pl instanceof EntityPlayerMP)) return;
            if (MiscUtils.isPlayerFakeMP((EntityPlayerMP) pl)) return;

            CapeEffectEvorsio ev = ItemCape.getCapeEffect(pl, Constellations.evorsio);
            if (ev != null) {
                ev.deathAreaDamage(ds, event.entityLiving);
            }
        }
    }

    @SubscribeEvent
    public void onAttack(LivingAttackEvent event) {
        if (discidiaChainingAttack) return;
        if (event.entityLiving.world.isRemote) return;

        DamageSource ds = event.source;
        if (ds.getTrueSource() != null && ds.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer attacker = (EntityPlayer) ds.getTrueSource();
            if (!(attacker instanceof EntityPlayerMP)) return;
            if (MiscUtils.isPlayerFakeMP((EntityPlayerMP) attacker)) return;

            CapeEffectDiscidia cd = ItemCape.getCapeEffect(attacker, Constellations.discidia);
            if (cd != null) {
                double added = cd.getLastAttackDamage();

                discidiaChainingAttack = true;
                try {
                    DamageUtil
                        .attackEntityFrom(event.entityLiving, CommonProxy.dmgSourceStellar, (float) (added / 2.0F));
                    DamageUtil.attackEntityFrom(
                        event.entityLiving,
                        DamageSource.causePlayerDamage(attacker),
                        (float) (added / 2.0F));
                } finally {
                    discidiaChainingAttack = false;
                }
            }
            CapeEffectPelotrio pel = ItemCape.getCapeEffect(attacker, Constellations.pelotrio);
            if (pel != null && !attacker.getHeldItemMainhand()
                .isEmpty() && rand.nextFloat() < pel.getChanceSpawnSword()) {
                BlockPos at = attacker.getPosition()
                    .up();
                EntitySpectralTool esp = new EntitySpectralTool(
                    attacker.getEntityWorld(),
                    at,
                    new ItemStack(Items.DIAMOND_SWORD),
                    EntitySpectralTool.ToolTask.createAttackTask());
                attacker.getEntityWorld()
                    .spawnEntity(esp);
            }
        }
    }

    /**
     * {@link EntityPlayer#getDigSpeed(IBlockState, BlockPos)}
     */
    @SubscribeEvent
    public void onWaterBreak(PlayerEvent.BreakSpeed event) {
        EntityPlayer pl = event.entityPlayer;
        if (pl.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(pl)) {
            // Normally the break speed would be divided by 5 here in the actual logic. See link above
            CapeEffectOctans ceo = ItemCape.getCapeEffect(pl, Constellations.octans);
            if (ceo != null) {
                // Revert speed back to what we think is original.
                // Might stack with others that implement it the same way.
                event.newSpeed = (event.originalSpeed * 5);
            }
        }
    }

    @ASMCallHook
    public static float getWaterSlowDown(float oldSlowDown, EntityLivingBase base) {
        if (oldSlowDown < 1 && base instanceof EntityPlayer) {
            CapeEffectOctans ceo = ItemCape.getCapeEffect((EntityPlayer) base, Constellations.octans);
            if (ceo != null) {
                oldSlowDown = 0.95F; // Make sure it's not setting it to > 1 by itself.
            }
        }
        return oldSlowDown;
    }

    private void tickFornaxMelting(EntityPlayer pl) {
        if (pl.isBurning()) {
            CapeEffectFornax cf = ItemCape.getCapeEffect(pl, Constellations.fornax);
            if (cf != null) {
                cf.attemptMelt(pl);
            }
        }
    }

    private void tickAevitasEffect(EntityPlayer pl) {
        CapeEffectAevitas cd = ItemCape.getCapeEffect(pl, Constellations.aevitas);
        if (cd != null) {
            float potency = cd.getPotency();
            float range = cd.getRange();
            if (rand.nextFloat() < potency) {
                World w = pl.getEntityWorld();
                AxisAlignedBB bb = new AxisAlignedBB(-range, -range, -range, range, range, range);
                bb = bb.offset(pl.posX, pl.posY, pl.posZ);
                Predicate<Entity> pr = EntitySelectors.NOT_SPECTATING.and(EntitySelectorS.IS_ALIVE);
                List players = w.getEntitiesWithinAABB(EntityPlayer.class, bb, pr::test);
                for (EntityPlayer player : players) {
                    if (rand.nextFloat() <= cd.getFeedChancePerCycle()) {
                        player.heal(cd.getHealPerCycle());
                        player.getFoodStats()
                            .addStats(cd.getFoodLevelPerCycle(), cd.getFoodSaturationLevelPerCycle());
                    }
                }
            }
            if (rand.nextFloat() < cd.getTurnChance()) {
                int x = Math.round(-range + 1 + (2 * range * rand.nextFloat()));
                int y = Math.round(-range + 1 + (2 * range * rand.nextFloat()));
                int z = Math.round(-range + 1 + (2 * range * rand.nextFloat()));
                BlockPos at = pl.getPosition()
                    .add(x, y, z);
                IBlockState state = pl.getEntityWorld()
                    .getBlockState(at);
                if (Plants.matchesAny(state)) {
                    state = Plants.getAnyRandomState();
                    if (pl.getEntityWorld()
                        .setBlockState(at, state)) {
                        PktParticleEvent ev = new PktParticleEvent(
                            PktParticleEvent.ParticleEventType.CE_CROP_INTERACT,
                            at);
                        PacketChannel.CHANNEL
                            .sendToAllAround(ev, PacketChannel.pointFromPos(pl.getEntityWorld(), at, 16));
                    }
                } else {
                    CropHelper.GrowablePlant growable = CropHelper.wrapPlant(pl.getEntityWorld(), at);
                    if (growable != null) {
                        growable.tryGrow(pl.getEntityWorld(), rand);
                        PktParticleEvent ev = new PktParticleEvent(
                            PktParticleEvent.ParticleEventType.CE_CROP_INTERACT,
                            at);
                        PacketChannel.CHANNEL
                            .sendToAllAround(ev, PacketChannel.pointFromPos(pl.getEntityWorld(), at, 16));
                    }
                }
            }
        }
    }

    private void tickArmaraWornEffect(EntityPlayer pl) {
        CapeEffectArmara ca = ItemCape.getCapeEffect(pl, Constellations.armara);
        if (ca != null) {
            ca.wornTick();
        }
    }

    @SideOnly(Side.CLIENT)
    private void tickVicioClientEffect(EntityPlayer player) {
        if (player instanceof EntityPlayerSP spl) {
            boolean hasFlightPerk = ResearchManager.getProgress(spl, Side.CLIENT)
                .hasPerkEffect(p -> p instanceof KeyMantleFlight);
            if (spl.movementInput.jump && !hasFlightPerk
                && !spl.onGround
                && spl.motionY < -0.5
                && !spl.capabilities.isFlying
                && !spl.isInWater()
                && !spl.isInsideOfMaterial(Material.lava)) {
                if (!spl.isElytraFlying()) {
                    PacketChannel.CHANNEL.sendToServer(PktElytraCapeState.setFlying());
                }
                PacketChannel.CHANNEL.sendToServer(PktElytraCapeState.resetFallDistance());
            } else if (spl.isElytraFlying()) {
                PacketChannel.CHANNEL.sendToServer(PktElytraCapeState.resetFallDistance());
                if (spl.capabilities.isFlying || hasFlightPerk || spl.onGround || spl.isInWater() || spl.isInLava()) {
                    PacketChannel.CHANNEL.sendToServer(PktElytraCapeState.resetFlying());
                } else {
                    Vector3 mov = new Vector3(((EntityPlayerSP) player).motionX, 0, ((EntityPlayerSP) player).motionZ);
                    if (mov.length() <= 0.4F && ((EntityPlayerSP) player).motionY > 0.4F) {
                        PacketChannel.CHANNEL.sendToServer(PktElytraCapeState.resetFlying());
                    }
                }
            }
        }
    }

    private void tickOctansEffect(EntityPlayer pl) {
        CapeEffectOctans ceo = ItemCape.getCapeEffect(pl, Constellations.octans);
        if (ceo != null && pl.isInsideOfMaterial(Material.water)) {
            if (pl.getAir() < 300) {
                pl.setAir(300);
            }
            ceo.onWaterHealTick(pl);
        }
    }

    private void tickBootesEffect(EntityPlayer pl) {
        CapeEffectBootes ceo = ItemCape.getCapeEffect(pl, Constellations.bootes);
        if (ceo != null) {
            ceo.onPlayerTick(pl);
        }
    }

    private void tickVicioEffect(EntityPlayer pl) {
        if (!(pl instanceof EntityPlayerMP)) {
            return;
        }
        PlayerProgress prog = ResearchManager.getProgress(pl, Side.SERVER);
        if (!prog.hasPerkEffect(p -> p instanceof KeyMantleFlight)) {
            if (vicioMantleFlightPlayers.contains(pl.getUniqueID())) {
                if (pl.isCreative()) {
                    pl.capabilities.allowFlying = true;
                } else {
                    pl.capabilities.allowFlying = false;
                    pl.capabilities.isFlying = false;
                }
                pl.sendPlayerAbilities();
                vicioMantleFlightPlayers.remove(pl.getUniqueID());
            }
            return;
        }

        CapeEffectVicio ceo = ItemCape.getCapeEffect(pl, Constellations.vicio);
        if (ceo != null) {
            if (!vicioMantleFlightPlayers.contains(pl.getUniqueID())) {
                vicioMantleFlightPlayers.add(pl.getUniqueID());
            }
            if (!pl.capabilities.allowFlying) {
                pl.capabilities.allowFlying = true;
                pl.sendPlayerAbilities();
            }
        } else if (vicioMantleFlightPlayers.contains(pl.getUniqueID())) {
            if (pl.isCreative()) {
                pl.capabilities.allowFlying = true;
            } else {
                pl.capabilities.allowFlying = false;
                pl.capabilities.isFlying = false;
            }
            pl.sendPlayerAbilities();
            vicioMantleFlightPlayers.remove(pl.getUniqueID());
        }
    }

    @ASMCallHook
    public static void updateElytraEventPre(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            CapeEffectVicio vic = ItemCape.getCapeEffect((EntityPlayer) entity, Constellations.vicio);
            if (vic != null) {
                updateElytraBuffer = entity.getFlag(7);
                inElytraCheck = true;
            }
        }
    }

    @ASMCallHook
    public static void updateElytraEventPost(EntityLivingBase entity) {
        inElytraCheck = false;
        if (entity instanceof EntityPlayer && updateElytraBuffer) {
            CapeEffectVicio vic = ItemCape.getCapeEffect((EntityPlayer) entity, Constellations.vicio);
            if (vic != null) {
                boolean current = entity.getFlag(7);
                // So the state from true before has now changed to false.
                // We need to check if the item not being an elytra is responsible for that.
                if (!current) {
                    if (!((EntityPlayer) entity).onGround && !entity.isRiding()) {
                        entity.setFlag(7, true);
                    }
                }

                // Vector3 mV = new Vector3(entity.motionX, entity.motionY, entity.motionZ).normalize().multiply(0.65F);
                // entity.motionX += mV.getX() * 0.1D + (mV.getX() * 1.5D - entity.motionX) * 0.5D;
                // entity.motionY += mV.getY() * 0.1D + (mV.getY() * 1.5D - entity.motionY) * 0.5D;
                // entity.motionZ += mV.getZ() * 0.1D + (mV.getZ() * 1.5D - entity.motionZ) * 0.5D;
                entity.motionX *= 1.006F;
                entity.motionY *= 1.006F;
                entity.motionZ *= 1.006F;
            }
        }
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        if (Objects.requireNonNull(type) == TickEvent.Type.PLAYER) {
            EntityPlayer pl = (EntityPlayer) context[0];
            Side side = (Side) context[1];
            if (side == Side.SERVER) {
                if (!(pl instanceof EntityPlayerMP)) return;
                if (MiscUtils.isPlayerFakeMP((EntityPlayerMP) pl)) return;

                tickAevitasEffect(pl);
                tickFornaxMelting(pl);
                tickArmaraWornEffect(pl);
                tickOctansEffect(pl);
                tickBootesEffect(pl);
                tickVicioEffect(pl);
            } else if (side == Side.CLIENT) {
                CapeArmorEffect cae = ItemCape.getCapeEffect(pl);
                if (cae != null) {
                    cae.playActiveParticleTick(pl);
                }
                CapeEffectVicio vic = ItemCape.getCapeEffect(pl, Constellations.vicio);
                if (vic != null) {
                    tickVicioClientEffect(pl);
                }
                CapeEffectLucerna luc = ItemCape.getCapeEffect(pl, Constellations.lucerna);
                if (luc != null) {
                    luc.playClientHighlightTick(pl);
                }
                CapeEffectMineralis min = ItemCape.getCapeEffect(pl, Constellations.mineralis);
                if (min != null) {
                    min.playClientHighlightTick(pl);
                }
            }
        }
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.PLAYER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Cape-EventHandler";
    }

}
