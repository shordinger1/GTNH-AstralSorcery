/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import shordinger.astralsorcery.migration.IBlockState;

/**
 * HellFirePvP@Admin
 * Date: 21.04.2016 / 22:38
 * on Gadomancy_1_8
 * IVariantTileProvider
 */
public interface IVariantTileProvider {

    public TileEntity provideTileEntity(World world, IBlockState state);

}
