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
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.ClientReplyPacket;
import shordinger.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.gui.GuiScreen;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.server.MinecraftServer;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraftforge.fml.common.FMLCommonHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktUnlockPerk
 * Created by HellFirePvP
 * Date: 12.12.2016 / 13:07
 */
public class PktUnlockPerk implements IMessage, IMessageHandler<PktUnlockPerk, PktUnlockPerk>, ClientReplyPacket {

    private AbstractPerk perk;

    private boolean serverAccept = false;

    public PktUnlockPerk() {}

    public PktUnlockPerk(boolean serverAccepted, AbstractPerk perk) {
        this.serverAccept = serverAccepted;
        this.perk = perk;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.serverAccept = buf.readBoolean();
        AbstractPerk perk = PerkTree.PERK_TREE.getPerk(ByteBufUtils.readResourceLocation(buf));
        if(perk != null) {
            this.perk = perk;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(serverAccept);
        ByteBufUtils.writeResourceLocation(buf, perk.getRegistryName());
    }

    @Override
    public PktUnlockPerk onMessage(PktUnlockPerk message, MessageContext ctx) {
        if(ctx.side == Side.SERVER) {
            MinecraftServer ms = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (ms != null) {
                ms.addScheduledTask(() -> {
                    EntityPlayerMP pl = ctx.getServerHandler().player;
                    if(pl != null) {
                        if(message.perk != null) {
                            AbstractPerk perk = message.perk;
                            PlayerProgress prog = ResearchManager.getProgress(pl, ctx.side);
                            if (!prog.hasPerkUnlocked(perk) && prog.isValid()) {
                                if (perk.mayUnlockPerk(prog, pl) && ResearchManager.applyPerk(pl, message.perk)) {
                                    PacketChannel.CHANNEL.sendTo(new PktUnlockPerk(true, message.perk), pl);
                                }
                            }
                        }
                    }
                });
            }
        } else {
            recUnlockResultClient(message);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void recUnlockResultClient(PktUnlockPerk message) {
        if (message.serverAccept) {
            AbstractPerk perk = message.perk;
            GuiScreen current = Minecraft.getMinecraft().currentScreen;
            if (current instanceof GuiJournalPerkTree) {
                Minecraft.getMinecraft().addScheduledTask(() -> ((GuiJournalPerkTree) current).playUnlockAnimation(perk));
            }
        }
    }

}
