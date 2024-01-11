package shordinger.wrapper.net.minecraft.client.gui.chat;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.util.text.ChatType;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;

@SideOnly(Side.CLIENT)
public interface IChatListener {

    /**
     * Called whenever this listener receives a chat message, if this listener is registered to the given type in {@link
     * net.minecraft.client.gui.GuiIngame#chatListeners chatListeners}
     *
     * @param chatTypeIn The type of chat message
     * @param message    The chat message.
     */
    void say(ChatType chatTypeIn, ITextComponent message);
}
