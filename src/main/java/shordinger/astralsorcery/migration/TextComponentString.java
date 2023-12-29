package shordinger.astralsorcery.migration;

import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

public class TextComponentString extends TextComponentBase {

    private final String text;

    public TextComponentString(String msg) {
        this.text = msg;
    }

    public String getText() {
        return this.text;
    }

    public String getUnformattedComponentText() {
        return this.text;
    }

    @Override
    public IChatComponent setChatStyle(net.minecraft.util.ChatStyle ChatStyle) {
        return setChatStyle(ChatStyle);
    }

    @Override
    public ChatStyle getChatStyle() {
        return null;
    }

    @Override
    public String getUnformattedTextForChat() {
        return null;
    }

    public TextComponentString createCopy() {
        TextComponentString textcomponentstring = new TextComponentString(this.text);
        textcomponentstring.setChatStyle(
            this.getChatStyle()
                .createShallowCopy());

        for (IChatComponent itextcomponent : this.getSiblings()) {
            textcomponentstring.appendSibling(itextcomponent.createCopy());
        }

        return textcomponentstring;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (!(p_equals_1_ instanceof TextComponentString textcomponentstring)) {
            return false;
        } else {
            return this.text.equals(textcomponentstring.getText()) && super.equals(p_equals_1_);
        }
    }

    public String toString() {
        return "TextComponent{text='" + this.text
            + '\''
            + ", siblings="
            + this.siblings
            + ", ChatStyle="
            + this.getChatStyle()
            + '}';
    }
}
