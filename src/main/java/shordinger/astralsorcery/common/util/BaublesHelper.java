/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import com.google.common.collect.Iterables;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BaublesHelper
 * Created by HellFirePvP
 * Date: 27.01.2018 / 14:14
 */
public class BaublesHelper {

    public static boolean doesPlayerWearBauble(EntityPlayer player, BaubleType inType, Predicate<ItemStack> predicate) {
        for (ItemStack worn : getWornBaublesForType(player, inType)) {
            if (predicate.test(worn)) {
                return true;
            }
        }
        return false;
    }

    public static Iterable<ItemStack> getWornBaublesForType(EntityPlayer player, BaubleType type) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        List<ItemStack> worn = NonNullList.create();
        for (int slot : type.getValidSlots()) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (stack.stackSize!=0) {
                worn.add(stack);
            }
        }
        return worn;
    }

    public static ItemStack getFirstWornBaublesForType(EntityPlayer player, BaubleType type) {
        return Iterables.getFirst(getWornBaublesForType(player, type), null);
    }

}
