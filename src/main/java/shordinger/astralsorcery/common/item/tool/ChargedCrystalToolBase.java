/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.item.crystal.ToolCrystalProperties;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ChargedCrystalToolBase
 * Created by HellFirePvP
 * Date: 14.03.2017 / 12:38
 */
public interface ChargedCrystalToolBase {

    static Random chRand = new Random();

    @Nonnull
    public Item getInertVariant();

    public static ItemStack getAsInertVariant(ItemStack stack) {
        ToolCrystalProperties prop = getToolProperties(stack);
        ItemStack inert = new ItemStack(((ChargedCrystalToolBase) stack.getItem()).getInertVariant());
        applyToolProperties(inert, prop);
        if (stack.hasTagCompound()) {
            inert.setTagCompound(
                (NBTTagCompound) stack.getTagCompound()
                    .copy());
        }
        return inert;
    }

    public static boolean shouldRevert(ItemStack stack) {
        if (!Config.shouldChargedToolsRevert) return false;
        NBTTagCompound tag = NBTHelper.getPersistentData(stack);
        if (!tag.hasKey("chCount")) {
            tag.setInteger("chCount", 0);
        }
        int c = tag.getInteger("chCount");
        c++;
        tag.setInteger("chCount", c);
        if (c >= Config.revertStart) {
            return chRand.nextInt(Config.revertChance) == 0;
        } else {
            return false;
        }
    }

    public static void removeChargeRevertCounter(ItemStack stack) {
        NBTHelper.getPersistentData(stack)
            .removeTag("chCount");
    }

    public static boolean tryRevertMainHand(EntityPlayer player, ItemStack stack) {
        if (shouldRevert(stack)) {
            ItemStack inert = getAsInertVariant(stack);
            removeChargeRevertCounter(inert);
            player.setHeldItem(, inert);
            return false;
        }
        return true;
    }

    public static void applyToolProperties(ItemStack stack, ToolCrystalProperties properties) {
        properties.writeToNBT(NBTHelper.getPersistentData(stack));
    }

    public static ToolCrystalProperties getToolProperties(ItemStack stack) {
        NBTTagCompound nbt = NBTHelper.getPersistentData(stack);
        return ToolCrystalProperties.readFromNBT(nbt);
    }

}
