/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.jei.altar;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import shordinger.astralsorcery.common.integrations.ModIntegrationJEI;
import shordinger.astralsorcery.common.integrations.mods.jei.base.JEIBaseCategory;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.resources.I18n;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.text.TextFormatting;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CategoryAltarDiscovery
 * Created by HellFirePvP
 * Date: 15.02.2017 / 16:54
 */
public class CategoryAltarDiscovery extends JEIBaseCategory<AltarDiscoveryRecipeWrapper> {

    private final IDrawable background;

    public CategoryAltarDiscovery(IGuiHelper guiHelper) {
        super("jei.category.altar.discovery", ModIntegrationJEI.idAltarDiscovery);
        ResourceLocation location = new ResourceLocation(
            "astralsorcery",
            "textures/gui/jei/recipeTemplateAltarDiscovery.png");
        background = guiHelper.createDrawable(location, 0, 0, 116, 162);
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {}

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, AltarDiscoveryRecipeWrapper recipeWrapper,
                          IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();
        group.init(0, false, 48, 18);

        group.init(1, true, 22, 70);
        group.init(2, true, 49, 70);
        group.init(3, true, 76, 70);
        group.init(4, true, 22, 97);
        group.init(5, true, 49, 97);
        group.init(6, true, 76, 97);
        group.init(7, true, 22, 124);
        group.init(8, true, 49, 124);
        group.init(9, true, 76, 124);

        group.set(ingredients);

        group.addTooltipCallback((slot, input, stack, tooltip) -> {
            if (!input && Minecraft.getMinecraft().gameSettings.showDebugInfo) {
                tooltip.add("");
                tooltip.add(
                    TextFormatting.DARK_GRAY + I18n.format(
                        "misc.recipename",
                        recipeWrapper.getRecipe()
                            .getNativeRecipe()
                            .getRegistryName()
                            .toString()));
            }
        });
    }

}
