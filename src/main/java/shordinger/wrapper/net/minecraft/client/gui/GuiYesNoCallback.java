package shordinger.wrapper.net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface GuiYesNoCallback {

    void confirmClicked(boolean result, int id);
}
