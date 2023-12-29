/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block;

import javax.annotation.Nullable;

import net.minecraft.world.IBlockAccess;

import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockDynamicColor
 * Created by HellFirePvP
 * Date: 05.12.2016 / 07:46
 */
public interface BlockDynamicColor {

    // Return -1 for no color multiplication
    public int getColorMultiplier(IBlockState state, @Nullable IBlockAccess access, @Nullable BlockPos pos,
                                  int renderPass);

}
