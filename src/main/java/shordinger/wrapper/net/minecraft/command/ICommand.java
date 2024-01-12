package shordinger.wrapper.net.minecraft.command;

import java.util.List;

import javax.annotation.Nullable;

import cpw.mods.fml.server.FMLServerHandler;
import shordinger.wrapper.net.minecraft.server.MinecraftServer;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;

public interface ICommand extends net.minecraft.command.ICommand {

    /**
     * Gets the name of the command
     */
    String getName();

    default String getCommandName() {
        return getName();
    }

    /**
     * Gets the usage string for the command.
     */
    String getUsage(ICommandSender sender);

    default String getCommandUsage(net.minecraft.command.ICommandSender sender) {
        return getUsage((ICommandSender) sender);
    }

    /**
     * Get a list of aliases for this command. <b>Never return null!</b>
     */
    List<String> getAliases();

    default List getCommandAliases() {
        return getAliases();
    }

    /**
     * Callback for when the command is executed
     */
    void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException;

    default void processCommand(net.minecraft.command.ICommandSender sender, String[] args) throws CommandException {
        execute((MinecraftServer) FMLServerHandler.instance().getServer(), (ICommandSender) sender, args);
    }

    /**
     * Check if the given ICommandSender has permission to execute this command
     */
    boolean checkPermission(MinecraftServer server, ICommandSender sender);


    default boolean canCommandSenderUseCommand(net.minecraft.command.ICommandSender sender) {
        return checkPermission((MinecraftServer) FMLServerHandler.instance().getServer(), (ICommandSender) sender);
    }

    /**
     * Get a list of options for when the user presses the TAB key
     */
    List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
                                   @Nullable BlockPos targetPos);

}
