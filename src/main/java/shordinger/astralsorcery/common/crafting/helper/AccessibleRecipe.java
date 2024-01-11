/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.helper;

import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AccessibleRecipe
 * Created by HellFirePvP
 * Date: 06.10.2016 / 14:18
 */
public abstract class AccessibleRecipe extends BasePlainRecipe {

    protected AccessibleRecipe(@Nullable ResourceLocation registryName) {
        super(registryName);
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public abstract NonNullList<ItemStack> getExpectedStackForRender(int row, int column);

    @Nullable
    public abstract ItemHandle getExpectedStackHandle(int row, int column);

    @Nullable
    @SideOnly(Side.CLIENT)
    public abstract NonNullList<ItemStack> getExpectedStackForRender(ShapedRecipeSlot slot);

    @Nullable
    public abstract ItemHandle getExpectedStackHandle(ShapedRecipeSlot slot);

}
