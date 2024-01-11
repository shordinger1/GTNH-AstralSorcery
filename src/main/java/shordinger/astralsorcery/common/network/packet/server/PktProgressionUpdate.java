/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.server;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.gui.GuiJournalPerkTree;
import shordinger.astralsorcery.client.gui.GuiJournalProgression;
import shordinger.astralsorcery.client.gui.journal.GuiScreenJournal;
import shordinger.astralsorcery.common.data.research.ProgressionTier;
import shordinger.astralsorcery.common.data.research.ResearchProgression;
import io.netty.buffer.ByteBuf;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.gui.GuiScreen;
import shordinger.wrapper.net.minecraft.client.resources.I18n;
import shordinger.wrapper.net.minecraft.util.text.TextComponentString;
import shordinger.wrapper.net.minecraft.util.text.TextFormatting;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktProgressionUpdate
 * Created by HellFirePvP
 * Date: 24.10.2016 / 23:54
 */
public class PktProgressionUpdate implements IMessage, IMessageHandler<PktProgressionUpdate, IMessage> {

    public int tier = -1;
    public boolean isProg = false;
    public boolean isPresent = false;

    public PktProgressionUpdate() {
        isPresent = false;
    }

    public PktProgressionUpdate(ResearchProgression prog) {
        this.tier = prog.getProgressId();
        this.isPresent = true;
    }

    public PktProgressionUpdate(ProgressionTier tier) {
        this.isProg = true;
        this.tier = tier.ordinal();
        this.isPresent = true;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.tier = buf.readInt();
        this.isProg = buf.readBoolean();
        this.isPresent = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(tier);
        buf.writeBoolean(isProg);
        buf.writeBoolean(isPresent);
    }

    @Override
    public IMessage onMessage(PktProgressionUpdate message, MessageContext ctx) {
        AstralSorcery.proxy.scheduleClientside(() -> {
            if(message.isPresent) {
                if(message.isProg) {
                    addProgressChatMessage();
                } else {
                    addResearchChatMessage(message.tier);
                }
            }
            closeAndRefreshJournal();
        });
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void closeAndRefreshJournal() {
        GuiScreen open = Minecraft.getMinecraft().currentScreen;
        if(open != null) {
            if(open instanceof GuiScreenJournal && !(open instanceof GuiJournalPerkTree)) {
                Minecraft.getMinecraft().displayGuiScreen(null);
            }
        }

        GuiJournalProgression.resetJournal();
    }

    @SideOnly(Side.CLIENT)
    private void addResearchChatMessage(int resId) {
        ResearchProgression prog = ResearchProgression.getById(resId);
        String tr = I18n.format(prog.getUnlocalizedName());
        String out = I18n.format("progress.gain.research.chat", tr);
        out = TextFormatting.AQUA + out;
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(out));
    }

    @SideOnly(Side.CLIENT)
    private void addProgressChatMessage() {
        String out = TextFormatting.BLUE + I18n.format("progress.gain.progress.chat");
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(out));
    }

}
