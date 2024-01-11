/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.server;

import shordinger.astralsorcery.client.ClientProxy;
import io.netty.buffer.ByteBuf;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktFinalizeLogin
 * Created by HellFirePvP
 * Date: 28.01.2018 / 19:36
 */
public class PktFinalizeLogin implements IMessage, IMessageHandler<PktFinalizeLogin, IMessage> {

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    @Override
    public IMessage onMessage(PktFinalizeLogin message, MessageContext ctx) {
        finalizeClientLogin();
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void finalizeClientLogin() {
        ClientProxy.connected = true;
    }

}
