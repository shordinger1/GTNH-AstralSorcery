/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.base;

import java.awt.*;

import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemHighlighted
 * Created by HellFirePvP
 * Date: 01.08.2016 / 19:42
 */
public interface ItemHighlighted {

    default public Color getHightlightColor(ItemStack stack) {
        return Color.WHITE;
    }

}
