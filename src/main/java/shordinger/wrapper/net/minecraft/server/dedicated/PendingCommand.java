package shordinger.wrapper.net.minecraft.server.dedicated;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.command.ICommandSender;

@SideOnly(Side.SERVER)
public class PendingCommand {

    /**
     * The command string.
     */
    public final String command;
    public final ICommandSender sender;

    public PendingCommand(String input, ICommandSender sender) {
        this.command = input;
        this.sender = sender;
    }
}
