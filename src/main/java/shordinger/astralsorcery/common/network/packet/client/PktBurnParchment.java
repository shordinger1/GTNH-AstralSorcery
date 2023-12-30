/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.gui.GuiMapDrawing;
import shordinger.astralsorcery.common.network.packet.ClientReplyPacket;
import shordinger.astralsorcery.common.tile.TileMapDrawingTable;
import shordinger.astralsorcery.common.util.ByteBufUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.migration.block.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktBurnParchment
 * Created by HellFirePvP
 * Date: 30.04.2017 / 22:20
 */
public class PktBurnParchment
    implements IMessage, IMessageHandler<PktBurnParchment, PktBurnParchment>, ClientReplyPacket {

    public int dimId;
    public BlockPos tablePos;

    public PktBurnParchment() {
    }

    public PktBurnParchment(int dimid, BlockPos tablePos) {
        this.tablePos = tablePos;
        this.dimId = dimid;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.tablePos = ByteBufUtils.readPos(buf);
        this.dimId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writePos(buf, this.tablePos);
        buf.writeInt(dimId);
    }

    @Override
    public PktBurnParchment onMessage(PktBurnParchment message, MessageContext ctx) {
        if (ctx.side == Side.SERVER) {
            World world = DimensionManager.getWorld(message.dimId);
            if (world != null) {
                TileMapDrawingTable tmt = MiscUtils
                    .getTileAt(world, message.tablePos, TileMapDrawingTable.class, false);
                if (tmt != null) {
                    if (tmt.burnParchment()) {
                        return new PktBurnParchment(-1, BlockPos.ORIGIN);
                    }
                }
            }
            return null;
        } else {
            closeTable();
            return null;
        }
    }

    @SideOnly(Side.CLIENT)
    private void closeTable() {
        if (Minecraft.getMinecraft().currentScreen != null
            && Minecraft.getMinecraft().currentScreen instanceof GuiMapDrawing) {
            AstralSorcery.proxy.scheduleClientside(
                () -> Minecraft.getMinecraft()
                    .displayGuiScreen(null));
        }
    }

}
