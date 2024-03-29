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

package shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl;

import com.google.common.base.Preconditions;

import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import shordinger.wrapper.net.minecraft.network.INetHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.FMLLog;
import shordinger.wrapper.net.minecraftforge.fml.common.network.FMLOutboundHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.network.NetworkRegistry;

public class SimpleChannelHandlerWrapper<REQ extends IMessage, REPLY extends IMessage>
    extends SimpleChannelInboundHandler<REQ> {

    private final IMessageHandler<? super REQ, ? extends REPLY> messageHandler;
    private final Side side;

    public SimpleChannelHandlerWrapper(Class<? extends IMessageHandler<? super REQ, ? extends REPLY>> handler,
                                       Side side, Class<REQ> requestType) {
        this(SimpleNetworkWrapper.instantiate(handler), side, requestType);
    }

    public SimpleChannelHandlerWrapper(IMessageHandler<? super REQ, ? extends REPLY> handler, Side side,
                                       Class<REQ> requestType) {
        super(requestType);
        messageHandler = Preconditions.checkNotNull(handler, "IMessageHandler must not be null");
        this.side = side;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, REQ msg) throws Exception {
        INetHandler iNetHandler = ctx.channel()
            .attr(NetworkRegistry.NET_HANDLER)
            .get();
        MessageContext context = new MessageContext(iNetHandler, side);
        REPLY result = messageHandler.onMessage(msg, context);
        if (result != null) {
            ctx.channel()
                .attr(FMLOutboundHandler.FML_MESSAGETARGET)
                .set(FMLOutboundHandler.OutboundTarget.REPLY);
            ctx.writeAndFlush(result)
                .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        FMLLog.log.error("SimpleChannelHandlerWrapper exception", cause);
        super.exceptionCaught(ctx, cause);
    }
}
