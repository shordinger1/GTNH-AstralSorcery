/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.server;

import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.effect.fx.EntityFXFluidFountain;
import shordinger.astralsorcery.common.util.ByteBufUtils;
import shordinger.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktPlayLiquidSpring
 * Created by HellFirePvP
 * Date: 02.11.2017 / 16:17
 */
public class PktPlayLiquidSpring implements IMessageHandler<PktPlayLiquidSpring, IMessage>, IMessage {

    private FluidStack stack;
    private Vector3 vec;

    public PktPlayLiquidSpring() {
    }

    public PktPlayLiquidSpring(FluidStack stack, Vector3 vec) {
        this.stack = stack;
        this.vec = vec;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.vec = ByteBufUtils.readVector(buf);
        this.stack = ByteBufUtils.readFluidStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeVector(buf, vec);
        ByteBufUtils.writeFluidStack(buf, stack);
    }

    @Override
    public IMessage onMessage(PktPlayLiquidSpring message, MessageContext ctx) {
        message.playEffect();
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void playEffect() {
        AstralSorcery.proxy.scheduleClientside(() -> EntityFXFluidFountain.spawnAt(vec, stack));
    }

}
