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

package shordinger.wrapper.net.minecraftforge.fml.common.event;

import shordinger.wrapper.net.minecraft.command.CommandHandler;
import shordinger.wrapper.net.minecraft.command.ICommand;
import shordinger.wrapper.net.minecraft.server.MinecraftServer;
import shordinger.wrapper.net.minecraftforge.fml.common.LoaderState.ModState;

/**
 * Called after {@link FMLServerAboutToStartEvent} and before {@link FMLServerStartedEvent}.
 * This event allows for customizations of the server, such as loading custom commands, perhaps customizing recipes or
 * other activities.
 *
 * @author cpw
 * @see net.minecraftforge.fml.common.Mod.EventHandler for how to subscribe to this event
 */
public class FMLServerStartingEvent extends FMLStateEvent {

    private MinecraftServer server;

    public FMLServerStartingEvent(Object... data) {
        super(data);
        this.server = (MinecraftServer) data[0];
    }

    @Override
    public ModState getModState() {
        return ModState.AVAILABLE;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public void registerServerCommand(ICommand command) {
        CommandHandler ch = (CommandHandler) getServer().getCommandManager();
        ch.registerCommand(command);
    }
}
