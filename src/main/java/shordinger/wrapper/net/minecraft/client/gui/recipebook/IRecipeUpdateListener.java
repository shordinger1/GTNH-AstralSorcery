package shordinger.wrapper.net.minecraft.client.gui.recipebook;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.item.crafting.IRecipe;

@SideOnly(Side.CLIENT)
public interface IRecipeUpdateListener {

    void recipesShown(List<IRecipe> recipes);
}
