package shordinger.wrapper.net.minecraft.client.gui.recipebook;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IRecipeShownListener {

    void recipesUpdated();

    GuiRecipeBook func_194310_f();
}
