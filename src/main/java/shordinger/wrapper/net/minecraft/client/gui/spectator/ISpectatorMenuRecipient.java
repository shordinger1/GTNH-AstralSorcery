package shordinger.wrapper.net.minecraft.client.gui.spectator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ISpectatorMenuRecipient {

    void onSpectatorMenuClosed(SpectatorMenu menu);
}
