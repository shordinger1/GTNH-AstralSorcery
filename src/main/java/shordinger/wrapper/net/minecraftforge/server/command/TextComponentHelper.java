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

package shordinger.wrapper.net.minecraftforge.server.command;

import io.netty.channel.Channel;
import shordinger.wrapper.net.minecraft.command.ICommandSender;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.network.NetHandlerPlayServer;
import shordinger.wrapper.net.minecraft.network.NetworkManager;
import shordinger.wrapper.net.minecraft.util.text.TextComponentBase;
import shordinger.wrapper.net.minecraft.util.text.TextComponentString;
import shordinger.wrapper.net.minecraft.util.text.TextComponentTranslation;
import shordinger.wrapper.net.minecraft.util.text.translation.I18n;
import shordinger.wrapper.net.minecraftforge.fml.common.network.NetworkRegistry;

public class TextComponentHelper {

    private TextComponentHelper() {
    }

    /**
     * Detects when sending to a vanilla client and falls back to sending english,
     * since they don't have the lang data necessary to translate on the client.
     */
    public static TextComponentBase createComponentTranslation(ICommandSender sender, final String translation,
                                                               final Object... args) {
        if (isVanillaClient(sender)) {
            return new TextComponentString(I18n.translateToLocalFormatted(translation, args));
        }
        return new TextComponentTranslation(translation, args);
    }

    private static boolean isVanillaClient(ICommandSender sender) {
        if (sender instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) sender;
            NetHandlerPlayServer connection = playerMP.connection;
            if (connection != null) {
                NetworkManager netManager = connection.netManager;
                Channel channel = netManager.channel();
                return !channel.attr(NetworkRegistry.FML_MARKER)
                    .get();
            }
        }
        return false;
    }
}
