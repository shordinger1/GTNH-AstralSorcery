/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.base;

import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.wrapper.net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemConstellationFocus
 * Created by HellFirePvP
 * Date: 06.03.2017 / 14:50
 */
public interface ItemConstellationFocus {

    @Nullable
    public IConstellation getFocusConstellation(ItemStack stack);

}
