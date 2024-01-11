package shordinger.wrapper.net.minecraft.command.server;

import shordinger.wrapper.net.minecraft.command.CommandBase;
import shordinger.wrapper.net.minecraft.command.CommandException;
import shordinger.wrapper.net.minecraft.command.ICommandSender;
import shordinger.wrapper.net.minecraft.server.MinecraftServer;

public class CommandStop extends CommandBase {

    /**
     * Gets the name of the command
     */
    public String getName() {
        return "stop";
    }

    /**
     * Gets the usage string for the command.
     */
    public String getUsage(ICommandSender sender) {
        return "commands.stop.usage";
    }

    /**
     * Callback for when the command is executed
     */
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (server.worlds != null) {
            notifyCommandListener(sender, this, "commands.stop.start", new Object[0]);
        }

        server.initiateShutdown();
    }
}
