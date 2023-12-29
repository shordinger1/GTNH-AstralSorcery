/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.auxiliary.tick.ITickHandler;
import shordinger.astralsorcery.common.constellation.perk.tree.PerkTree;
import shordinger.astralsorcery.common.constellation.perk.types.IConverterProvider;
import shordinger.astralsorcery.common.constellation.perk.types.ICooldownPerk;
import shordinger.astralsorcery.common.constellation.perk.types.IPlayerTickPerk;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.server.PktSyncPerkActivity;
import shordinger.astralsorcery.common.util.data.TimeoutListContainer;
import shordinger.astralsorcery.common.util.log.LogCategory;
import shordinger.astralsorcery.migration.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkEffectHelper
 * Created by HellFirePvP
 * Date: 30.06.2018 / 15:25
 */
public class PerkEffectHelper implements ITickHandler {

    public static TimeoutListContainer<PlayerWrapperContainer, ResourceLocation> perkCooldowns = new TimeoutListContainer<>(
        new PerkTimeoutHandler(),
        TickEvent.Type.SERVER);
    public static TimeoutListContainer<PlayerWrapperContainer, ResourceLocation> perkCooldownsClient = new TimeoutListContainer<>(
        new PerkTimeoutHandler(),
        TickEvent.Type.CLIENT);

    public static final PerkEffectHelper EVENT_INSTANCE = new PerkEffectHelper();

    private PerkEffectHelper() {
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ServerDisconnectionFromClientEvent event) {
        LogCategory.PERKS.info(
            () -> ((NetHandlerPlayServer) event.getHandler()).player.getDisplayName()
                + " disconnected from server on side SERVER");
        AstralSorcery.proxy.scheduleDelayed(
            () -> handlePerkModification(((NetHandlerPlayServer) event.getHandler()).player, Side.SERVER, true));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    @SideOnly(Side.CLIENT)
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        LogCategory.PERKS.info(() -> "Disconnected from server on side CLIENT");
        PerkAttributeHelper.clearClient();
    }

    @SubscribeEvent
    public void onConnect(FMLNetworkEvent.ServerConnectionFromClientEvent event) {
        LogCategory.PERKS.info(
            () -> ((NetHandlerPlayServer) event.getHandler()).player.getDisplayName()
                + " connected to server on side SERVER");
        AstralSorcery.proxy.scheduleDelayed(
            () -> handlePerkModification(((NetHandlerPlayServer) event.getHandler()).player, Side.SERVER, false));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        LogCategory.PERKS.info(() -> "Connected to server on side CLIENT");
        AstralSorcery.proxy.scheduleClientside(new Runnable() {

            @Override
            public void run() {
                if (Minecraft.getMinecraft().thePlayer != null && ResearchManager.clientInitialized) {
                    handlePerkModification(Minecraft.getMinecraft().thePlayer, Side.CLIENT, false);
                } else {
                    AstralSorcery.proxy.scheduleClientside(this);
                }
            }
        });
    }

