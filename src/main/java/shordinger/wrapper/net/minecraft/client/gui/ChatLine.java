package shordinger.wrapper.net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;

@SideOnly(Side.CLIENT)
public class ChatLine {

    /**
     * GUI Update Counter value this Line was created at
     */
    private final int updateCounterCreated;
    private final ITextComponent lineString;
    /**
     * int value to refer to existing Chat Lines, can be 0 which means unreferrable
     */
    private final int chatLineID;

    public ChatLine(int updateCounterCreatedIn, ITextComponent lineStringIn, int chatLineIDIn) {
        this.lineString = lineStringIn;
        this.updateCounterCreated = updateCounterCreatedIn;
        this.chatLineID = chatLineIDIn;
    }

    public ITextComponent getChatComponent() {
        return this.lineString;
    }

    public int getUpdatedCounter() {
        return this.updateCounterCreated;
    }

    public int getChatLineID() {
        return this.chatLineID;
    }
}
