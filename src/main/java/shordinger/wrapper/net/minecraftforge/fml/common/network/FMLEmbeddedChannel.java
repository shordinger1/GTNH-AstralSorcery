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

package shordinger.wrapper.net.minecraftforge.fml.common.network;

import java.util.Map.Entry;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraftforge.fml.common.Loader;
import shordinger.wrapper.net.minecraftforge.fml.common.ModContainer;
import shordinger.wrapper.net.minecraftforge.fml.common.network.FMLOutboundHandler.OutboundTarget;
import shordinger.wrapper.net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import shordinger.wrapper.net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

/**
 * Utility wrapper around {@link EmbeddedChannel}. Provides some convenience methods
 * associated with the specific needs of FML network handling.
 *
 * @author cpw
 */
public class FMLEmbeddedChannel extends EmbeddedChannel {

    public FMLEmbeddedChannel(String channelName, Side source, ChannelHandler... handlers) {
        this(
            Loader.instance()
                .activeModContainer(),
            channelName,
            source,
            handlers);
    }

    public FMLEmbeddedChannel(ModContainer container, String channelName, Side source, ChannelHandler... handlers) {
        super(handlers);
        this.attr(NetworkRegistry.FML_CHANNEL)
            .set(channelName);
        this.attr(NetworkRegistry.CHANNEL_SOURCE)
            .set(source);
        this.attr(NetworkRegistry.MOD_CONTAINER)
            .setIfAbsent(container);
        this.pipeline()
            .addFirst("fml:outbound", new FMLOutboundHandler());
    }

    /**
     * Utility method to generate a regular packet from a custom packet. Basically, it writes the packet through the
     * outbound side which should have a message to message codec present (such as
     * {@link FMLIndexedMessageToMessageCodec},
     * transforming from mod packets to standard {@link FMLProxyPacket}s.
     * <p>
     * This is mostly useful in cases where vanilla expects a packet, such as the TileEntity getDescriptionPacket.
     *
     * @param object The inbound packet
     * @return A Packet suitable for passing to vanilla network code.
     */
    public Packet<?> generatePacketFrom(Object object) {
        OutboundTarget outboundTarget = attr(FMLOutboundHandler.FML_MESSAGETARGET).getAndSet(OutboundTarget.NOWHERE);
        writeOutbound(object);
        Packet<?> pkt = (Packet<?>) outboundMessages().poll();
        attr(FMLOutboundHandler.FML_MESSAGETARGET).set(outboundTarget);
        return pkt;
    }

    @Nullable
    public String findChannelHandlerNameForType(Class<? extends ChannelHandler> type) {
        String targetName = null;
        for (Entry<String, ChannelHandler> entry : pipeline()) {
            if (type.isInstance(entry.getValue())) {
                targetName = entry.getKey();
                break;
            }
        }
        return targetName;
    }

    public void cleanAttributes() {
        this.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS)
            .set(null);
        this.attr(NetworkRegistry.NET_HANDLER)
            .set(null);
        this.attr(NetworkDispatcher.FML_DISPATCHER)
            .set(null);
    }
}
