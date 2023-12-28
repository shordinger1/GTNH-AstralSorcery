/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.types;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IPlayerTickPerk
 * Created by HellFirePvP
 * Date: 30.06.2018 / 15:55
 */
public interface IPlayerTickPerk {

    // The player being ticked definitely has the perk unlocked
    public void onPlayerTick(EntityPlayer player, Side side);

}
