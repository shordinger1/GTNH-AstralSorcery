/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.util.ByteBufUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktParticleDataEvent
 * Created by HellFirePvP
 * Date: 15.07.2017 / 14:44
 */
public class PktParticleDataEvent implements IMessage, IMessageHandler<PktParticleDataEvent, IMessage> {

    public double[] data;
    private double xCoord, yCoord, zCoord;
    public ParticleType effectType;

    public PktParticleDataEvent() {}

    public PktParticleDataEvent(ParticleType effectType, double xCoord, double yCoord, double zCoord, double... data) {
        this.effectType = effectType;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
        this.data = data;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int amt = buf.readInt();
        this.data = new double[amt];
        for (int i = 0; i < amt; i++) {
            this.data[i] = buf.readDouble();
        }
        this.xCoord = buf.readDouble();
        this.yCoord = buf.readDouble();
        this.zCoord = buf.readDouble();
        this.effectType = ByteBufUtils.readEnumValue(buf, ParticleType.class);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.data.length);
        for (double d : this.data) {
            buf.writeDouble(d);
        }
        buf.writeDouble(this.xCoord);
        buf.writeDouble(this.yCoord);
        buf.writeDouble(this.zCoord);
        ByteBufUtils.writeEnumValue(buf, this.effectType);
    }

    @Override
    public IMessage onMessage(PktParticleDataEvent message, MessageContext ctx) {
        try {
            EventAction trigger = message.effectType.getTrigger(ctx.side);
            if (trigger != null) {
                triggerClientside(trigger, message);
            }
        } catch (Exception exc) {
            AstralSorcery.log.warn(
                "Error executing ParticleEventType " + message.effectType
                    .name() + " at " + xCoord + ", " + yCoord + ", " + zCoord);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void triggerClientside(EventAction trigger, PktParticleDataEvent message) {
        if (Minecraft.getMinecraft().world == null) return;
        AstralSorcery.proxy.scheduleClientside(() -> trigger.trigger(message));
    }

    public Vector3 getVec() {
        return new Vector3(xCoord, yCoord, zCoord);
    }

    public static enum ParticleType {

        ;

        @SideOnly(Side.CLIENT)
        private static EventAction getClientTrigger(ParticleType type) {
            switch (type) {
                default:
                    break;
            }
            return null;
        }

        public EventAction getTrigger(Side side) {
            if (!side.isClient()) return null;
            return getClientTrigger(this);
        }

    }

    private static interface EventAction {

        public void trigger(PktParticleDataEvent event);

    }

}
