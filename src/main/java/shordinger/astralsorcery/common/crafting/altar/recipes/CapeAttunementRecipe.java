/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.altar.recipes;

import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.crafting.helper.AccessibleRecipe;
import shordinger.astralsorcery.common.crafting.helper.ShapeMap;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import shordinger.astralsorcery.common.item.wearable.ItemCape;
import shordinger.astralsorcery.common.lib.ItemsAS;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.wrapper.net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeAttunementRecipe
 * Created by HellFirePvP
 * Date: 19.10.2017 / 18:40
 */
public class CapeAttunementRecipe extends TraitRecipe {

    private final IConstellation constellation;

    public CapeAttunementRecipe(IConstellation constellation, AccessibleRecipe recipe) {
        super(recipe);
        setPassiveStarlightRequirement(3000);
        this.constellation = constellation;
        setRequiredConstellation(constellation);
    }

    @Nonnull
    @Override
    public ItemStack getOutputForRender() {
        ItemStack cPaper = new ItemStack(ItemsAS.armorImbuedCape);
        ItemCape.setAttunedConstellation(cPaper, constellation);
        return cPaper;
    }

    @Nonnull
    @Override
    public ItemStack getOutputForMatching() {
        ItemStack cPaper = new ItemStack(ItemsAS.armorImbuedCape);
        ItemCape.setAttunedConstellation(cPaper, constellation);
        return cPaper;
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar altar) {
        ItemStack cape = new ItemStack(ItemsAS.armorImbuedCape);
        ItemStack center = altar.getInventoryHandler().getStackInSlot(ShapedRecipeSlot.CENTER.getSlotID());
        if(!center.isEmpty() && center.getItem() instanceof ItemCape) {
            cape = ItemUtils.copyStackWithSize(center, center.getCount());
        }
        ItemCape.setAttunedConstellation(cape, constellation);
        return cape;
    }

}
