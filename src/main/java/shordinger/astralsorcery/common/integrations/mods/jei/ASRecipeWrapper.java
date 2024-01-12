package shordinger.astralsorcery.common.integrations.mods.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import shordinger.astralsorcery.common.crafting.helper.BasePlainRecipe;
import shordinger.astralsorcery.common.integrations.ModIntegrationJEI;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.resources.I18n;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;

public class ASRecipeWrapper implements ICraftingRecipeWrapper {

    private final BasePlainRecipe recipe;

    public ASRecipeWrapper(BasePlainRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputLists(
            ItemStack.class,
            ModIntegrationJEI.stackHelper.expandRecipeItemStackInputs(recipe.getIngredients()));
        ingredients.setOutput(ItemStack.class, recipe.getRecipeOutput());
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return recipe.getRegistryName();
    }

    public static class ShapedRecipe extends ASRecipeWrapper implements IShapedCraftingRecipeWrapper {

        public ShapedRecipe(BasePlainRecipe recipe) {
            super(recipe);
        }

        @Override
        public int getHeight() {
            return 3;
        }

        @Override
        public int getWidth() {
            return 3;
        }

    }

    public static class LightRecipe extends ShapedRecipe {

        public LightRecipe(BasePlainRecipe recipe) {
            super(recipe);
        }

        @Override
        public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
            minecraft.fontRenderer
                .drawString(I18n.format("jei.additional_info.starlight"), 60, 46, Color.GRAY.getRGB());
        }

    }
}
