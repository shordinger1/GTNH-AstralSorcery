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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.util.IThreadListener;
import shordinger.wrapper.net.minecraftforge.fml.client.FMLClientHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.FMLCommonHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.FMLLog;
import shordinger.wrapper.net.minecraftforge.fml.common.network.NetworkRegistry;
import shordinger.wrapper.net.minecraftforge.fml.common.network.internal.FMLMessage.OpenGui;

public class OpenGuiHandler extends SimpleChannelInboundHandler<FMLMessage.OpenGui> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, final OpenGui msg) throws Exception {
        IThreadListener thread = FMLCommonHandler.instance()
            .getWorldThread(
                ctx.channel()
                    .attr(NetworkRegistry.NET_HANDLER)
                    .get());
        if (thread.isCallingFromMinecraftThread()) {
            process(msg);
        } else {
            thread.addScheduledTask(() -> OpenGuiHandler.this.process(msg));
        }
    }

    private void process(OpenGui msg) {
        EntityPlayer player = FMLClientHandler.instance()
            .getClient().player;
        player.openGui(msg.modId, msg.modGuiId, player.world, msg.x, msg.y, msg.z);
        player.openContainer.windowId = msg.windowId;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        FMLLog.log.error("OpenGuiHandler exception", cause);
        super.exceptionCaught(ctx, cause);
    }

}
