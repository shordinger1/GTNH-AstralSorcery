/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.server;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.constellation.charge.PlayerChargeHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSyncCharge
 * Created by HellFirePvP
 * Date: 06.04.2017 / 11:29
 */
public class PktSyncCharge implements IMessage, IMessageHandler<PktSyncCharge, IMessage> {

    public float charge = 1F;

    public PktSyncCharge() {
    }

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
