/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.structure.array;

import java.util.Map;

import net.minecraft.world.World;

import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiBlockArray
 * Created by HellFirePvP
 * Date: 30.07.2016 / 16:07
 */
public class StructureBlockArray extends BlockArray {

    public Map<BlockPos, IBlockState> placeInWorld(World world, BlockPos center, PastPlaceProcessor processor) {
        Map<BlockPos, IBlockState> result = super.placeInWorld(world, center);
        if (processor != null) {
            for (Map.Entry<BlockPos, IBlockState> entry : result.entrySet())
                processor.process(world, entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static interface PastPlaceProcessor {

        public void process(World world, BlockPos pos, IBlockState currentState);

    }

}
