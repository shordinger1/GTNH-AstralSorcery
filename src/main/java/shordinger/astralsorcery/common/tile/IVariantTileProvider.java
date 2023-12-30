/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import shordinger.astralsorcery.migration.block.IBlockState;

/**
 * HellFirePvP@Admin
 * Date: 21.04.2016 / 22:38
 * on Gadomancy_1_8
 * IVariantTileProvider
 */
public interface IVariantTileProvider {

    public TileEntity provideTileEntity(World world, IBlockState state);

}
