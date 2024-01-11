package shordinger.wrapper.net.minecraft.command.server;

import java.util.List;

import javax.annotation.Nullable;

import shordinger.wrapper.net.minecraft.command.CommandBase;
import shordinger.wrapper.net.minecraft.command.CommandException;
import shordinger.wrapper.net.minecraft.command.ICommandSender;
import shordinger.wrapper.net.minecraft.command.WrongUsageException;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.server.MinecraftServer;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;
import shordinger.wrapper.net.minecraft.util.text.TextComponentTranslation;

public class CommandEmote extends CommandBase {

    /**
     * Gets the name of the command
     */
    public String getName() {
        return "me";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel() {
        return 0;
    }

    /**
     * Gets the usage string for the command.
     */
    public String getUsage(ICommandSender sender) {
        return "commands.me.usage";
    }

    /**
     * Callback for when the command is executed
     */
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length <= 0) {
            throw new WrongUsageException("commands.me.usage", new Object[0]);
        } else {
            ITextComponent itextcomponent = getChatComponentFromNthArg(
                sender,
                args,
                0,
                !(sender instanceof EntityPlayer));
            server.getPlayerList()
                .sendMessage(
                    new TextComponentTranslation(
                        "chat.type.emote",
                        new Object[]{sender.getDisplayName(), itextcomponent}));
        }
    }

    /**
     * Get a list of options for when the user presses the TAB key
     */
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
                                          @Nullable BlockPos targetPos) {
        return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
    }
}
