/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.helper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.migration.NonNullList;

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
