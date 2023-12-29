/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.altar.recipes;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.crafting.helper.AccessibleRecipe;
import shordinger.astralsorcery.common.crafting.helper.ShapeMap;
import shordinger.astralsorcery.common.item.ItemConstellationPaper;
import shordinger.astralsorcery.common.lib.ItemsAS;
import shordinger.astralsorcery.common.tile.TileAltar;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationPaperRecipe
 * Created by HellFirePvP
 * Date: 25.07.2017 / 19:37
 */
public class ConstellationPaperRecipe extends TraitRecipe {

    private final IConstellation constellation;

    public ConstellationPaperRecipe(AccessibleRecipe recipe, IConstellation constellation) {
        super(recipe);
        setPassiveStarlightRequirement(3000);
        this.constellation = constellation;
    }

    @Nonnull
    @Override
    public ItemStack getOutputForRender() {
        ItemStack cPaper = new ItemStack(ItemsAS.constellationPaper);
        ItemConstellationPaper.setConstellation(cPaper, constellation);
        return cPaper;
    }

    @Nonnull
    @Override
    public ItemStack getOutputForMatching() {
        ItemStack cPaper = new ItemStack(ItemsAS.constellationPaper);
        ItemConstellationPaper.setConstellation(cPaper, constellation);
        return cPaper;
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar altar) {
        ItemStack cPaper = new ItemStack(ItemsAS.constellationPaper);
        ItemConstellationPaper.setConstellation(cPaper, constellation);
        return cPaper;
    }

}
