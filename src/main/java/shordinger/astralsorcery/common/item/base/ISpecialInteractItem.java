/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.base;

import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ISpecialInteractItem
 * Created by HellFirePvP
 * Date: 23.09.2016 / 15:10
 */
public interface ISpecialInteractItem {

    public boolean needsSpecialHandling(World world, BlockPos at, EntityPlayer player, ItemStack stack);

    public boolean onRightClick(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side, EnumHand hand,
                                ItemStack stack);

}
