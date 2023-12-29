/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.client;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.item.ItemBlockStorage;
import shordinger.astralsorcery.common.network.packet.ClientReplyPacket;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktClearBlockStorageStack
 * Created by HellFirePvP
 * Date: 30.04.2018 / 15:30
 */
public class PktClearBlockStorageStack
    implements IMessage, IMessageHandler<PktClearBlockStorageStack, IMessage>, ClientReplyPacket {

    public PktClearBlockStorageStack() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    @Override
    public IMessage onMessage(PktClearBlockStorageStack message, MessageContext ctx) {
        AstralSorcery.proxy.scheduleDelayed(() -> ItemBlockStorage.tryClearContainerFor(ctx.getServerHandler().player));
        return null;
    }

}
