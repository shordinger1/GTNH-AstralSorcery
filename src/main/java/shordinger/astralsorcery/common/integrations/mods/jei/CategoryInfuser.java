/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.jei;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import shordinger.astralsorcery.common.integrations.ModIntegrationJEI;
import shordinger.astralsorcery.common.integrations.mods.jei.base.JEIBaseCategory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CategoryInfuser
 * Created by HellFirePvP
 * Date: 11.01.2017 / 00:10
 */
public class CategoryInfuser extends JEIBaseCategory<InfuserRecipeWrapper> {

    private final IDrawable background;

    public CategoryInfuser(IGuiHelper guiHelper) {
        super("jei.category.infuser", ModIntegrationJEI.idInfuser);
        ResourceLocation location = new ResourceLocation(
            "astralsorcery",
            "textures/gui/jei/recipeTemplateInfusion.png");
        background = guiHelper.createDrawable(location, 0, 0, 116, 54);
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, InfuserRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();
        group.init(0, true, 18, 18);
        group.init(1, false, 94, 18);

        group.set(ingredients);
    }
}
