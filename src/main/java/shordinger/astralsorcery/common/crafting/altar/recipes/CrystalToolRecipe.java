/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.altar.recipes;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.crafting.helper.AccessibleRecipeAdapater;
import shordinger.astralsorcery.common.crafting.helper.ShapeMap;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import shordinger.astralsorcery.common.item.crystal.CrystalProperties;
import shordinger.astralsorcery.common.item.crystal.ToolCrystalProperties;
import shordinger.astralsorcery.common.item.tool.ItemCrystalToolBase;
import shordinger.astralsorcery.common.tile.TileAltar;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalToolRecipe
 * Created by HellFirePvP
 * Date: 26.09.2016 / 01:44
 */
public class CrystalToolRecipe extends DiscoveryRecipe {

    private final ShapedRecipeSlot[] positions;

    public CrystalToolRecipe(AccessibleRecipeAdapater recipe, ShapedRecipeSlot... crystalPositions) {
        super(recipe);
        this.positions = crystalPositions;
    }

    @Override
    public int craftingTickTime() {
        return (int) (super.craftingTickTime() * 1.5);
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar altar) {
        ItemStack toolOut = super.getOutput(centralGridMap, altar);
        List<CrystalProperties> prop = new LinkedList<>();
        for (ShapedRecipeSlot slot : ShapedRecipeSlot.values()) {
            ItemHandle handle = centralGridMap.get(slot);
            if (handle == null) continue;
            if (handle.getApplicableItems()
                .size() != 1) continue; // Force it to be the crystal. and only the crystal.
            ItemStack stack = handle.getApplicableItems()
                .get(0);
            CrystalProperties c = CrystalProperties.getCrystalProperties(stack);
            if (c == null) continue;
            prop.add(c);
        }
        ItemCrystalToolBase.setToolProperties(toolOut, ToolCrystalProperties.merge(prop));
        return toolOut;
    }

    @Nonnull
    @Override
    public ItemStack getOutputForRender() {
        ItemStack stack = super.getOutputForRender();
        List<CrystalProperties> props = new LinkedList<>();
        for (int i = 0; i < positions.length; i++) {
            props.add(CrystalProperties.getMaxRockProperties());
        }
        ItemCrystalToolBase.setToolProperties(stack, ToolCrystalProperties.merge(props));
        return stack;
    }

    @Override
    public boolean allowsForChaining() {
        return false;
    }

}
