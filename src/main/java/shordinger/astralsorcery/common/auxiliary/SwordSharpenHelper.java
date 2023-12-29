/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.auxiliary;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import shordinger.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SwordSharpenHelper
 * Created by HellFirePvP
 * Date: 04.11.2016 / 17:41
 */
public class SwordSharpenHelper {

    public static List<Class<?>> otherSharpenableSwordSuperClasses = new LinkedList<>();
    public static List<String> blacklistedSharpenableSwordClassNames = new LinkedList<>();

    public static boolean isSwordSharpened(@Nonnull ItemStack stack) {
        if (!isSharpenableItem(stack) || !stack.hasTagCompound()) return false;
        return NBTHelper.getData(stack)
            .getBoolean("sharp");
    }

    public static void setSwordSharpened(@Nonnull ItemStack stack) {
        if (!isSharpenableItem(stack)) return;
        NBTHelper.getData(stack)
            .setBoolean("sharp", true);
    }

    public static boolean canBeSharpened(@Nonnull ItemStack stack) {
        if (stack.isEmpty()) return false;
        Item i = stack.getItem();
        if (blacklistedSharpenableSwordClassNames.contains(
            i.getClass()
                .getName()))
            return false;

        if (isSharpenableItem(stack)) return true;
        Class<?> itemClass = stack.getItem()
            .getClass();
        for (Class<?> clazz : otherSharpenableSwordSuperClasses) {
            if (clazz.isAssignableFrom(itemClass)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSharpenableItem(ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemAxe);
    }

}
