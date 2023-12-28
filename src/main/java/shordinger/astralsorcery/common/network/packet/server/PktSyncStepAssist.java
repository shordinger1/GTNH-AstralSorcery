/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.server;

import net.minecraft.client.Minecraft;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.AstralSorcery;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSyncStepAssist
 * Created by HellFirePvP
 * Date: 06.04.2017 / 23:44
 */
public class PktSyncStepAssist implements IMessage, IMessageHandler<PktSyncStepAssist, IMessage> {

    public float stepHeight;

    public PktSyncStepAssist() {
    }

    public PktSyncStepAssist(float stepHeight) {
        this.stepHeight = stepHeight - 0.4F; // FFS mojang
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.stepHeight = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(this.stepHeight);
    }

    @Override
    public IMessage onMessage(PktSyncStepAssist message, MessageContext ctx) {
        AstralSorcery.proxy.scheduleClientside(() -> apply(message.stepHeight));
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void apply(float stepHeight) {
        if (Minecraft.getMinecraft().player == null) {
            AstralSorcery.proxy.scheduleClientside(() -> apply(stepHeight), 4);
            return;
        }
        Minecraft.getMinecraft().player.stepHeight = stepHeight;
    }

}
