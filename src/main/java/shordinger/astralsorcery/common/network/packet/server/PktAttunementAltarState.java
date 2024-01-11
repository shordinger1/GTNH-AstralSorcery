/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.tile.TileAttunementAltar;
import shordinger.astralsorcery.common.util.ByteBufUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.common.DimensionManager;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktAttunementAltarState
 * Created by HellFirePvP
 * Date: 28.12.2016 / 01:53
 */
public class PktAttunementAltarState
    implements IMessage, IMessageHandler<PktAttunementAltarState, PktAttunementAltarState> {

    private int entityId = -1;
    private int worldId = -1;
    private BlockPos at = BlockPos.ORIGIN;
    private boolean started = false;

    public PktAttunementAltarState() {
    }

    public PktAttunementAltarState(int entityId, int worldId, BlockPos at) {
        this.entityId = entityId;
        this.worldId = worldId;
        this.at = at;
    }

    public PktAttunementAltarState(boolean started, int worldId, BlockPos at) {
        this.started = started;
        this.worldId = worldId;
        this.at = at;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        started = buf.readBoolean();
        worldId = buf.readInt();
        at = ByteBufUtils.readPos(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeBoolean(started);
        buf.writeInt(worldId);
        ByteBufUtils.writePos(buf, at);
    }

    @Override
    public PktAttunementAltarState onMessage(PktAttunementAltarState message, MessageContext ctx) {
        if (ctx.side == Side.SERVER) {
            if (message.started) {
                AstralSorcery.proxy.scheduleDelayed(() -> {
                    World w = DimensionManager.getWorld(message.worldId);
                    TileAttunementAltar ta = MiscUtils.getTileAt(w, message.at, TileAttunementAltar.class, true);
                    if (ta != null) {
                        EntityPlayer pl = ctx.getServerHandler().player;
                        ta.markPlayerStartCameraFlight(pl);
                    }
                });
            }
        } else {
            recClient(message);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void recClient(PktAttunementAltarState message) {
        World mcWorld = Minecraft.getMinecraft().world;
        if (mcWorld != null && mcWorld.provider.getDimension() == message.worldId
            && Minecraft.getMinecraft().player != null
            && Minecraft.getMinecraft().player.getEntityId() == message.entityId) {

            AstralSorcery.proxy.scheduleClientside(() -> {
                TileAttunementAltar ta = MiscUtils.getTileAt(mcWorld, message.at, TileAttunementAltar.class, true);
                if (ta != null) {
                    if (ta.tryStartCameraFlight()) {
                        PacketChannel.CHANNEL
                            .sendToServer(new PktAttunementAltarState(true, message.worldId, message.at));
                    }
                }
            });
        }
        PacketChannel.CHANNEL.sendToServer(new PktAttunementAltarState(false, -1, BlockPos.ORIGIN));
    }
}
