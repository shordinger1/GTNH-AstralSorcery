/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ISpecialInteractItem
 * Created by HellFirePvP
 * Date: 23.09.2016 / 15:10
 */
public interface ISpecialInteractItem {

    public boolean needsSpecialHandling(World world, BlockPos at, EntityPlayer player, ItemStack stack);

    public boolean onRightClick(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side,
                                ItemStack stack);

}
