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

package shordinger.wrapper.net.minecraftforge.common.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import shordinger.wrapper.net.minecraft.world.DimensionType;
import shordinger.wrapper.net.minecraftforge.common.DimensionManager;
import shordinger.wrapper.net.minecraftforge.common.network.ForgeMessage.DimensionRegisterMessage;
import shordinger.wrapper.net.minecraftforge.fml.common.FMLLog;

public class DimensionMessageHandler extends SimpleChannelInboundHandler<ForgeMessage.DimensionRegisterMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DimensionRegisterMessage msg) throws Exception {
        if (!DimensionManager.isDimensionRegistered(msg.dimensionId)) {
            DimensionManager.registerDimension(msg.dimensionId, DimensionType.valueOf(msg.providerId));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        FMLLog.log.error("DimensionMessageHandler exception", cause);
        super.exceptionCaught(ctx, cause);
    }

}
