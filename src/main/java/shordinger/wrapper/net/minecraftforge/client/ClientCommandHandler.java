/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package shordinger.wrapper.net.minecraftforge.client;

import static net.minecraft.util.text.TextFormatting.*;

import java.util.List;

import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.gui.GuiChat;
import shordinger.wrapper.net.minecraft.command.CommandException;
import shordinger.wrapper.net.minecraft.command.CommandHandler;
import shordinger.wrapper.net.minecraft.command.ICommand;
import shordinger.wrapper.net.minecraft.command.ICommandSender;
import shordinger.wrapper.net.minecraft.command.WrongUsageException;
import shordinger.wrapper.net.minecraft.server.MinecraftServer;
import shordinger.wrapper.net.minecraft.util.text.TextComponentTranslation;
import shordinger.wrapper.net.minecraft.util.text.TextFormatting;
import shordinger.wrapper.net.minecraftforge.common.MinecraftForge;
import shordinger.wrapper.net.minecraftforge.event.CommandEvent;
import shordinger.wrapper.net.minecraftforge.fml.client.FMLClientHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.FMLLog;

/**
 * The class that handles client-side chat commands. You should register any
 * commands that you want handled on the client with this command handler.
 * <p>
 * If there is a command with the same name registered both on the server and
 * client, the client takes precedence!
 */
public class ClientCommandHandler extends CommandHandler {

    public static final ClientCommandHandler instance = new ClientCommandHandler();

    public String[] latestAutoComplete = null;

    /**
     * @return 1 if successfully executed, -1 if no permission or wrong usage,
     *         0 if it doesn't exist or it was canceled (it's sent to the server)
     */
    /**
     * Attempt to execute a command. This method should return the number of times that the command was executed. If the
     * command does not exist or if the player does not have permission, 0 will be returned. A number greater than 1 can
     * be returned if a player selector is used.
     */
    @Override
    public int executeCommand(ICommandSender sender, String message) {
        message = message.trim();

        boolean usedSlash = message.startsWith("/");
        if (usedSlash) {
            message = message.substring(1);
        }

        String[] temp = message.split(" ");
        String[] args = new String[temp.length - 1];
        String commandName = temp[0];
        System.arraycopy(temp, 1, args, 0, args.length);
        ICommand icommand = getCommands().get(commandName);

        try {
            if (icommand == null || (!usedSlash && icommand instanceof IClientCommand
                && !((IClientCommand) icommand).allowUsageWithoutPrefix(sender, message))) {
                return 0;
            }

            if (icommand.checkPermission(this.getServer(), sender)) {
                CommandEvent event = new CommandEvent(icommand, sender, args);
                if (MinecraftForge.EVENT_BUS.post(event)) {
                    if (event.getException() != null) {
                        throw event.getException();
                    }
                    return 0;
                }

                this.tryExecute(sender, args, icommand, message);
                return 1;
            } else {
                sender.sendMessage(format(RED, "commands.generic.permission"));
            }
        } catch (WrongUsageException wue) {
            sender.sendMessage(
                format(RED, "commands.generic.usage", format(RED, wue.getMessage(), wue.getErrorObjects())));
        } catch (CommandException ce) {
            sender.sendMessage(format(RED, ce.getMessage(), ce.getErrorObjects()));
        } catch (Throwable t) {
            sender.sendMessage(format(RED, "commands.generic.exception"));
            FMLLog.log.error("Command '{}' threw an exception:", message, t);
        }

        return -1;
    }

    // Couple of helpers because the mcp names are stupid and long...
    private TextComponentTranslation format(TextFormatting color, String str, Object... args) {
        TextComponentTranslation ret = new TextComponentTranslation(str, args);
        ret.getStyle()
            .setColor(color);
        return ret;
    }

    public void autoComplete(String leftOfCursor) {
        latestAutoComplete = null;

        if (leftOfCursor.charAt(0) == '/') {
            leftOfCursor = leftOfCursor.substring(1);

            Minecraft mc = FMLClientHandler.instance()
                .getClient();
            if (mc.currentScreen instanceof GuiChat) {
                List<String> commands = getTabCompletions(mc.player, leftOfCursor, mc.player.getPosition());
                if (!commands.isEmpty()) {
                    if (leftOfCursor.indexOf(' ') == -1) {
                        for (int i = 0; i < commands.size(); i++) {
                            commands.set(i, GRAY + "/" + commands.get(i) + RESET);
                        }
                    } else {
                        for (int i = 0; i < commands.size(); i++) {
                            commands.set(i, GRAY + commands.get(i) + RESET);
                        }
                    }

                    latestAutoComplete = commands.toArray(new String[commands.size()]);
                }
            }
        }
    }

    @Override
    protected MinecraftServer getServer() {
        return Minecraft.getMinecraft()
            .getIntegratedServer();
    }
}
