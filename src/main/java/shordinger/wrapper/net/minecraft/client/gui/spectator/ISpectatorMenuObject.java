package shordinger.wrapper.net.minecraft.client.gui.spectator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;

@SideOnly(Side.CLIENT)
public interface ISpectatorMenuObject {

    void selectItem(SpectatorMenu menu);

    ITextComponent getSpectatorName();

    void renderIcon(float brightness, int alpha);

    boolean isEnabled();
}
