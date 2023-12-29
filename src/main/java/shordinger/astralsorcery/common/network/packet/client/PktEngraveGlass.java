/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.client;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.common.constellation.ConstellationRegistry;
import shordinger.astralsorcery.common.constellation.DrawnConstellation;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.tile.TileMapDrawingTable;
import shordinger.astralsorcery.common.util.ByteBufUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktEngraveGlass
 * Created by HellFirePvP
 * Date: 01.05.2017 / 14:00
 */
public class PktEngraveGlass implements IMessage, IMessageHandler<PktEngraveGlass, IMessage> {

    public int dimId;
    public BlockPos pos;
    public List<DrawnConstellation> constellations = new LinkedList<>();

    public PktEngraveGlass() {
    }

    public PktEngraveGlass(int dimId, BlockPos pos, List<DrawnConstellation> constellations) {
        this.dimId = dimId;
        this.pos = pos;
        this.constellations = constellations;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.dimId = buf.readInt();
        this.pos = ByteBufUtils.readPos(buf);
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            String name = ByteBufUtils.readString(buf);
            int x = buf.readInt();
            int z = buf.readInt();
            IConstellation c = ConstellationRegistry.getConstellationByName(name);
            if (c != null) {
                this.constellations.add(new DrawnConstellation(new Point(x, z), c));
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.dimId);
        ByteBufUtils.writePos(buf, this.pos);
        buf.writeInt(this.constellations.size());
        for (DrawnConstellation c : this.constellations) {
            ByteBufUtils.writeString(buf, c.constellation.getUnlocalizedName());
            buf.writeInt(c.point.x);
            buf.writeInt(c.point.y);
        }
    }

    @Override
    public IMessage onMessage(PktEngraveGlass message, MessageContext ctx) {
        FMLCommonHandler.instance()
            .getMinecraftServerInstance()
            .addScheduledTask(() -> {
                World w = DimensionManager.getWorld(message.dimId);
                if (w != null) {
                    TileMapDrawingTable tmt = MiscUtils.getTileAt(w, message.pos, TileMapDrawingTable.class, false);
                    if (tmt != null) {
                        List<DrawnConstellation> constellations = message.constellations;
                        if (!constellations.isEmpty()) {
                            tmt.tryEngraveGlass(constellations.subList(0, Math.min(3, constellations.size())));
                        }
                    }
                }
            });
        return null;
    }
}
