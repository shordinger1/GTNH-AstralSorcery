/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.server;

import java.awt.*;

import net.minecraft.client.Minecraft;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.effect.light.EffectLightning;
import shordinger.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktLightningEffect
 * Created by HellFirePvP
 * Date: 07.02.2017 / 18:40
 */
public class PktLightningEffect implements IMessage, IMessageHandler<PktLightningEffect, IMessage> {

    private Vector3 from, to;
    private Color colorOverlay = null;

    public PktLightningEffect() {
    }

    public PktLightningEffect(Vector3 from, Vector3 to) {
        this.from = from;
        this.to = to;
    }

    public PktLightningEffect setColorOverlay(Color colorOverlay) {
        this.colorOverlay = colorOverlay;
        return this;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        from = Vector3.fromBytes(buf);
        to = Vector3.fromBytes(buf);
        if (buf.readBoolean()) {
            float[] colorComponents = new float[4];
            for (int i = 0; i < colorComponents.length; i++) {
                colorComponents[i] = buf.readFloat();
            }
            colorOverlay = new Color(colorComponents[0], colorComponents[1], colorComponents[2], colorComponents[3]);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        from.toBytes(buf);
        to.toBytes(buf);
        buf.writeBoolean(colorOverlay != null);
        if (colorOverlay != null) {
            for (float color : colorOverlay.getComponents(new float[4])) {
                buf.writeFloat(color);
            }
        }
    }

    @Override
    public IMessage onMessage(PktLightningEffect message, MessageContext ctx) {
        AstralSorcery.proxy.scheduleClientside(() -> playLightningEffect(message));
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void playLightningEffect(PktLightningEffect p) {
        Minecraft.getMinecraft()
            .addScheduledTask(() -> {
                EffectLightning lightning = EffectHandler.getInstance()
                    .lightning(p.from, p.to);
                if (p.colorOverlay != null) {
                    lightning.setOverlayColor(p.colorOverlay);
                }
            });
    }

}
