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

package shordinger.wrapper.net.minecraftforge.fml.common.network.internal;

import com.google.common.base.Splitter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import shordinger.wrapper.net.minecraftforge.fml.common.FMLLog;
import shordinger.wrapper.net.minecraftforge.fml.common.network.ByteBufUtils;
import shordinger.wrapper.net.minecraftforge.fml.common.network.FMLIndexedMessageToMessageCodec;
import shordinger.wrapper.net.minecraftforge.fml.common.network.FMLNetworkException;

public class FMLRuntimeCodec extends FMLIndexedMessageToMessageCodec<FMLMessage> {

    public FMLRuntimeCodec() {
        addDiscriminator(0, FMLMessage.CompleteHandshake.class);
        addDiscriminator(1, FMLMessage.OpenGui.class);
        addDiscriminator(2, FMLMessage.EntitySpawnMessage.class);
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, FMLMessage msg, ByteBuf target) throws Exception {
        msg.toBytes(target);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, FMLMessage msg) {
        msg.fromBytes(source);
    }

    @Override
    protected void testMessageValidity(FMLProxyPacket msg) {
        if (msg.payload()
            .getByte(0) == 0
            && msg.payload()
            .readableBytes() > 2) {
            FMLLog.log.fatal(
                "The connection appears to have sent an invalid FML packet of type 0, this is likely because it think's it's talking to 1.6.4 FML");
            FMLLog.log.info("Bad data :");
            for (String l : Splitter.on('\n')
                .split(ByteBufUtils.getContentDump(msg.payload()))) {
                FMLLog.log.info("\t{}", l);
            }
            throw new FMLNetworkException("Invalid FML packet");
        }
    }
}
