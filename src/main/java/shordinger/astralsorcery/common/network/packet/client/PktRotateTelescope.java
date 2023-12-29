/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.client.gui.GuiTelescope;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.ClientReplyPacket;
import shordinger.astralsorcery.common.tile.TileTelescope;
import shordinger.astralsorcery.common.util.ByteBufUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktRotateTelescope
 * Created by HellFirePvP
 * Date: 10.01.2017 / 00:54
 */
public class PktRotateTelescope
    implements IMessage, IMessageHandler<PktRotateTelescope, PktRotateTelescope>, ClientReplyPacket {

    private boolean isClockwise = false;
    private int dimId = -1;
    private BlockPos pos = BlockPos.ORIGIN;

    public PktRotateTelescope() {
    }

    public PktRotateTelescope(boolean isClockwise, int dimId, BlockPos pos) {
        this.isClockwise = isClockwise;
        this.dimId = dimId;
        this.pos = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.isClockwise = buf.readBoolean();
        this.pos = ByteBufUtils.readPos(buf);
        this.dimId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(isClockwise);
        ByteBufUtils.writePos(buf, pos);
        buf.writeInt(dimId);
    }

    @Override
    public PktRotateTelescope onMessage(PktRotateTelescope message, MessageContext ctx) {
        if (ctx.side == Side.SERVER) {
            FMLCommonHandler.instance()
                .getMinecraftServerInstance()
                .addScheduledTask(() -> {
                    if (DimensionManager.isDimensionRegistered(message.dimId)) {
                        WorldServer ws = DimensionManager.getWorld(message.dimId);
                        TileTelescope tt = MiscUtils.getTileAt(ws, message.pos, TileTelescope.class, false);
                        if (tt != null) {
                            tt.setRotation(
                                message.isClockwise ? tt.getRotation()
                                    .nextClockWise()
                                    : tt.getRotation()
                                    .nextCounterClockWise());
                            PktRotateTelescope pkt = new PktRotateTelescope(
                                message.isClockwise,
                                message.dimId,
                                message.pos);
                            PacketChannel.CHANNEL.sendTo(pkt, ctx.getServerHandler().player);
                        }
                    }
                });
        } else {
            applyRotation(message);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void applyRotation(PktRotateTelescope pkt) {
        if (Minecraft.getMinecraft().theWorld.provider.dimensionId == pkt.dimId) {
            TileTelescope tt = MiscUtils.getTileAt(Minecraft.getMinecraft().theWorld, pkt.pos, TileTelescope.class, false);
            if (tt != null) {
                tt.setRotation(
                    pkt.isClockwise ? tt.getRotation()
                        .nextClockWise()
                        : tt.getRotation()
                        .nextCounterClockWise());
            }
        }
        if (Minecraft.getMinecraft().currentScreen != null
            && Minecraft.getMinecraft().currentScreen instanceof GuiTelescope) {
            ((GuiTelescope) Minecraft.getMinecraft().currentScreen).handleRotationChange(pkt.isClockwise);
        }
    }

}
