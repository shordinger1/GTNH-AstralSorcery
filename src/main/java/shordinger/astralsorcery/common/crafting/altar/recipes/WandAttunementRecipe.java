/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.altar.recipes;

import shordinger.astralsorcery.common.constellation.IMajorConstellation;
import shordinger.astralsorcery.common.crafting.helper.AccessibleRecipeAdapater;
import shordinger.astralsorcery.common.crafting.helper.ShapeMap;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import shordinger.astralsorcery.common.item.tool.wand.ItemWand;
import shordinger.astralsorcery.common.item.tool.wand.WandAugment;
import shordinger.astralsorcery.common.lib.ItemsAS;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import shordinger.wrapper.net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WandAttunementRecipe
 * Created by HellFirePvP
 * Date: 04.08.2017 / 18:39
 */
public class WandAttunementRecipe extends TraitRecipe {

    private final IMajorConstellation cst;

    public WandAttunementRecipe(IMajorConstellation cst, AccessibleRecipeAdapater ara) {
        super(ara);
        this.cst = cst;
    }

    @Override
    public boolean matches(TileAltar altar, TileReceiverBaseInventory.ItemHandlerTile invHandler, boolean ignoreStarlightRequirement) {
        ItemStack center = invHandler.getStackInSlot(ShapedRecipeSlot.CENTER.getSlotID());
        if(center.isEmpty() ||
                !(center.getItem() instanceof ItemWand) ||
                ItemWand.getAugment(center) != null) {
            return false;
        }

        return super.matches(altar, invHandler, ignoreStarlightRequirement);
    }

    @Nonnull
    @Override
    public ItemStack getOutputForRender() {
        ItemStack cPaper = new ItemStack(ItemsAS.wand);
        ItemWand.setAugment(cPaper, WandAugment.getByConstellation(cst));
        return cPaper;
    }

    @Nonnull
    @Override
    public ItemStack getOutputForMatching() {
        ItemStack cPaper = new ItemStack(ItemsAS.wand);
        ItemWand.setAugment(cPaper, WandAugment.getByConstellation(cst));
        return cPaper;
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar altar) {
        ItemStack cPaper = new ItemStack(ItemsAS.wand);
        ItemWand.setAugment(cPaper, WandAugment.getByConstellation(cst));
        return cPaper;
    }

}
