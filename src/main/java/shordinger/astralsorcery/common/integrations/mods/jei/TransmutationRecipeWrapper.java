/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.jei;

import com.google.common.collect.Lists;
import shordinger.astralsorcery.common.base.LightOreTransmutations;
import shordinger.astralsorcery.common.integrations.mods.jei.base.JEIBaseWrapper;
import mezz.jei.api.ingredients.IIngredients;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.resources.I18n;
import shordinger.wrapper.net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TransmutationRecipeWrapper
 * Created by HellFirePvP
 * Date: 15.02.2017 / 15:57
 */
public class TransmutationRecipeWrapper extends JEIBaseWrapper {

    private LightOreTransmutations.Transmutation transmutation;

    public TransmutationRecipeWrapper(LightOreTransmutations.Transmutation transmutation) {
        this.transmutation = transmutation;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ItemStack in = transmutation.getInputDisplayStack();
        ItemStack out = transmutation.getOutputDisplayStack();

        if(!in.isEmpty() && !out.isEmpty()) {
            ingredients.setInput(ItemStack.class, in);
            ingredients.setOutput(ItemStack.class, out);
        }
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        if (minecraft.fontRenderer != null) {
            if (this.transmutation.getRequiredType() != null) {
                String name = this.transmutation.getRequiredType().getUnlocalizedName();
                String out = I18n.format("misc.transmutation.constellation", I18n.format(name));
                int length = minecraft.fontRenderer.getStringWidth(out);
                minecraft.fontRenderer.drawString(out, recipeWidth / 2 - length / 2, recipeHeight - 12, 0xFF454545, false);
            }
        }
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
