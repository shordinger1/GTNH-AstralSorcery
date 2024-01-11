/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.helper;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

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
