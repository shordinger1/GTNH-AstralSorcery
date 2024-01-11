/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.data.research;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.io.Files;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.event.ClientRenderEventHandler;
import shordinger.astralsorcery.common.block.network.BlockAltar;
import shordinger.astralsorcery.common.constellation.ConstellationRegistry;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.constellation.IMajorConstellation;
import shordinger.astralsorcery.common.constellation.perk.AbstractPerk;
import shordinger.astralsorcery.common.constellation.perk.PerkEffectHelper;
import shordinger.astralsorcery.common.constellation.perk.tree.PerkTree;
import shordinger.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import shordinger.astralsorcery.common.crafting.infusion.ActiveInfusionTask;
import shordinger.astralsorcery.common.item.tool.sextant.SextantFinder;
import shordinger.astralsorcery.common.lib.AdvancementTriggers;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.server.PktProgressionUpdate;
import shordinger.astralsorcery.common.network.packet.server.PktSyncKnowledge;
import shordinger.astralsorcery.common.network.packet.server.PktSyncPerkActivity;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.tile.TileStarlightInfuser;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.server.MinecraftServer;
import shordinger.wrapper.net.minecraft.util.text.Style;
import shordinger.wrapper.net.minecraft.util.text.TextComponentString;
import shordinger.wrapper.net.minecraft.util.text.TextFormatting;
import shordinger.wrapper.net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ResearchManager
 * Created by HellFirePvP
 * Date: 07.05.2016 / 13:33
 */
public class ResearchManager {

    public static PlayerProgress clientProgress = new PlayerProgress();
    public static boolean clientInitialized = false;

    private static Map<UUID, PlayerProgress> playerProgressServer = new HashMap<>();

    @Nonnull
    public static PlayerProgress getProgress(EntityPlayer player) {
        return getProgress(player, player.getEntityWorld().isRemote ? Side.CLIENT : Side.SERVER);
    }

    @Nonnull
    public static PlayerProgress getProgress(EntityPlayer player, Side side) {
        if (side == Side.CLIENT) {
            return clientProgress;
        } else if (player instanceof EntityPlayerMP) {
            return getProgressServer((EntityPlayerMP) player);
        } else {
            return new PlayerProgressTestAccess();
        }
    }

    @Nonnull
    private static PlayerProgress getProgressServer(EntityPlayerMP player) {
        if (MiscUtils.isPlayerFakeMP(player)) {
            return new PlayerProgressTestAccess();
        }
        return getProgress(player.getUniqueID());
    }

    @Nonnull
    private static PlayerProgress getProgress(UUID uuid) {
        PlayerProgress progress = playerProgressServer.get(uuid);
        if (progress == null) {
            loadPlayerKnowledge(uuid);
            progress = playerProgressServer.get(uuid);
        }
        if (progress == null) {
            progress = new PlayerProgress(); // WELL we already try recovering.. so wtf.
            // AstralSorcery.log.warn("Failed to load AstralSocery Progress data!");
            // AstralSorcery.log.warn("Erroneous file: " + uuid.toString() + ".astral");
        }
        return progress;
    }

    public static void wipeKnowledge(EntityPlayerMP p) {
        resetPerks(p);
        wipeFile(p);
        playerProgressServer.remove(p.getUniqueID());
        PktProgressionUpdate pkt = new PktProgressionUpdate();
        PacketChannel.CHANNEL.sendTo(pkt, p);
        PktSyncKnowledge pk = new PktSyncKnowledge(PktSyncKnowledge.STATE_WIPE);
        PacketChannel.CHANNEL.sendTo(pk, p);
        loadPlayerKnowledge(p);
        pushProgressToClientUnsafe(p);
    }

    public static void sendInitClientKnowledge(EntityPlayerMP p) {
        UUID uuid = p.getUniqueID();
        if (playerProgressServer.get(uuid) == null) {
            loadPlayerKnowledge(p);
        }
        if (playerProgressServer.get(uuid) == null) {
            AstralSorcery.log.warn("Failed to load AstralSocery Progress data for " + p.getName());
            AstralSorcery.log.warn("Erroneous file: " + uuid.toString() + ".astral");
            return;
        }
        pushProgressToClientUnsafe(p);
    }

