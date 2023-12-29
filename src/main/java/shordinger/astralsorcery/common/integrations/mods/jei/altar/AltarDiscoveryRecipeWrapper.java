/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.jei.altar;

import com.google.common.collect.Lists;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.crafting.altar.recipes.DiscoveryRecipe;
import shordinger.astralsorcery.common.crafting.helper.AccessibleRecipe;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import shordinger.astralsorcery.common.integrations.mods.jei.base.JEIBaseWrapper;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarDiscoveryRecipeWrapper
 * Created by HellFirePvP
 * Date: 15.02.2017 / 16:50
 */
public class AltarDiscoveryRecipeWrapper extends JEIBaseWrapper {

    private DiscoveryRecipe recipe;

    public AltarDiscoveryRecipeWrapper(DiscoveryRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        AccessibleRecipe underlyingRecipe = recipe.getNativeRecipe();

        boolean r = ItemHandle.ignoreGatingRequirement;
        ItemHandle.ignoreGatingRequirement = true;

        List<List<ItemStack>> stackList = Lists.newArrayList();
        for (ShapedRecipeSlot srs : ShapedRecipeSlot.values()) {
            List<ItemStack> stacks = underlyingRecipe.getExpectedStackForRender(srs);
            stackList.add(stacks == null ? Lists.newArrayList() : stacks);
        }

        ItemHandle.ignoreGatingRequirement = r;

        ingredients.setInputLists(ItemStack.class, stackList);

        ingredients.setOutput(ItemStack.class, recipe.getOutputForRender());
    }

    public DiscoveryRecipe getRecipe() {
        return recipe;
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        /*
         * FontRenderer fr = minecraft.fontRenderer;
         * String toWrite = IGuiRenderablePage.GUI_INTERFACE.getDescriptionFromStarlightAmount(I18n.format(
         * "astralsorcery.journal.recipe.amt.desc"),
         * recipe.getPassiveStarlightRequired(), TileAltar.AltarLevel.DISCOVERY.getStarlightMaxStorage());
         * int w = fr.getStringWidth(toWrite);
         * fr.drawString(toWrite, recipeWidth / 2 - w / 2 + 1, recipeHeight + 1, 0xCCCCFF);
         * fr.drawString(toWrite, recipeWidth / 2 - w / 2, recipeHeight, 0x0044EE);
         */
    }

    @Nullable
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Lists.newArrayList();
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }
}
