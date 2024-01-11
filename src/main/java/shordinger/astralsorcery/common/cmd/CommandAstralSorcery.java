/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.cmd;

import com.google.common.collect.Lists;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.auxiliary.StarlightNetworkDebugHandler;
import shordinger.astralsorcery.common.constellation.*;
import shordinger.astralsorcery.common.constellation.perk.AbstractPerk;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ProgressionTier;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.data.research.ResearchProgression;
import shordinger.astralsorcery.common.lib.MultiBlockArrays;
import shordinger.astralsorcery.common.migration.LegacyDataMigration;
import shordinger.astralsorcery.common.registry.RegistryStructures;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Tuple;
import shordinger.astralsorcery.common.structure.array.BlockArray;
import shordinger.wrapper.net.minecraft.command.CommandBase;
import shordinger.wrapper.net.minecraft.command.CommandException;
import shordinger.wrapper.net.minecraft.command.ICommandSender;
import shordinger.wrapper.net.minecraft.command.PlayerNotFoundException;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.server.MinecraftServer;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.RayTraceResult;
import shordinger.wrapper.net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CommandAstralSorcery
 * Created by HellFirePvP
 * Date: 07.05.2016 / 13:39
 */
public class CommandAstralSorcery extends CommandBase {

    private static final String[] COMMANDS = new String[]{
            "help",
            "constellations",
            "research",
            "progress",
            "reset",
            "exp",
            "attune",
            "build",
            "maximize",
            "slnetwork",
            "migrate-data"
    };

    private List<String> cmdAliases = new ArrayList<>();

    public CommandAstralSorcery() {
        this.cmdAliases.add("astralsorcery");
        this.cmdAliases.add("as");
    }

    @Override
    public String getName() {
        return "astralsorcery";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/astralsorcery <action> [player] [arguments...]";
    }