    @SubscribeEvent
    public void playerClone(PlayerEvent.Clone event) {
        EntityPlayer oldPlayer = event.getOriginal();
        EntityPlayer newPlayer = event.entityPlayer;

        handlePerkModification(oldPlayer, oldPlayer.world.isRemote ? Side.CLIENT : Side.SERVER, true);
        handlePerkModification(newPlayer, newPlayer.world.isRemote ? Side.CLIENT : Side.SERVER, false);

        PlayerWrapperContainer container = new PlayerWrapperContainer(oldPlayer);
        if (perkCooldowns.hasList(container)) {
            perkCooldowns.removeList(container);
        }
        if (perkCooldownsClient.hasList(container)) {
            perkCooldownsClient.removeList(container);
        }

        if (newPlayer instanceof EntityPlayerMP) {
            PacketChannel.CHANNEL
                .sendTo(new PktSyncPerkActivity(PktSyncPerkActivity.Type.UNLOCKALL), (EntityPlayerMP) newPlayer);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void expRemoval(LivingDeathEvent event) {
        if (event.entityLiving instanceof EntityPlayer player) {
            Side side = event.entityLiving.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
            if (side != Side.SERVER) return;

            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog.isValid()) {
                long exp = MathHelper.lfloor(prog.getPerkExp());
                int level = prog.getPerkLevel(player);
                long expThisLevel = PerkLevelManager.INSTANCE.getExpForLevel(level - 1, player);
                long expNextLevel = PerkLevelManager.INSTANCE.getExpForLevel(level, player);

                float removePerDeath = 0.25F;
                int remove = MathHelper.floor(((float) (expNextLevel - expThisLevel)) * removePerDeath);
                if (exp - remove < expThisLevel) {
                    exp = expThisLevel;
                } else {
                    exp -= remove;
                }

                ResearchManager.setExp(player, exp);
            }
        }
    }

    private void handlePerkModification(EntityPlayer player, Side side, boolean remove) {
        PlayerProgress progress = ResearchManager.getProgress(player, side);
        if (progress.isValid()) {
            LogCategory.PERKS.info(
                () -> (remove ? "Remove" : "Apply") + " ALL perks for "
                    + player.getDisplayName()
                    + " on side "
                    + side.name());
            for (AbstractPerk perk : progress.getAppliedPerks()) {
                if (remove) {
                    handlePerkRemoval(perk, player, side);
                } else {
                    handlePerkApplication(perk, player, side);
                }
            }
        }
    }

    public void notifyPerkChange(EntityPlayer player, Side side, AbstractPerk perk, boolean remove) {
        PlayerProgress progress = ResearchManager.getProgress(player, side);
        if (progress.isValid()) {
            if (remove) {
                handlePerkRemoval(perk, player, side);
            } else {
                handlePerkApplication(perk, player, side);
            }
        }
    }

    private void handlePerkApplication(AbstractPerk perk, EntityPlayer player, Side side) {
        Collection<PerkConverter> converters = Lists.newArrayList();
        if (perk instanceof IConverterProvider) {
            converters = ((IConverterProvider) perk).provideConverters(player, side);
        }
        LogCategory.PERKS.info(
            () -> "Apply perk " + perk
                .getRegistryName() + " for player " + player.getDisplayName() + " on side " + side.name());
        batchApplyConverters(player, side, converters, perk);
    }

    private void handlePerkRemoval(AbstractPerk perk, EntityPlayer player, Side side) {
        Collection<PerkConverter> converters = Lists.newArrayList();
        if (perk instanceof IConverterProvider) {
            converters = ((IConverterProvider) perk).provideConverters(player, side);
        }
        LogCategory.PERKS.info(
            () -> "Remove perk " + perk
                .getRegistryName() + " for player " + player.getDisplayName() + " on side " + side.name());
        batchRemoveConverters(player, side, converters, perk);
    }

    @SideOnly(Side.CLIENT)
    public void notifyPerkDataChangeClient(EntityPlayer player, AbstractPerk perk, NBTTagCompound oldData,
                                           NBTTagCompound newData) {
        LogCategory.PERKS.info(() -> "Updating data for perk " + perk.getRegistryName() + " on CLIENT");
        ResearchManager.getProgress(player, Side.CLIENT)
            .applyPerk(perk, oldData);
        notifyPerkChange(player, Side.CLIENT, perk, true);
        ResearchManager.getProgress(player, Side.CLIENT)
            .applyPerk(perk, newData);
        notifyPerkChange(player, Side.CLIENT, perk, false);
    }

    @SideOnly(Side.CLIENT)
    public void clearAllPerksClient(EntityPlayer player) {
        PlayerAttributeMap attr = PerkAttributeHelper.getOrCreateMap(player, Side.CLIENT);
        LogCategory.PERKS.info(() -> "Remove ALL CACHED perks on CLIENT");
        List<AbstractPerk> copyPerks = new ArrayList<>(attr.getCacheAppliedPerks());
        for (AbstractPerk perk : copyPerks) {
            handlePerkRemoval(perk, player, Side.CLIENT);
        }
    }

    @SideOnly(Side.CLIENT)
    public void reapplyAllPerksClient(EntityPlayer player) {
        LogCategory.PERKS.info(() -> "Apply ALL perks from KNOWLEDGE DATA on CLIENT");
        handlePerkModification(player, Side.CLIENT, false);

        PlayerWrapperContainer container = new PlayerWrapperContainer(player);
        if (perkCooldowns.hasList(container)) {
            perkCooldowns.removeList(container);
        }
        if (perkCooldownsClient.hasList(container)) {
            perkCooldownsClient.removeList(container);
        }
    }

    @SideOnly(Side.CLIENT)
    public void refreshAllPerksClient(EntityPlayer player) {
        clearAllPerksClient(player);
        reapplyAllPerksClient(player);
    }

    // Know that if you apply global converters, you're also responsible for removing them at the appropriate time...
    public void applyGlobalConverters(EntityPlayer player, Side side, PerkConverter... converters) {
        applyGlobalConverters(player, side, Arrays.asList(converters));
    }

    public void applyGlobalConverters(EntityPlayer player, Side side, List<PerkConverter> converters) {
        batchApplyConverters(player, side, converters, null);
    }

    public void removeGlobalConverters(EntityPlayer player, Side side, PerkConverter... converters) {
        removeGlobalConverters(player, side, Arrays.asList(converters));
    }

    public void removeGlobalConverters(EntityPlayer player, Side side, List<PerkConverter> converters) {
        batchRemoveConverters(player, side, converters, null);
    }

    private synchronized void batchApplyConverters(EntityPlayer player, Side side, Collection<PerkConverter> converters,
                                                   @Nullable AbstractPerk onlyAdd) {
        Thread tr = Thread.currentThread();
        if (!"Client thread".equalsIgnoreCase(tr.getName()) && !"Server thread".equalsIgnoreCase(tr.getName())) {
            AstralSorcery.log.error("Called perk modification outside synced thread!");
            throw new RuntimeException("Modified perks outside the main thread(s)");
        }

        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (prog.isValid()) {
            PlayerAttributeMap attributeMap = PerkAttributeHelper.getOrCreateMap(player, side);
            List<AbstractPerk> perks = new LinkedList<>(prog.getAppliedPerks());
            perks = perks.stream()
                .filter(attributeMap::isPerkApplied)
                .collect(Collectors.toList());

            List<AbstractPerk> logPerks1 = perks;
            LogCategory.PERKS.info(
                () -> "Removing " + logPerks1.size()
                    + " APPLIED perks on "
                    + side.name()
                    + " based on KNOWLEDGE DATA (filtered down to applied perks based on CACHE)");

            perks.forEach(perk -> perk.removePerk(player, side));

            if (onlyAdd == null || !prog.isPerkSealed(onlyAdd)) {
                converters.forEach((c) -> attributeMap.applyConverter(player, c));
            }

            if (onlyAdd != null && !prog.isPerkSealed(onlyAdd) && !perks.contains(onlyAdd)) {
                LogCategory.PERKS.info(() -> "Adding " + onlyAdd.getRegistryName() + " to perks on " + side.name());
                perks.add(onlyAdd);
            }

            List<AbstractPerk> logPerks2 = perks;
            LogCategory.PERKS.info(() -> "Applying " + logPerks2.size() + " perks on " + side.name());

            perks.forEach(perk -> perk.applyPerk(player, side));
        }
    }

    private synchronized void batchRemoveConverters(EntityPlayer player, Side side,
                                                    Collection<PerkConverter> converters, @Nullable AbstractPerk onlyRemove) {
        Thread tr = Thread.currentThread();
        if (!"Client thread".equalsIgnoreCase(tr.getName()) && !"Server thread".equalsIgnoreCase(tr.getName())) {
            AstralSorcery.log.error("Called perk modification outside synced thread!");
            throw new RuntimeException("Modified perks outside the main thread(s)");
        }

        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (prog.isValid()) {
            PlayerAttributeMap attributeMap = PerkAttributeHelper.getOrCreateMap(player, side);
            List<AbstractPerk> perks = new ArrayList<>(attributeMap.getCacheAppliedPerks());

            LogCategory.PERKS.info(
                () -> "Removing " + perks.size() + " APPLIED perks on " + side.name() + " based on APPLICATION CACHE");

            perks.forEach(perk -> perk.removePerk(player, side));

            converters.forEach((c) -> attributeMap.removeConverter(player, c));

            if (onlyRemove != null) {
                LogCategory.PERKS
                    .info(() -> "Removing " + onlyRemove.getRegistryName() + " from perks on " + side.name());
                perks.remove(onlyRemove);
            }

            LogCategory.PERKS.info(() -> "Applying " + perks.size() + " perks on " + side.name());

            perks.forEach(perk -> perk.applyPerk(player, side));
        }
    }

    public final boolean isCooldownActiveForPlayer(EntityPlayer player, AbstractPerk perk) {
        if (!(perk instanceof ICooldownPerk)) return false;

        TimeoutListContainer<PlayerWrapperContainer, ResourceLocation> container = player.getEntityWorld().isRemote
            ? perkCooldownsClient
            : perkCooldowns;
        PlayerWrapperContainer ct = new PlayerWrapperContainer(player);
        return container.hasList(ct) && container.getOrCreateList(ct)
            .contains(perk.getRegistryName());
    }

    public final void setCooldownActiveForPlayer(EntityPlayer player, AbstractPerk perk, int cooldownTicks) {
        if (!(perk instanceof ICooldownPerk)) return;

        LogCategory.PERKS.info(
            () -> "Set perk cooldown on " + perk.getRegistryName()
                + " for "
                + player.getDisplayName()
                + " on "
                + (player.getEntityWorld().isRemote ? "CLIENT" : "SERVER"));

        TimeoutListContainer<PlayerWrapperContainer, ResourceLocation> container = player.getEntityWorld().isRemote
            ? perkCooldownsClient
            : perkCooldowns;
        PlayerWrapperContainer ct = new PlayerWrapperContainer(player);
        container.getOrCreateList(ct)
            .setOrAddTimeout(cooldownTicks, perk.getRegistryName());
    }

    public final void forceSetCooldownForPlayer(EntityPlayer player, AbstractPerk perk, int cooldownTicks) {
        if (!(perk instanceof ICooldownPerk)) return;

        LogCategory.PERKS.info(
            () -> "Force update perk cooldown on " + perk.getRegistryName()
                + " for "
                + player.getDisplayName()
                + " on "
                + (player.getEntityWorld().isRemote ? "CLIENT" : "SERVER"));

        TimeoutListContainer<PlayerWrapperContainer, ResourceLocation> container = player.getEntityWorld().isRemote
            ? perkCooldownsClient
            : perkCooldowns;
        PlayerWrapperContainer ct = new PlayerWrapperContainer(player);
        if (!container.getOrCreateList(ct)
            .setTimeout(cooldownTicks, perk.getRegistryName())) {
            setCooldownActiveForPlayer(player, perk, cooldownTicks);
        }
    }

    public final int getActiveCooldownForPlayer(EntityPlayer player, AbstractPerk perk) {
        if (!(perk instanceof ICooldownPerk)) return -1;

        TimeoutListContainer<PlayerWrapperContainer, ResourceLocation> container = player.getEntityWorld().isRemote
            ? perkCooldownsClient
            : perkCooldowns;
        PlayerWrapperContainer ct = new PlayerWrapperContainer(player);
        if (!container.hasList(ct)) {
            return -1;
        }
        return container.getOrCreateList(ct)
            .getTimeout(perk.getRegistryName());
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        EntityPlayer ticked = (EntityPlayer) context[0];
        Side side = (Side) context[1];
        PlayerProgress prog = ResearchManager.getProgress(ticked, side);
        if (prog.isValid()) {
            for (AbstractPerk perk : prog.getAppliedPerks()) {
                if (perk instanceof IPlayerTickPerk && prog.hasPerkEffect(perk)) {
                    ((IPlayerTickPerk) perk).onPlayerTick(ticked, side);
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
        return "PlayerPerkHandler";
    }

    public static class PerkTimeoutHandler
        implements TimeoutListContainer.ContainerTimeoutDelegate<PlayerWrapperContainer, ResourceLocation> {

        @Override
        public void onContainerTimeout(PlayerWrapperContainer plWrapper, ResourceLocation key) {
            AbstractPerk perk = PerkTree.PERK_TREE.getPerk(key);
            if (perk instanceof ICooldownPerk) {
                LogCategory.PERKS.info(
                    () -> "Perk cooldown has finished on " + perk.getRegistryName()
                        + " for "
                        + plWrapper.player.getDisplayName()
                        + " on "
                        + (plWrapper.player.getEntityWorld().isRemote ? "CLIENT" : "SERVER"));

                ((ICooldownPerk) perk).handleCooldownTimeout(plWrapper.player);
            }
        }
    }

    public static class PlayerWrapperContainer {

        @Nonnull
        public final EntityPlayer player;

        public PlayerWrapperContainer(@Nonnull EntityPlayer player) {
            this.player = player;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (!(obj instanceof PlayerWrapperContainer)) return false;
            return ((PlayerWrapperContainer) obj).player.getUniqueID()
                .equals(player.getUniqueID());
        }

        @Override
        public int hashCode() {
            return player.getUniqueID()
                .hashCode();
        }

    }

}
