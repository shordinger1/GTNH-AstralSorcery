/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.client;

import shordinger.astralsorcery.client.gui.GuiJournalPerkTree;
import shordinger.astralsorcery.common.constellation.perk.AbstractPerk;
import shordinger.astralsorcery.common.constellation.perk.tree.PerkTree;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.item.useables.ItemPerkSeal;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.ClientReplyPacket;
import shordinger.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.gui.GuiScreen;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraftforge.fml.common.FMLCommonHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktRequestPerkSealAction
 * Created by HellFirePvP
 * Date: 18.09.2018 / 21:09
 */
public class PktRequestPerkSealAction implements IMessage, IMessageHandler<PktRequestPerkSealAction, IMessage>, ClientReplyPacket {

    private AbstractPerk perk;
    private boolean doSealing; //Make/true or break/false the seal

    public PktRequestPerkSealAction() {}

    public PktRequestPerkSealAction(AbstractPerk perk, boolean seal) {
        this.perk = perk;
        this.doSealing = seal;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        ResourceLocation key = new ResourceLocation(ByteBufUtils.readString(buf));
        this.perk = PerkTree.PERK_TREE.getPerk(key);
        this.doSealing = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeString(buf, this.perk.getRegistryName().toString());
        buf.writeBoolean(this.doSealing);
    }

    @Override
    public IMessage onMessage(PktRequestPerkSealAction pkt, MessageContext ctx) {
        if (ctx.side == Side.SERVER) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                if (pkt.perk != null) {
                    EntityPlayerMP p = ctx.getServerHandler().player;
                    if (pkt.doSealing) {
                        if (ItemPerkSeal.useSeal(p, true) &&
                                ResearchManager.applyPerkSeal(ctx.getServerHandler().player, pkt.perk)) {
                            if (!ItemPerkSeal.useSeal(p, false)) {
                                ResearchManager.breakPerkSeal(ctx.getServerHandler().player, pkt.perk);
                            }
                        }
                    } else {
                        if(ResearchManager.breakPerkSeal(ctx.getServerHandler().player, pkt.perk)) {
                            PktRequestPerkSealAction packet = new PktRequestPerkSealAction(pkt.perk, false);
                            PacketChannel.CHANNEL.sendTo(packet, p);
                        }
                    }
                }
            });
        } else {
            recClientBreak(pkt);
        }
        return null;
    }

    private void recClientBreak(PktRequestPerkSealAction pkt) {
        if (!pkt.doSealing) {
            GuiScreen current = Minecraft.getMinecraft().currentScreen;
            if(current != null && current instanceof GuiJournalPerkTree) {
                Minecraft.getMinecraft().addScheduledTask(() -> ((GuiJournalPerkTree) current).playSealBreakAnimation(pkt.perk));
            }
        }
    }
}
