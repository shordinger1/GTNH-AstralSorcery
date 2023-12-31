/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.base.render;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import shordinger.astralsorcery.migration.block.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ISpecialStackDescriptor
 * Created by HellFirePvP
 * Date: 23.04.2017 / 18:14
 */
// Intended to be implemented by blocks to give a more appropiate itemstack as descriptor
public interface ISpecialStackDescriptor {

    @Nonnull
    public ItemStack getDecriptor(IBlockState state);

}
