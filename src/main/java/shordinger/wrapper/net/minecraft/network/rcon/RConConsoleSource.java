package shordinger.wrapper.net.minecraft.network.rcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.command.ICommandSender;
import shordinger.wrapper.net.minecraft.server.MinecraftServer;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;
import shordinger.wrapper.net.minecraft.world.World;

public class RConConsoleSource implements ICommandSender {

    /**
     * RCon string buffer for log.
     */
    private final StringBuffer buffer = new StringBuffer();
    private final MinecraftServer server;

    public RConConsoleSource(MinecraftServer serverIn) {
        this.server = serverIn;
    }

    /**
     * Get the name of this object. For players this returns their username
     */
    public String getName() {
        return "Rcon";
    }

    /**
     * Send a chat message to the CommandSender
     */
    public void sendMessage(ITextComponent component) {
        this.buffer.append(component.getUnformattedText());
    }

    /**
     * Returns {@code true} if the CommandSender is allowed to execute the command, {@code false} if not
     */
    public boolean canUseCommand(int permLevel, String commandName) {
        return true;
    }

    /**
     * Get the world, if available. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return
     * the overworld
     */
    public World getEntityWorld() {
        return this.server.getEntityWorld();
    }

    /**
     * Returns true if the command sender should be sent feedback about executed commands
     */
    public boolean sendCommandFeedback() {
        return true;
    }

    /**
     * Get the Minecraft server instance
     */
    public MinecraftServer getServer() {
        return this.server;
    }

    /**
     * Clears the RCon log
     */
    @SideOnly(Side.SERVER)
    public void resetLog() {
        this.buffer.setLength(0);
    }

    /**
     * Gets the contents of the RCon log
     */
    @SideOnly(Side.SERVER)
    public String getLogContents() {
        return this.buffer.toString();
    }
}
