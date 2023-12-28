/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.base.render;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemAlignmentChargeRevealer
 * Created by HellFirePvP
 * Date: 27.12.2016 / 13:36
 */
public interface ItemAlignmentChargeRevealer {

    @SideOnly(Side.CLIENT)
    default public boolean shouldReveal(ChargeType ct, ItemStack stack) {
        return true;
    }

    public static enum ChargeType {

        TEMP,
        PERM

    }

}
