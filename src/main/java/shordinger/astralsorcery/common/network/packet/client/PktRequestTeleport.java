/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.data.world.WorldCacheManager;
import shordinger.astralsorcery.common.data.world.data.GatewayCache;
import shordinger.astralsorcery.common.tile.TileCelestialGateway;
import shordinger.astralsorcery.common.util.ByteBufUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktRequestTeleport
 * Created by HellFirePvP
 * Date: 19.04.2017 / 14:11
 */
public class PktRequestTeleport implements IMessage, IMessageHandler<PktRequestTeleport, IMessage> {

    private int dimId;
    private BlockPos pos;

    public PktRequestTeleport() {
    }

    public PktRequestTeleport(int dimId, BlockPos pos) {
        this.dimId = dimId;
        this.pos = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.dimId = buf.readInt();
        this.pos = ByteBufUtils.readPos(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.dimId);
        ByteBufUtils.writePos(buf, this.pos);
    }

    @Override
    public IMessage onMessage(PktRequestTeleport message, MessageContext ctx) {

        FMLCommonHandler.instance()
            .getMinecraftServerInstance()
            .addScheduledTask(() -> {
                EntityPlayer request = ctx.getServerHandler().player;
                TileCelestialGateway gate = MiscUtils.getTileAt(
                    request.world,
                    Vector3.atEntityCorner(request)
                        .toBlockPos(),
                    TileCelestialGateway.class,
                    false);
                if (gate != null && gate.hasMultiblock() && gate.doesSeeSky()) {
                    MinecraftServer server = FMLCommonHandler.instance()
                        .getMinecraftServerInstance();
                    if (server != null) {
                        World to = server.getWorld(message.dimId);
                        if (to != null) {
                            GatewayCache data = WorldCacheManager
                                .getOrLoadData(to, WorldCacheManager.SaveKey.GATEWAY_DATA);
                            if (MiscUtils
                                .contains(data.getGatewayPositions(), gatewayNode -> gatewayNode.equals(message.pos))) {
                                AstralSorcery.proxy.scheduleDelayed(
                                    () -> MiscUtils.transferEntityTo(request, message.dimId, message.pos));
                            }
                        }
                    }
                }
            });
        return null;
    }
}
