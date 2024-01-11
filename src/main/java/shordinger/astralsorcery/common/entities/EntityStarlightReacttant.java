/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.entities;

import shordinger.astralsorcery.common.block.fluid.FluidBlockLiquidStarlight;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityStarlightReacttant
 * Created by HellFirePvP
 * Date: 11.12.2016 / 16:26
 */
public interface EntityStarlightReacttant {

    default public boolean isInLiquidStarlight(Entity e) {
        BlockPos at = e.getPosition();
        IBlockState state = e.getEntityWorld()
            .getBlockState(at);
        if (!(state.getBlock() instanceof FluidBlockLiquidStarlight)) {
            return false;
        }
        if (!((FluidBlockLiquidStarlight) state.getBlock()).isSourceBlock(e.getEntityWorld(), at)) {
            return false;
        }
        state = e.getEntityWorld()
            .getBlockState(at.down());
        return state.isSideSolid(e.getEntityWorld(), at.down(), EnumFacing.UP);
    }

}
