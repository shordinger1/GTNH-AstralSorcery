package shordinger.wrapper.net.minecraft.client.gui.spectator;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;

@SideOnly(Side.CLIENT)
public interface ISpectatorMenuView {

    List<ISpectatorMenuObject> getItems();

    ITextComponent getPrompt();
}
