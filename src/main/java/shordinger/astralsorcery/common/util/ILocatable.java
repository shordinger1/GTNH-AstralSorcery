/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util;

import shordinger.astralsorcery.migration.block.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ILocatable
 * Created by HellFirePvP
 * Date: 26.07.2017 / 21:44
 */
public interface ILocatable {

    public BlockPos getLocationPos();

    public static ILocatable fromPos(BlockPos pos) {
        return new PosLocatable(pos);
    }

    class PosLocatable implements ILocatable {

        private final BlockPos pos;

        private PosLocatable(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public BlockPos getLocationPos() {
            return pos;
        }

    }

}
