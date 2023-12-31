/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.block;

import net.minecraft.item.ItemBlock;

import shordinger.astralsorcery.common.lib.BlocksAS;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemBlockRitualPedestal
 * Created by HellFirePvP
 * Date: 01.11.2016 / 14:38
 */
public class ItemBlockRitualPedestal extends ItemBlock {

    public ItemBlockRitualPedestal() {
        super(BlocksAS.ritualPedestal);
    }

    /*
     * public static void setBeaconType(ItemStack stack, boolean isPlayerBeacon) {
     * NBTHelper.getData(stack).setBoolean("playerBeacon", isPlayerBeacon);
     * }
     * public static boolean isPlayerBeacon(ItemStack stack) {
     * return NBTHelper.getData(stack).getBoolean("playerBeacon");
     * }
     */

}
