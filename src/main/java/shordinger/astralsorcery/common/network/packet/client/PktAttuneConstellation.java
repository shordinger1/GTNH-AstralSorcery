/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.client;

import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.common.constellation.ConstellationRegistry;
import shordinger.astralsorcery.common.constellation.IMajorConstellation;
import shordinger.astralsorcery.common.tile.TileAttunementAltar;
import shordinger.astralsorcery.common.util.ByteBufUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktAttuneConstellation
 * Created by HellFirePvP
 * Date: 19.12.2016 / 12:42
 */
public class PktAttuneConstellation implements IMessage, IMessageHandler<PktAttuneConstellation, IMessage> {

    public IMajorConstellation attunement = null;
    private int worldId = -1;
    private BlockPos at = BlockPos.ORIGIN;

    public PktAttuneConstellation() {
    }

    public PktAttuneConstellation(IMajorConstellation attunement, int worldId, BlockPos pos) {
        this.attunement = attunement;
        this.worldId = worldId;
        this.at = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.attunement = ConstellationRegistry.getMajorConstellationByName(ByteBufUtils.readString(buf));
        this.worldId = buf.readInt();
        this.at = ByteBufUtils.readPos(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeString(buf, attunement.getUnlocalizedName());
        buf.writeInt(worldId);
        ByteBufUtils.writePos(buf, at);
    }

    @Override
    public IMessage onMessage(PktAttuneConstellation message, MessageContext ctx) {
        IMajorConstellation cst = message.attunement;
        if (cst != null) {
            FMLCommonHandler.instance()
                .getMinecraftServerInstance()
                .addScheduledTask(() -> {
                    World w = DimensionManager.getWorld(message.worldId);
                    TileAttunementAltar ta = MiscUtils.getTileAt(w, message.at, TileAttunementAltar.class, false);
                    if (ta != null) {
                        ta.askForAttunement(ctx.getServerHandler().player, cst);
                    }
                });
        }
        return null;
    }
}
