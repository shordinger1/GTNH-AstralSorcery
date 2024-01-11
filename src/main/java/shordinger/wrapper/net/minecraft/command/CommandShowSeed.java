package shordinger.wrapper.net.minecraft.command;

import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.server.MinecraftServer;
import shordinger.wrapper.net.minecraft.util.text.TextComponentTranslation;
import shordinger.wrapper.net.minecraft.world.World;

public class CommandShowSeed extends CommandBase {

    /**
     * Check if the given ICommandSender has permission to execute this command
     */
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return server.isSinglePlayer() || super.checkPermission(server, sender);
    }

    /**
     * Gets the name of the command
     */
    public String getName() {
        return "seed";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel() {
        return 2;
    }

    /**
     * Gets the usage string for the command.
     */
    public String getUsage(ICommandSender sender) {
        return "commands.seed.usage";
    }

    /**
     * Callback for when the command is executed
     */
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        World world = (World) (sender instanceof EntityPlayer ? ((EntityPlayer) sender).world : server.getWorld(0));
        sender.sendMessage(new TextComponentTranslation("commands.seed.success", new Object[]{world.getSeed()}));
    }
}
