/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.crafttweaker.network;

import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.crafting.helper.CraftingAccessManager;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.wrapper.net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarRecipeDiscovery
 * Created by HellFirePvP
 * Date: 27.02.2017 / 15:15
 */
public class AltarRecipeDiscovery extends BaseAltarRecipe {

    AltarRecipeDiscovery() {
        super(null, null, null, 0, 0);
    }

    public AltarRecipeDiscovery(String name, ItemHandle[] inputs, ItemStack output, int starlightRequired,
                                int craftingTickTime) {
        super(name, inputs, output, starlightRequired, craftingTickTime);
    }

    @Override
    public CraftingType getType() {
        return CraftingType.ALTAR_T1_ADD;
    }

    @Override
    public void applyRecipe() {
        CraftingAccessManager.registerMTAltarRecipe(
            buildRecipeUnsafe(
                TileAltar.AltarLevel.DISCOVERY,
                this.starlightRequired,
                this.craftingTickTime,
                this.output,
                this.inputs));
    }

}
