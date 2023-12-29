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
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.tile.TileGrindstone;
import shordinger.astralsorcery.common.util.BlockBreakAssist;
import shordinger.astralsorcery.common.util.ByteBufUtils;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktPlayEntityEffect
 * Created by HellFirePvP
 * Date: 10.11.2016 / 12:47
 */
public class PktPlayEffect implements IMessage, IMessageHandler<PktPlayEffect, IMessage> {

    private byte typeOrdinal;
    public int data = 0;
    public BlockPos pos;

    public PktPlayEffect() {
    }

    public PktPlayEffect(EffectType type, BlockPos pos) {
        this.typeOrdinal = (byte) type.ordinal();
        this.pos = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.typeOrdinal = buf.readByte();
        this.pos = ByteBufUtils.readPos(buf);
        this.data = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(typeOrdinal);
        ByteBufUtils.writePos(buf, pos);
        buf.writeInt(data);
    }

    @Override
    public IMessage onMessage(PktPlayEffect message, MessageContext ctx) {
        try {
            EffectType type = EffectType.values()[message.typeOrdinal];
            EventAction trigger = type.getTrigger(ctx.side);
            if (trigger != null) {
                AstralSorcery.proxy.scheduleClientside(() -> trigger.trigger(message));
            }
        } catch (Exception exc) {
            AstralSorcery.log
                .warn("Error executing ParticleEventType " + message.typeOrdinal + " for pos " + pos.toString());
        }
        return null;
    }

    public static enum EffectType {

        // DEFINE EVENT TRIGGER IN THE FCKING HUGE SWITCH STATEMENT DOWN TEHRE.
        GRINDSTONE_WHEEL,
        BEAM_BREAK;

        // GOD I HATE THIS PART
        // But i can't do this in the ctor because server-client stuffs.
        @SideOnly(Side.CLIENT)
        private static EventAction getClientTrigger(EffectType type) {
            switch (type) {
                case GRINDSTONE_WHEEL:
                    return TileGrindstone::playWheelAnimation;
                case BEAM_BREAK:
                    return BlockBreakAssist::blockBreakAnimation;
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

        public void trigger(PktPlayEffect event);

    }

}
