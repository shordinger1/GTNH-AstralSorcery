/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting;

import net.minecraft.nbt.NBTTagCompound;

import shordinger.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import shordinger.astralsorcery.common.tile.TileAltar;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ICraftingProgress
 * Created by HellFirePvP
 * Date: 13.04.2017 / 18:33
 */
public interface ICraftingProgress {

    // True if the recipe progressed, false if the recipe should be stuck..
    public boolean tryProcess(TileAltar altar, ActiveCraftingTask runningTask, NBTTagCompound craftingData,
                              int activeCraftingTick, int totalCraftingTime);

}
