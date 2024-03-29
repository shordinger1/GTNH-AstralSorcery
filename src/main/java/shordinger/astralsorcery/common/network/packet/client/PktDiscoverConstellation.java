/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.client;

import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.constellation.ConstellationRegistry;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.util.ByteBufUtils;
import shordinger.wrapper.net.minecraft.util.text.Style;
import shordinger.wrapper.net.minecraft.util.text.TextComponentTranslation;
import shordinger.wrapper.net.minecraft.util.text.TextFormatting;
import shordinger.wrapper.net.minecraftforge.fml.common.FMLCommonHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktDiscoverConstellation
 * Created by HellFirePvP
 * Date: 12.05.2016 / 13:50
 */
public class PktDiscoverConstellation implements IMessage, IMessageHandler<PktDiscoverConstellation, IMessage> {

    private String discoveredConstellation;

    public PktDiscoverConstellation() {
    }

    public PktDiscoverConstellation(String discoveredConstellation) {
        this.discoveredConstellation = discoveredConstellation;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        discoveredConstellation = ByteBufUtils.readString(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeString(buf, discoveredConstellation);
    }

    @Override
    public IMessage onMessage(PktDiscoverConstellation message, MessageContext ctx) {
        FMLCommonHandler.instance()
            .getMinecraftServerInstance()
            .addScheduledTask(() -> {
                IConstellation received = ConstellationRegistry.getConstellationByName(message.discoveredConstellation);
                if (received == null) {
                    AstralSorcery.log
                        .info("Received unknown constellation from client: " + message.discoveredConstellation);
                } else {
                    PlayerProgress prog = ResearchManager.getProgress(ctx.getServerHandler().player, Side.SERVER);
                    if (prog.isValid() && received.canDiscover(ctx.getServerHandler().player, prog)) {
                        ResearchManager.discoverConstellation(received, ctx.getServerHandler().player);
                        ctx.getServerHandler().player.sendMessage(
                            new TextComponentTranslation(
                                "progress.discover.constellation.chat",
                                new TextComponentTranslation(message.discoveredConstellation)
                                    .setStyle(new Style().setColor(TextFormatting.GRAY)))
                                .setStyle(new Style().setColor(TextFormatting.BLUE)));
                    }
                }
            });
        return null;
    }

}