    @Override
    public List<String> getAliases() {
        return cmdAliases;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, COMMANDS);
        } else {
            String identifier = args[0].toLowerCase();
            if ("build".equals(identifier)) {
                Field[] fields = MultiBlockArrays.class.getDeclaredFields();
                List<String> names = new ArrayList<>(fields.length);

                for (Field f: fields) {
                    if (f.isAnnotationPresent(MultiBlockArrays.PasteBlacklist.class)) {
                        continue;
                    }
                    names.add(f.getName());
                }
                return getListOfStringsMatchingLastWord(args, names);
            } else if (args.length == 2) {
                return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
            } else if (args.length == 3) {
                switch (identifier) {
                    case "constellations": {
                        List<String> names = new ArrayList<>();
                        for (IConstellation c : ConstellationRegistry.getAllConstellations()) {
                            names.add(c.getUnlocalizedName());
                        }
                        names.add("all");
                        return getListOfStringsMatchingLastWord(args, names);
                    }
                    case "research": {
                        List<String> names = new ArrayList<>();
                        for (ResearchProgression r : ResearchProgression.values()) {
                            names.add(r.name());
                        }
                        names.add("all");
                        return getListOfStringsMatchingLastWord(args, names);
                    }
                    case "progress":
                        List<String> progressNames = new ArrayList<>();
                        progressNames.add("all");
                        progressNames.add("next");
                        return getListOfStringsMatchingLastWord(args, progressNames);
                    case "attune": {
                        List<String> names = new ArrayList<>();
                        for (IConstellation c : ConstellationRegistry.getMajorConstellations()) {
                            names.add(c.getUnlocalizedName());
                        }
                        return getListOfStringsMatchingLastWord(args, names);
                    }
                    default:
                        break;
                }

            }
        }
        return Collections.<String>emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(new TextComponentString("§cNot enough arguments."));
            sender.sendMessage(new TextComponentString("§cType \"/astralsorcery help\" for help"));
            return;
        }
        if (args.length >= 1) {
            String identifier = args[0];
            if ("help".equalsIgnoreCase(identifier)) {
                displayHelp(sender);
            } else if ("migrate-data".equalsIgnoreCase(identifier)) {
                migrateAllLegacyData(sender);
            } else if ("slnetwork".equalsIgnoreCase(identifier)) {
                tryEnterSLNetworkDebugMode(sender);
            } else if ("constellation".equalsIgnoreCase(identifier) || "constellations".equalsIgnoreCase(identifier)) {
                if (args.length == 1) {
                    listConstellations(sender);
                } else if (args.length == 2) {
                    listConstellations(server, sender, args[1]);
                } else if (args.length == 3) {
                    addConstellations(server, sender, args[1], args[2]);
                }
            } else if("research".equalsIgnoreCase(identifier) || "res".equalsIgnoreCase(identifier)) {
                if(args.length == 3) {
                    modifyResearch(server, sender, args[1], args[2]);
                }
            } else if ("progress".equalsIgnoreCase(identifier) || "prog".equalsIgnoreCase(identifier)) {
                if(args.length <= 2) {
                    showProgress(server, sender, args.length == 1 ? sender.getName() : args[1]);
                } else if(args.length == 3) {
                    modifyProgress(server, sender, args[1], args[2]);
                }
            } else if ("reset".equalsIgnoreCase(identifier)) {
                if (args.length == 2) {
                    wipeProgression(server, sender, args[1]);
                }
            } else if ("charge".equalsIgnoreCase(identifier) || "exp".equalsIgnoreCase(identifier)) {
                if (args.length == 3) {
                    setExp(server, sender, args[1], args[2]);
                }
            } else if ("attune".equalsIgnoreCase(identifier)) {
                if(args.length == 3) {
                    attuneToConstellation(server, sender, args[1], args[2]);
                }
            } else if ("build".equalsIgnoreCase(identifier)) {
                if(args.length == 2) {
                    buildStruct(sender, args[1]);
                } else {
                    RegistryStructures.init(); //Reload
                }
            } else if("maximize".equalsIgnoreCase(identifier)) {
                if (args.length == 2) {
                    maxAll(server, sender, args[1]);
                }
            }
        }
    }

    private void migrateAllLegacyData(ICommandSender sender) {
        LegacyDataMigration.migrateRockCrystalData(s -> sender.sendMessage(new TextComponentString(s)));

        sender.sendMessage(new TextComponentString("Data migration finished."));
    }

    private void tryEnterSLNetworkDebugMode(ICommandSender sender) {
        if(!(sender instanceof EntityPlayer)) {
            sender.sendMessage(new TextComponentString("This command can only be executed by a player!"));
            return;
        }

        EntityPlayer player = (EntityPlayer) sender;
        if (!player.isCreative()) {
            sender.sendMessage(new TextComponentString("§cYou have to be in creative-mode to use the debug mode!"));
            return;
        }
        StarlightNetworkDebugHandler.INSTANCE.awaitDebugInteraction(player, () -> sender.sendMessage(new TextComponentString("§cStarlight network debug-rightclick timed out.")));
        sender.sendMessage(new TextComponentString("§aRightclick a block within 20 seconds to collect information about its starlight network activity."));
    }

    private void attuneToConstellation(MinecraftServer server, ICommandSender sender, String otherPlayerName, String majorConstellationStr) {
        Tuple<EntityPlayerMP, PlayerProgress> prTuple = tryGetProgressWithMessages(server, sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        EntityPlayer other = prTuple.key;

        IMajorConstellation cst = ConstellationRegistry.getMajorConstellationByName(majorConstellationStr);
        if(cst == null) {
            sender.sendMessage(new TextComponentString("§cFailed! Given constellation name is not a (major) constellation! " + majorConstellationStr));
            sender.sendMessage(new TextComponentString("§cSee '/astralsorcery constellations' to get all constellations!"));
            return;
        }

        if(ResearchManager.setAttunedConstellation(other, cst)) {
            sender.sendMessage(new TextComponentString("§aSuccess! Player has been attuned to " + cst.getUnlocalizedName()));
        } else {
            sender.sendMessage(new TextComponentString("§cFailed! Player specified doesn't seem to have the research progress necessary!"));
        }
    }

    private void setExp(MinecraftServer server, ICommandSender sender, String otherPlayerName, String strCharge) {
        Tuple<EntityPlayerMP, PlayerProgress> prTuple = tryGetProgressWithMessages(server, sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        EntityPlayer other = prTuple.key;

        long chargeToSet;
        try {
            chargeToSet = Long.parseLong(strCharge);
        } catch (NumberFormatException exc) {
            sender.sendMessage(new TextComponentString("§cFailed! Alignment charge to set should be a number! " + strCharge));
            return;
        }

        if(ResearchManager.setExp(other, chargeToSet)) {
            sender.sendMessage(new TextComponentString("§aSuccess! Player charge has been set to " + chargeToSet));
        } else {
            sender.sendMessage(new TextComponentString("§cFailed! Player specified doesn't seem to have a research progress!"));
        }
    }

    private void modifyResearch(MinecraftServer server, ICommandSender sender, String otherPlayerName, String research) {
        Tuple<EntityPlayerMP, PlayerProgress> prTuple = tryGetProgressWithMessages(server, sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        EntityPlayerMP other = prTuple.key;

        if("all".equalsIgnoreCase(research)) {
            ResearchManager.forceMaximizeResearch(other);
            sender.sendMessage(new TextComponentString("§aSuccess!"));
        } else {
            ResearchProgression pr = ResearchProgression.getByEnumName(research);
            if(pr == null) {
                sender.sendMessage(new TextComponentString("§cFailed! Unknown research: " + research));
            } else {
                /*ProgressionTier pt = pr.getRequiredProgress();
                ResearchManager.giveProgressionIgnoreFail(other, pt);*/
                ResearchManager.unsafeForceGiveResearch(other, pr);
                sender.sendMessage(new TextComponentString("§aSuccess!"));
            }
        }
    }

    private void maxAll(MinecraftServer server, ICommandSender sender, String otherPlayerName) {
        Tuple<EntityPlayerMP, PlayerProgress> prTuple = tryGetProgressWithMessages(server, sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        EntityPlayer other = prTuple.key;

        ResearchManager.forceMaximizeAll(other);
        sender.sendMessage(new TextComponentString("§aSuccess!"));
    }

    private void buildStruct(ICommandSender sender, String name) {
        BlockArray array;
        try {
            Field f = MultiBlockArrays.class.getDeclaredField(name);
            f.setAccessible(true);
            if(f.isAnnotationPresent(MultiBlockArrays.PasteBlacklist.class)) {
                sender.sendMessage(new TextComponentString("§cFailed! You may not paste " + name + ", as it may be unstable or may have other unwanted effects!"));
                return;
            }
            array = (BlockArray) f.get(null);
        } catch (NoSuchFieldException e) {
            sender.sendMessage(new TextComponentString("§cFailed! " + name + " doesn't exist!"));
            return;
        } catch (IllegalAccessException e) {
            return; //doesn't happen
        }
        EntityPlayer exec;
        try {
            exec = getCommandSenderAsPlayer(sender);
        } catch (PlayerNotFoundException e) {
            sender.sendMessage(new TextComponentString("§cFailed! Couldn't find you as player in the world!"));
            return;
        }
        RayTraceResult res = MiscUtils.rayTraceLook(exec, 60);
        if(res == null) {
            sender.sendMessage(new TextComponentString("§cFailed! Couldn't find the block you're looking at?"));
            return;
        }
        BlockPos hit;
        switch (res.typeOfHit) {
            case BLOCK:
                hit = res.getBlockPos();
                break;
            case MISS:
            case ENTITY:
            default:
                sender.sendMessage(new TextComponentString("§cFailed! Couldn't find the block you're looking at?"));
                return;
        }
        sender.sendMessage(new TextComponentString("§aStarting to build " + name + " at " + hit.toString() + "!"));
        array.placeInWorld(exec.world, hit);
        sender.sendMessage(new TextComponentString("§aBuilt " + name + "!"));
    }

    private void wipeProgression(MinecraftServer server, ICommandSender sender, String otherPlayerName) {
        Tuple<EntityPlayerMP, PlayerProgress> prTuple = tryGetProgressWithMessages(server, sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        EntityPlayerMP other = prTuple.key;

        ResearchManager.wipeKnowledge(other);
        sender.sendMessage(new TextComponentString("§aWiped " + otherPlayerName + "'s data!"));
    }

    private void modifyProgress(MinecraftServer server, ICommandSender sender, String otherPlayerName, String argument) {
        Tuple<EntityPlayerMP, PlayerProgress> prTuple = tryGetProgressWithMessages(server, sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        PlayerProgress prog = prTuple.value;
        EntityPlayer other = prTuple.key;
        if("all".equalsIgnoreCase(argument)) {
            if(!ResearchManager.maximizeTier(other)) {
                sender.sendMessage(new TextComponentString("§cFailed! Could not load Progress for (" + otherPlayerName + ") !"));
            } else {
                sender.sendMessage(new TextComponentString("§aMaximized ProgressionTier for " + otherPlayerName + " !"));
            }
        } else if ("next".equalsIgnoreCase(argument)) {
            ProgressionTier tier = prog.getTierReached();
            if (!tier.hasNextTier()) {
                sender.sendMessage(new TextComponentString("§aPlayer " + otherPlayerName + " has already reached the highest tier!"));
            } else {
                ProgressionTier next = tier.next();
                ResearchManager.giveProgressionIgnoreFail(other, next);
                sender.sendMessage(new TextComponentString("§aPlayer " + otherPlayerName + " advanced to Tier " + next.name() + "!"));
            }
        }
    }

    private void showProgress(MinecraftServer server, ICommandSender sender, String otherPlayerName) {
        Tuple<EntityPlayerMP, PlayerProgress> prTuple = tryGetProgressWithMessages(server, sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        PlayerProgress progress = prTuple.value;
        EntityPlayer other = prTuple.key;

        sender.sendMessage(new TextComponentString("§aPlayer " + otherPlayerName + "'s research Data:"));

        sender.sendMessage(new TextComponentString("§aProgression tier: " + progress.getTierReached().name()));
        sender.sendMessage(new TextComponentString("§aAttuned to: " + (progress.getAttunedConstellation() == null ? "<none>" : progress.getAttunedConstellation().getUnlocalizedName())));
        sender.sendMessage(new TextComponentString("§aPerk-Exp: " + progress.getPerkExp() + " - As level: " + progress.getPerkLevel(other)));
        sender.sendMessage(new TextComponentString("§aUnlocked perks + unlock-level:"));
        for (AbstractPerk perk : progress.getAppliedPerks()) {
            sender.sendMessage(new TextComponentString("§7" + (perk.getUnlocalizedName() + ".name")));
        }
        sender.sendMessage(new TextComponentString("§aUnlocked research groups:"));
        StringBuilder sb = new StringBuilder();
        for (ResearchProgression rp : progress.getResearchProgression()) {
            if(sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(rp.name());
        }
        sender.sendMessage(new TextComponentString("§7" + sb.toString()));
        sender.sendMessage(new TextComponentString("§aUnlocked constellations:"));
        sb = new StringBuilder();
        for (String str : progress.getKnownConstellations()) {
            if(sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(str);
        }
        sender.sendMessage(new TextComponentString("§7" + sb.toString()));
    }

    private void addConstellations(MinecraftServer server, ICommandSender sender, String otherPlayerName, String argument) {
        Tuple<EntityPlayerMP, PlayerProgress> prTuple = tryGetProgressWithMessages(server, sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        EntityPlayer other = prTuple.key;
        if ("all".equals(argument)) {
            Collection<IConstellation> constellations = ConstellationRegistry.getAllConstellations();
            if (!ResearchManager.discoverConstellations(constellations, other)) {
                sender.sendMessage(new TextComponentString("§cFailed! Could not load Progress for (" + otherPlayerName + ") !"));
                return;
            }
            other.sendMessage(new TextComponentString("§aDiscovered all Constellations!"));
            sender.sendMessage(new TextComponentString("§aSuccess!"));
        } else {
            IConstellation c = ConstellationRegistry.getConstellationByName(argument);
            if (c == null) {
                sender.sendMessage(new TextComponentString("§cUnknown constellation: " + argument));
                return;
            }
            if (!ResearchManager.discoverConstellation(c, other)) {
                sender.sendMessage(new TextComponentString("§cFailed! Could not load Progress for (" + otherPlayerName + ") !"));
                return;
            }
            other.sendMessage(new TextComponentString("§aDiscovered constellation " + c.getUnlocalizedName() + "!"));
            sender.sendMessage(new TextComponentString("§aSuccess!"));
        }
    }

    private void listConstellations(MinecraftServer server, ICommandSender sender, String otherPlayerName) {
        Tuple<EntityPlayerMP, PlayerProgress> prTuple = tryGetProgressWithMessages(server, sender, otherPlayerName);
        if (prTuple == null) {
            return;
        }
        PlayerProgress progress = prTuple.value;
        sender.sendMessage(new TextComponentString("§c" + otherPlayerName + " has discovered the constellations:"));
        if (progress.getKnownConstellations().size() == 0) {
            sender.sendMessage(new TextComponentString("§c NONE"));
            return;
        }
        for (String s : progress.getKnownConstellations()) {
            sender.sendMessage(new TextComponentString("§7" + s));
        }
    }

    private Tuple<EntityPlayerMP, PlayerProgress> tryGetProgressWithMessages(MinecraftServer server, ICommandSender sender, String otherPlayerName) {
        EntityPlayerMP other;
        try {
            other = getPlayer(server, sender, otherPlayerName);
        } catch (CommandException e) {
            sender.sendMessage(new TextComponentString("§cSpecified player (" + otherPlayerName + ") is not online!"));
            return null;
        }
        PlayerProgress progress = ResearchManager.getProgress(other);
        if (!progress.isValid()) {
            sender.sendMessage(new TextComponentString("§cCould not get Progress for (" + otherPlayerName + ") !"));
            return null;
        }
        return new Tuple<>(other, progress);
    }

    private void displayHelp(ICommandSender sender) {
        sender.sendMessage(new TextComponentString("§a/astralsorcery constellation§7 - lists all constellations"));
        sender.sendMessage(new TextComponentString("§a/astralsorcery constellation [playerName]§7 - lists all discovered constellations of the specified player if he/she is online"));
        sender.sendMessage(new TextComponentString("§a/astralsorcery constellation [playerName] <cName;all>§7 - player specified discovers the specified constellation or all or resets all"));
        sender.sendMessage(new TextComponentString("§a/astralsorcery progress [playerName]§7 - displays progress information about the player (Enter no player to view your own)"));
        sender.sendMessage(new TextComponentString("§a/astralsorcery progress [playerName] <all>§7 - maximize progression"));
        sender.sendMessage(new TextComponentString("§a/astralsorcery research [playerName] <research;all>§7 - set/add Research"));
        sender.sendMessage(new TextComponentString("§a/astralsorcery reset [playerName]§7 - resets all progression-related data for that player."));
        sender.sendMessage(new TextComponentString("§a/astralsorcery build [structure]§7 - builds the named structure wherever the player is looking at."));
        sender.sendMessage(new TextComponentString("§a/astralsorcery maximize [playerName]§7 - unlocks everything for that player."));
        sender.sendMessage(new TextComponentString("§a/astralsorcery exp [playerName] <exp>§7 - sets the perk exp for a player"));
        sender.sendMessage(new TextComponentString("§a/astralsorcery attune [playerName] <majorConstellationName>§7 - sets the attunement constellation for a player"));
        sender.sendMessage(new TextComponentString("§a/astralsorcery slnetwork§7 - Executing player enters StarlightNetwork debug mode for the next block"));
    }

    private void listConstellations(ICommandSender sender) {
        sender.sendMessage(new TextComponentString("§cMajor \"Bright\" Constellations:"));
        for (IMajorConstellation c : ConstellationRegistry.getMajorConstellations()) {
            sender.sendMessage(new TextComponentString("§7" + c.getUnlocalizedName()));
        }
        sender.sendMessage(new TextComponentString("§Weak \"Dim\" Constellations:"));
        for (IWeakConstellation c : ConstellationRegistry.getWeakConstellations()) {
            if(c instanceof IMajorConstellation) continue;
            sender.sendMessage(new TextComponentString("§7" + c.getUnlocalizedName()));
        }
        sender.sendMessage(new TextComponentString("§cMinor \"Faint\" Constellations:"));
        for (IMinorConstellation c : ConstellationRegistry.getMinorConstellations()) {
            sender.sendMessage(new TextComponentString("§7" + c.getUnlocalizedName()));
        }
    }

}
