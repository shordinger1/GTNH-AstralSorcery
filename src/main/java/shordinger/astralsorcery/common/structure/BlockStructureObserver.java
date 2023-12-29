/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.structure;

import net.minecraft.world.IBlockAccess;

import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStructureObserver
 * Created by HellFirePvP
 * Date: 29.12.2018 / 15:52
 */
// Add interface to blocks to notify structure match buffer of block removals
public interface BlockStructureObserver {

    // oldState's block will be *this* block's instance!
    default boolean removeWithNewState(IBlockAccess world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState != newState;
    }

}
