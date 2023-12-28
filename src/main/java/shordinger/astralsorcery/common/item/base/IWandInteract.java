/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IWandInteract
 * Created by HellFirePvP
 * Date: 23.09.2016 / 12:44
 */
public interface IWandInteract {

    public void onInteract(World world, BlockPos pos, EntityPlayer player, EnumFacing side, boolean sneak);

}
