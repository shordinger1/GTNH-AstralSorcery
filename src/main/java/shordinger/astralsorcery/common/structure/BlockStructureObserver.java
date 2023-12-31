/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.structure;

import net.minecraft.world.IBlockAccess;

import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.IBlockState;

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
