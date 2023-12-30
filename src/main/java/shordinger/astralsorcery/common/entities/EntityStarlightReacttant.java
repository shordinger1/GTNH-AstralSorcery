/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.entities;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.util.ForgeDirection;

import shordinger.astralsorcery.common.block.fluid.FluidBlockLiquidStarlight;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.IBlockState;

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
        return state.isSideSolid(e.getEntityWorld(), at.down(), ForgeDirection.UP);
    }

}
