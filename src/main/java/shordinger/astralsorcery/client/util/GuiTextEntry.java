/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;

import org.lwjgl.input.Keyboard;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiTextEntry
 * Created by HellFirePvP
 * Date: 15.07.2018 / 14:49
 */
public class GuiTextEntry {

    private String text = "";
    private Runnable changeCallback = null;

    public void setChangeCallback(Runnable changeCallback) {
        this.changeCallback = changeCallback;
    }

    public void setText(@Nullable String newText) {
        if (newText == null) {
            newText = "";
        }
        String prevText = this.text;
        this.text = newText;
        if (!newText.equals(prevText) && changeCallback != null) {
            changeCallback.run();
        }
    }

    @Nonnull
    public String getText() {
        return text;
    }

    public void textboxKeyTyped(char typedChar, int keyCode) {
        if (GuiScreen.isKeyComboCtrlC(keyCode)) {
            GuiScreen.setClipboardString(this.getText());
        } else if (GuiScreen.isKeyComboCtrlV(keyCode)) {
            this.setText(GuiScreen.getClipboardString());
        } else if (GuiScreen.isKeyComboCtrlX(keyCode)) {
            GuiScreen.setClipboardString(this.getText());
            this.setText("");
        } else {
            String text = this.getText();
            switch (keyCode) {
                case Keyboard.KEY_BACK:
                    this.setText(text.length() > 1 ? text.substring(0, text.length() - 1) : "");
                    break;
                default:
                    if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                        this.setText(text + Character.toString(typedChar));
                    }
            }
        }
    }
}
