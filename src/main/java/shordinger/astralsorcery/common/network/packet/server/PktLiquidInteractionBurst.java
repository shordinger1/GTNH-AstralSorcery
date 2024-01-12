/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.server;

import java.awt.*;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.effect.fx.EntityFXFloatingCube;
import shordinger.astralsorcery.client.util.RenderingUtils;
import shordinger.astralsorcery.common.util.ByteBufUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.renderer.texture.TextureAtlasSprite;
import shordinger.wrapper.net.minecraftforge.fluids.FluidStack;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktLiquidInteractionBurst
 * Created by HellFirePvP
 * Date: 28.10.2017 / 23:37
 */
public class PktLiquidInteractionBurst implements IMessageHandler<PktLiquidInteractionBurst, IMessage>, IMessage {

    private static Random rand = new Random();

    private FluidStack comp1, comp2;
    private Vector3 pos;

    public PktLiquidInteractionBurst() {}

    public PktLiquidInteractionBurst(FluidStack comp1, FluidStack comp2, Vector3 pos) {
        this.comp1 = comp1;
        this.comp2 = comp2;
        this.pos = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.comp1 = ByteBufUtils.readFluidStack(buf);
        this.comp2 = ByteBufUtils.readFluidStack(buf);
        this.pos = ByteBufUtils.readVector(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeFluidStack(buf, comp1);
        ByteBufUtils.writeFluidStack(buf, comp2);
        ByteBufUtils.writeVector(buf, pos);
    }

    @Override
    public IMessage onMessage(PktLiquidInteractionBurst message, MessageContext ctx) {
        AstralSorcery.proxy.scheduleClientside(() -> playClientEffect(message));
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void playClientEffect(PktLiquidInteractionBurst message) {
        if (Minecraft.getMinecraft().theWorld == null) return;

        TextureAtlasSprite tas1 = RenderingUtils.tryGetFlowingTextureOfFluidStack(message.comp1);

        for (int i = 0; i < 11 + rand.nextInt(3); i++) {
            EntityFXFloatingCube cube = RenderingUtils.spawnFloatingBlockCubeParticle(message.pos, tas1);
            cube.setTextureSubSizePercentage(1F / 16F)
                .setMaxAge(20 + rand.nextInt(20));
            cube.setWorldLightCoord(Minecraft.getMinecraft().theWorld, message.pos.toBlockPos());
            cube.setColorHandler(
                cb -> new Color(
                    message.comp1.getFluid()
                        .getColor(message.comp1)));
            cube.setScale(0.1F)
                .tumble()
                .setMotion(
                    rand.nextFloat() * 0.017F * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.017F * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.017F * (rand.nextBoolean() ? 1 : -1));
        }

        TextureAtlasSprite tas2 = RenderingUtils.tryGetFlowingTextureOfFluidStack(message.comp2);

        for (int i = 0; i < 11 + rand.nextInt(3); i++) {
            EntityFXFloatingCube cube = RenderingUtils.spawnFloatingBlockCubeParticle(message.pos, tas2);
            cube.setTextureSubSizePercentage(1F / 16F)
                .setMaxAge(20 + rand.nextInt(20));
            cube.setWorldLightCoord(Minecraft.getMinecraft().theWorld, message.pos.toBlockPos());
            cube.setColorHandler(
                cb -> new Color(
                    message.comp2.getFluid()
                        .getColor(message.comp2)));
            cube.setScale(0.1F)
                .tumble()
                .setMotion(
                    rand.nextFloat() * 0.027F * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.027F * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.027F * (rand.nextBoolean() ? 1 : -1));
        }
    }

}
