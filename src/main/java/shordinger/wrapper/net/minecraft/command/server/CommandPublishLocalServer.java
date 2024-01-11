package shordinger.wrapper.net.minecraft.command.server;

import shordinger.wrapper.net.minecraft.command.CommandBase;
import shordinger.wrapper.net.minecraft.command.CommandException;
import shordinger.wrapper.net.minecraft.command.ICommandSender;
import shordinger.wrapper.net.minecraft.server.MinecraftServer;
import shordinger.wrapper.net.minecraft.world.GameType;

public class CommandPublishLocalServer extends CommandBase {

    /**
     * Gets the name of the command
     */
    public String getName() {
        return "publish";
    }

    /**
     * Gets the usage string for the command.
     */
    public String getUsage(ICommandSender sender) {
        return "commands.publish.usage";
    }

    /**
     * Callback for when the command is executed
     */
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        String s = server.shareToLAN(GameType.SURVIVAL, false);

        if (s != null) {
            notifyCommandListener(sender, this, "commands.publish.started", new Object[]{s});
        } else {
            notifyCommandListener(sender, this, "commands.publish.failed", new Object[0]);
        }
    }
}
