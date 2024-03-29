package shordinger.wrapper.net.minecraft.command.server;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import shordinger.wrapper.net.minecraft.command.CommandBase;
import shordinger.wrapper.net.minecraft.command.CommandException;
import shordinger.wrapper.net.minecraft.command.ICommandSender;
import shordinger.wrapper.net.minecraft.command.PlayerNotFoundException;
import shordinger.wrapper.net.minecraft.command.WrongUsageException;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.server.MinecraftServer;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;
import shordinger.wrapper.net.minecraft.util.text.TextComponentTranslation;
import shordinger.wrapper.net.minecraft.util.text.TextFormatting;

public class CommandMessage extends CommandBase {

    /**
     * Get a list of aliases for this command. <b>Never return null!</b>
     */
    public List<String> getAliases() {
        return Arrays.<String>asList("w", "msg");
    }

    /**
     * Gets the name of the command
     */
    public String getName() {
        return "tell";
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
        return "commands.message.usage";
    }

    /**
     * Callback for when the command is executed
     */
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            throw new WrongUsageException("commands.message.usage", new Object[0]);
        } else {
            EntityPlayer entityplayer = getPlayer(server, sender, args[0]);

            if (entityplayer == sender) {
                throw new PlayerNotFoundException("commands.message.sameTarget");
            } else {
                ITextComponent itextcomponent = getChatComponentFromNthArg(
                    sender,
                    args,
                    1,
                    !(sender instanceof EntityPlayer));
                TextComponentTranslation textcomponenttranslation = new TextComponentTranslation(
                    "commands.message.display.incoming",
                    new Object[]{sender.getDisplayName(), itextcomponent.createCopy()});
                TextComponentTranslation textcomponenttranslation1 = new TextComponentTranslation(
                    "commands.message.display.outgoing",
                    new Object[]{entityplayer.getDisplayName(), itextcomponent.createCopy()});
                textcomponenttranslation.getStyle()
                    .setColor(TextFormatting.GRAY)
                    .setItalic(Boolean.valueOf(true));
                textcomponenttranslation1.getStyle()
                    .setColor(TextFormatting.GRAY)
                    .setItalic(Boolean.valueOf(true));
                entityplayer.sendMessage(textcomponenttranslation);
                sender.sendMessage(textcomponenttranslation1);
            }
        }
    }

    /**
     * Get a list of options for when the user presses the TAB key
     */
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
                                          @Nullable BlockPos targetPos) {
        return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }
}
