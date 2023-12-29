/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.base.render;

import net.minecraft.entity.player.EntityPlayer;

import shordinger.astralsorcery.common.constellation.charge.PlayerChargeHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemAlignmentChargeConsumer
 * Created by HellFirePvP
 * Date: 27.12.2016 / 21:52
 */
public interface ItemAlignmentChargeConsumer extends ItemAlignmentChargeRevealer {

    default public boolean drainTempCharge(EntityPlayer player, float charge, boolean simulate) {
        if (player.isCreative()) return true;

        if (!PlayerChargeHandler.INSTANCE.hasAtLeast(player, charge)) {
            return false;
        }
        if (!simulate) {
            PlayerChargeHandler.INSTANCE.drainCharge(player, charge);
        }
        return true;
    }

}
