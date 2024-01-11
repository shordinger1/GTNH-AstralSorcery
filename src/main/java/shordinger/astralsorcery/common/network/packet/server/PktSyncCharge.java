/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.server;

import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.constellation.charge.PlayerChargeHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSyncCharge
 * Created by HellFirePvP
 * Date: 06.04.2017 / 11:29
 */
public class PktSyncCharge implements IMessage, IMessageHandler<PktSyncCharge, IMessage> {

    public float charge = 1F;

    public PktSyncCharge() {}

    public PktSyncCharge(float charge) {
        this.charge = charge;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.charge = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(this.charge);
    }

    @Override
    public IMessage onMessage(PktSyncCharge message, MessageContext ctx) {
        AstralSorcery.proxy.scheduleClientside(() -> PlayerChargeHandler.INSTANCE.setClientCharge(message.charge));
        return null;
    }
}