    public static void unsafeForceGiveResearch(EntityPlayerMP player, ResearchProgression prog) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return;

        ProgressionTier reqTier = prog.getRequiredProgress();
        if (!progress.getTierReached()
            .isThisLaterOrEqual(reqTier)) {
            progress.setTierReached(reqTier);
        }

        LinkedList<ResearchProgression> progToGive = new LinkedList<>();
        progToGive.add(prog);
        while (!progToGive.isEmpty()) {
            ResearchProgression give = progToGive.pop();
            if (!progress.getResearchProgression()
                .contains(give)) {
                progress.forceGainResearch(give);
            }
            progToGive.addAll(give.getPreConditions());
        }

        PktProgressionUpdate pkt = new PktProgressionUpdate();
        PacketChannel.CHANNEL.sendTo(pkt, player);

        pushProgressToClientUnsafe(player);
        savePlayerKnowledge(player);
    }

    public static void giveResearchIgnoreFail(EntityPlayer player, ResearchProgression prog) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return;

        ProgressionTier tier = prog.getRequiredProgress();
        if (!progress.getTierReached()
            .isThisLaterOrEqual(tier)) return;
        for (ResearchProgression other : prog.getPreConditions()) {
            if (!progress.getResearchProgression()
                .contains(other)) return;
        }

        if (progress.forceGainResearch(prog)) {
            PktProgressionUpdate pkt = new PktProgressionUpdate(prog);
            PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) player);
        }

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
    }

    public static void giveProgressionIgnoreFail(EntityPlayer player, ProgressionTier tier) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return;

        ProgressionTier t = progress.getTierReached();
        if (!t.hasNextTier()) return; // No higher tier available anyway.
        ProgressionTier next = t.next();
        if (!next.equals(tier)) return; // Given one is not the next step.

        progress.setTierReached(next);
        PktProgressionUpdate pkt = new PktProgressionUpdate(next);
        PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) player);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
    }

    public static boolean mergeApplyPlayerprogress(PlayerProgress toMergeFrom, EntityPlayer player) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        progress.acceptMergeFrom(toMergeFrom);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean useSextantTarget(SextantFinder.TargetObject to, EntityPlayer player) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        progress.useTarget(to);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean discoverConstellations(Collection<IConstellation> csts, EntityPlayer player) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        for (IConstellation c : csts) {
            progress.discoverConstellation(c.getUnlocalizedName());
            AdvancementTriggers.DISCOVER_CONSTELLATION.trigger((EntityPlayerMP) player, c);
        }

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean discoverConstellation(IConstellation c, EntityPlayer player) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        progress.discoverConstellation(c.getUnlocalizedName());

        AdvancementTriggers.DISCOVER_CONSTELLATION.trigger((EntityPlayerMP) player, c);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean memorizeConstellation(IConstellation c, EntityPlayer player) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        progress.memorizeConstellation(c.getUnlocalizedName());

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean maximizeTier(EntityPlayer player) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        progress.setTierReached(ProgressionTier.values()[ProgressionTier.values().length - 1]);

        PktProgressionUpdate pkt = new PktProgressionUpdate();
        PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) player);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean setAttunedBefore(EntityPlayer player, boolean wasAttunedBefore) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        progress.setAttunedBefore(wasAttunedBefore);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean setAttunedConstellation(EntityPlayer player, @Nullable IMajorConstellation constellation) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        if (constellation != null && !progress.getKnownConstellations()
            .contains(constellation.getUnlocalizedName())) {
            return false;
        }

        Map<AbstractPerk, NBTTagCompound> perkCopy = new HashMap<>(progress.getUnlockedPerkData());
        for (Map.Entry<AbstractPerk, NBTTagCompound> perkEntry : perkCopy.entrySet()) {
            dropPerk(progress, player, Side.SERVER, perkEntry.getKey(), perkEntry.getValue());
        }

        PacketChannel.CHANNEL
            .sendTo(new PktSyncPerkActivity(PktSyncPerkActivity.Type.CLEARALL), (EntityPlayerMP) player);

        progress.setExp(0);
        progress.setAttunedConstellation(constellation);
        AbstractPerk root;
        if (constellation != null && (root = PerkTree.PERK_TREE.getRootPerk(constellation)) != null) {
            NBTTagCompound data = new NBTTagCompound();
            root.onUnlockPerkServer(player, progress, data);
            progress.applyPerk(root, data);
            PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Side.SERVER, root, false);
            PacketChannel.CHANNEL.sendTo(new PktSyncPerkActivity(root, true), (EntityPlayerMP) player);
        }

        AdvancementTriggers.ATTUNE_SELF.trigger((EntityPlayerMP) player, constellation);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean setPerkData(EntityPlayer player, @Nonnull AbstractPerk perk, NBTTagCompound prevoiusData,
                                      NBTTagCompound newData) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;
        if (!progress.hasPerkEffect(perk)) return false;

        PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Side.SERVER, perk, true);
        progress.applyPerk(perk, newData);
        PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Side.SERVER, perk, false);

        PacketChannel.CHANNEL.sendTo(new PktSyncPerkActivity(perk, prevoiusData, newData), (EntityPlayerMP) player);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean applyPerk(EntityPlayer player, @Nonnull AbstractPerk perk) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;
        if (!progress.hasFreeAllocationPoint(player)) return false;
        if (progress.hasPerkUnlocked(perk)) return false;

        NBTTagCompound data = new NBTTagCompound();
        perk.onUnlockPerkServer(player, progress, data);
        progress.applyPerk(perk, data);

        PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Side.SERVER, perk, false);
        PacketChannel.CHANNEL.sendTo(new PktSyncPerkActivity(perk, true), (EntityPlayerMP) player);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean applyPerkSeal(EntityPlayer player, @Nonnull AbstractPerk perk) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;
        if (!progress.hasPerkUnlocked(perk)) return false;
        if (progress.isPerkSealed(perk)) return false;

        if (!progress.sealPerk(perk)) {
            return false;
        }

        PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Side.SERVER, perk, true);
        PacketChannel.CHANNEL.sendTo(new PktSyncPerkActivity(perk, false), (EntityPlayerMP) player);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean breakPerkSeal(EntityPlayer player, @Nonnull AbstractPerk perk) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;
        if (!progress.hasPerkUnlocked(perk)) return false;
        if (!progress.isPerkSealed(perk)) return false;

        if (!progress.breakSeal(perk)) {
            return false;
        }

        PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Side.SERVER, perk, false);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);

        // Send way after research sync...
        AstralSorcery.proxy.scheduleDelayed(
            () -> {
                PacketChannel.CHANNEL.sendTo(new PktSyncPerkActivity(perk, true), (EntityPlayerMP) player);
            });
        return true;
    }

    public static boolean grantFreePerkPoint(EntityPlayer player, String token) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        if (!progress.grantFreeAllocationPoint(token)) {
            return false;
        }

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean revokeFreePoint(EntityPlayer player, String token) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        if (!progress.tryRevokeAllocationPoint(token)) {
            return false;
        }

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean forceApplyPerk(EntityPlayer player, @Nonnull AbstractPerk perk) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;
        if (progress.hasPerkUnlocked(perk)) return false;

        NBTTagCompound data = new NBTTagCompound();
        perk.onUnlockPerkServer(player, progress, data);
        progress.applyPerk(perk, data);

        PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Side.SERVER, perk, false);
        PacketChannel.CHANNEL.sendTo(new PktSyncPerkActivity(perk, true), (EntityPlayerMP) player);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean removePerk(EntityPlayer player, AbstractPerk perk) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        NBTTagCompound data = progress.getPerkData(perk);
        if (data == null) {
            return false;
        }
        dropPerk(progress, player, Side.SERVER, perk, data);

        PacketChannel.CHANNEL.sendTo(new PktSyncPerkActivity(perk, false), (EntityPlayerMP) player);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean resetPerks(EntityPlayer player) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        Map<AbstractPerk, NBTTagCompound> perkCopy = new HashMap<>(progress.getUnlockedPerkData());
        for (Map.Entry<AbstractPerk, NBTTagCompound> perkEntry : perkCopy.entrySet()) {
            dropPerk(progress, player, Side.SERVER, perkEntry.getKey(), perkEntry.getValue());
        }

        PacketChannel.CHANNEL
            .sendTo(new PktSyncPerkActivity(PktSyncPerkActivity.Type.CLEARALL), (EntityPlayerMP) player);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    private static void dropPerk(PlayerProgress progress, EntityPlayer player, Side side, AbstractPerk perk,
                                 NBTTagCompound data) {
        progress.removePerk(perk);
        PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, side, perk, true);
        perk.onRemovePerkServer(player, progress, data);
        progress.removePerkData(perk);
    }

    public static boolean setTomeReceived(EntityPlayer player) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        progress.setTomeReceived();

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean setExp(EntityPlayer player, long exp) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        progress.setExp(exp);

        AdvancementTriggers.PERK_LEVEL.trigger((EntityPlayerMP) player);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean modifyExp(EntityPlayer player, double exp) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;

        progress.modifyExp(exp, player);

        AdvancementTriggers.PERK_LEVEL.trigger((EntityPlayerMP) player);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static void forceMaximizeAll(EntityPlayer player) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return;
        ProgressionTier before = progress.getTierReached();

        ResearchManager.discoverConstellations(ConstellationRegistry.getAllConstellations(), player);
        ResearchManager.maximizeTier(player);
        ResearchManager.forceMaximizeResearch(player);
        ResearchManager.setAttunedBefore(player, true);
        for (SextantFinder.TargetObject to : SextantFinder.getSelectableTargets()) {
            progress.useTarget(to);
        }

        if (progress.getTierReached()
            .isThisLater(before)) {
            PktProgressionUpdate pkt = new PktProgressionUpdate(progress.getTierReached());
            PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) player);
        }

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
    }

    public static boolean forceMaximizeResearch(EntityPlayer player) {
        PlayerProgress progress = getProgress(player, Side.SERVER);
        if (!progress.isValid()) return false;
        for (ResearchProgression progression : ResearchProgression.values()) {
            progress.forceGainResearch(progression);
        }

        PktProgressionUpdate pkt = new PktProgressionUpdate();
        PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) player);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    private static void pushProgressToClientUnsafe(EntityPlayerMP p) {
        PlayerProgress progress = playerProgressServer.get(p.getUniqueID());
        PktSyncKnowledge pkt = new PktSyncKnowledge(PktSyncKnowledge.STATE_ADD);
        pkt.load(progress);
        PacketChannel.CHANNEL.sendTo(pkt, p);
    }

    private static void wipeFile(EntityPlayerMP player) {
        getPlayerFile(player).delete();
        ResearchIOThread.cancelSave(player.getUniqueID());
    }

    public static void savePlayerKnowledge(EntityPlayerMP p) {
        if (!MiscUtils.isPlayerFakeMP(p)) {
            savePlayerKnowledge(p.getUniqueID(), false);
        }
    }

    private static void savePlayerKnowledge(UUID pUUID, boolean force) {
        if (playerProgressServer.get(pUUID) == null) return;
        PlayerProgress progress = playerProgressServer.get(pUUID);
        if (force) {
            ResearchIOThread.saveNow(pUUID, progress);
        } else {
            ResearchIOThread.saveProgress(pUUID, progress.copy());
        }
    }

    public static void loadPlayerKnowledge(EntityPlayerMP p) {
        if (!MiscUtils.isPlayerFakeMP(p)) {
            loadPlayerKnowledge(p.getUniqueID());
        }
    }

    private static void loadPlayerKnowledge(UUID pUUID) {
        File playerFile = getPlayerFile(pUUID);
        try {
            load_unsafe(pUUID, playerFile);
        } catch (Exception e) {
            AstralSorcery.log.warn("Unable to load progress from default progress file. Attempting loading backup.");
            AstralSorcery.log.warn("Erroneous file: " + playerFile.getName());
            e.printStackTrace();

            playerFile = getPlayerBackupFile(pUUID);
            try {
                load_unsafe(pUUID, playerFile);
                Files.copy(playerFile, getPlayerFile(pUUID)); // Copying back.
            } catch (Exception e1) {
                AstralSorcery.log
                    .warn("Unable to load progress from backup progress file. Copying relevant files to error files.");
                AstralSorcery.log.warn("Erroneous file: " + playerFile.getName());
                e1.printStackTrace();

                File plOriginal = getPlayerFile(pUUID);
                File plBackup = getPlayerBackupFile(pUUID);
                try {
                    Files.copy(plOriginal, new File(plOriginal.getParent(), plOriginal.getName() + ".lerror"));
                    Files.copy(plBackup, new File(plBackup.getParent(), plBackup.getName() + ".lerror"));
                    AstralSorcery.log.warn(
                        "Copied progression files to error files. In case you would like to try me (HellFirePvP) to maybe see what i can do about maybe recovering the files,");
                    AstralSorcery.log.warn(
                        "send them over to me at the issue tracker https://github.com/HellFirePvP/AstralSorcery/issues - 90% that i won't be able to do anything, but reporting it would still be great.");
                } catch (IOException e2) {
                    AstralSorcery.log.warn("Unable to copy files to error-files.");
                    AstralSorcery.log
                        .warn("I've had enough. I can't even access or open the files apparently. I'm giving up.");
                    e2.printStackTrace();
                }
                plOriginal.delete();
                plBackup.delete();

                informPlayersAboutProgressionLoss(pUUID);

                load_unsafeFromNBT(pUUID, null);
                savePlayerKnowledge(pUUID, true);
            }
        }
    }

    private static void load_unsafe(UUID pUUID, File playerFile) throws Exception {
        NBTTagCompound compound = CompressedStreamTools.read(playerFile); // IO-Exc thrown only here.
        load_unsafeFromNBT(pUUID, compound);
    }

    private static void load_unsafeFromNBT(UUID pUUID, @Nullable NBTTagCompound compound) {
        PlayerProgress progress = new PlayerProgress();
        if (compound != null && !compound.hasNoTags()) {
            progress.load(compound);
        }
        progress.forceGainResearch(ResearchProgression.DISCOVERY);

        playerProgressServer.put(pUUID, progress);
    }

    public static File getPlayerFile(EntityPlayer player) {
        return getPlayerFile(player.getUniqueID());
    }

    public static File getPlayerFile(UUID pUUID) {
        File f = new File(getPlayerDirectory(), pUUID.toString() + ".astral");
        if (!f.exists()) {
            try {
                CompressedStreamTools.write(new NBTTagCompound(), f);
            } catch (IOException ignored) {
            } // Will be created later anyway... just as fail-safe.
        }
        return f;
    }

    public static boolean doesPlayerFileExist(EntityPlayer player) {
        return new File(
            getPlayerDirectory(),
            player.getUniqueID()
                .toString() + ".astral").exists();
    }

    public static File getPlayerBackupFile(EntityPlayer player) {
        return getPlayerBackupFile(player.getUniqueID());
    }

    public static File getPlayerBackupFile(UUID pUUID) {
        File f = new File(getPlayerDirectory(), pUUID.toString() + ".astralback");
        if (!f.exists()) {
            try {
                CompressedStreamTools.write(new NBTTagCompound(), f);
            } catch (IOException ignored) {
            } // Will be created later anyway... just as fail-safe.
        }
        return f;
    }

    private static File getPlayerDirectory() {
        MinecraftServer server = FMLCommonHandler.instance()
            .getMinecraftServerInstance();
        File worldDir = new File(server.anvilFile, server.getFolderName());
        File pDir = new File(worldDir, "playerdata");
        if (!pDir.exists()) {
            pDir.mkdirs();
        }
        return pDir;
    }

    public static void saveAndClearServerCache() {
        ResearchIOThread.saveAllPending();
        playerProgressServer.clear();
    }

    @SideOnly(Side.CLIENT)
    public static void recieveProgressFromServer(PktSyncKnowledge message, EntityPlayer player) {
        int currentLvl = clientProgress == null ? 0 : clientProgress.getPerkLevel(player);
        clientProgress = new PlayerProgress();
        clientProgress.receive(message);
        clientInitialized = true;
        if (clientProgress.getPerkLevel(player) > currentLvl) {
            showBar();
        }
    }

    @SideOnly(Side.CLIENT)
    private static void showBar() {
        ClientRenderEventHandler.requestPermChargeReveal(160);
    }

    public static void informCraftingGridCompletion(EntityPlayer player, ItemStack out) {
        Item iOut = out.getItem();
        informCraft(player, out, iOut, Block.getBlockFromItem(iOut));
    }

    public static void informCraftingInfusionCompletion(TileStarlightInfuser infuser, ActiveInfusionTask recipe) {
        EntityPlayer crafter = recipe.tryGetCraftingPlayerServer();
        if (crafter == null) {
            AstralSorcery.log.warn("Infusion finished, player that initialized crafting could not be found!");
            AstralSorcery.log
                .warn("Affected tile: " + infuser.getPos() + " in dim " + infuser.getWorld().provider.getDimension());
            return;
        }

        ItemStack out = recipe.getRecipeToCraft()
            .getOutput(infuser);
        Item iOut = out.getItem();

        informCraft(crafter, out, iOut, Block.getBlockFromItem(iOut));
    }

    public static void informCraftingAltarCompletion(TileAltar altar, ActiveCraftingTask recipeToCraft) {
        EntityPlayer crafter = recipeToCraft.tryGetCraftingPlayerServer();
        if (crafter == null || !(crafter instanceof EntityPlayerMP)) {
            AstralSorcery.log.warn("Crafting finished, player that initialized crafting could not be found!");
            AstralSorcery.log
                .warn("Affected tile: " + altar.getPos() + " in dim " + altar.getWorld().provider.getDimension());
            return;
        }

        ItemStack out = recipeToCraft.getRecipeToCraft()
            .getOutputForMatching();
        Item iOut = out.getItem();

        informCraft(crafter, out, iOut, Block.getBlockFromItem(iOut));

        AdvancementTriggers.ALTAR_CRAFT.trigger((EntityPlayerMP) crafter, recipeToCraft.getRecipeToCraft());
    }

    private static void informCraft(EntityPlayer crafter, ItemStack crafted, Item itemCrafted, @Nullable Block iBlock) {
        if (iBlock != null) {
            if (iBlock instanceof BlockAltar) {
                giveProgressionIgnoreFail(crafter, ProgressionTier.BASIC_CRAFT);
                giveResearchIgnoreFail(crafter, ResearchProgression.BASIC_CRAFT);

                TileAltar.AltarLevel to = TileAltar.AltarLevel.values()[crafted.getItemDamage()];
                switch (to) {
                    case ATTUNEMENT:
                        giveProgressionIgnoreFail(crafter, ProgressionTier.ATTUNEMENT);
                        giveResearchIgnoreFail(crafter, ResearchProgression.ATTUNEMENT);
                        break;
                    case CONSTELLATION_CRAFT:
                        giveProgressionIgnoreFail(crafter, ProgressionTier.CONSTELLATION_CRAFT);
                        giveResearchIgnoreFail(crafter, ResearchProgression.CONSTELLATION);
                        break;
                    case TRAIT_CRAFT:
                        giveProgressionIgnoreFail(crafter, ProgressionTier.TRAIT_CRAFT);
                        giveResearchIgnoreFail(crafter, ResearchProgression.RADIANCE);
                        break;
                    case BRILLIANCE:
                        giveProgressionIgnoreFail(crafter, ProgressionTier.BRILLIANCE);
                        giveResearchIgnoreFail(crafter, ResearchProgression.BRILLIANCE);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private static void informPlayersAboutProgressionLoss(UUID pUUID) {
        MinecraftServer server = FMLCommonHandler.instance()
            .getMinecraftServerInstance();
        if (server != null) {
            EntityPlayerMP player = server.getPlayerList()
                .getPlayerByUUID(pUUID);
            if (player != null) {
                player.sendMessage(
                    new TextComponentString(
                        "AstralSorcery: Your progression could not be loaded and can't be recovered from backup. Please contact an administrator to lookup what went wrong and/or potentially recover your data from a backup.")
                        .setStyle(new Style().setColor(TextFormatting.RED)));
            }
            String resolvedName = player != null ? player.getName() : pUUID.toString() + " (Not online)";
            for (String opName : server.getPlayerList()
                .getOppedPlayerNames()) {
                EntityPlayer pl = server.getPlayerList()
                    .getPlayerByUsername(opName);
                if (pl != null) {
                    pl.sendMessage(
                        new TextComponentString(
                            "AstralSorcery: The progression of " + resolvedName
                                + " could not be loaded and can't be recovered from backup. Error files might be created from the unloadable progression files, check the console for additional information!")
                            .setStyle(new Style().setColor(TextFormatting.RED)));
                }
            }
        }
    }

}
