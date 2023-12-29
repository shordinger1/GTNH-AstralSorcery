/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.crystal;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalPropertyItem
 * Created by HellFirePvP
 * Date: 19.05.2018 / 16:55
 */
public interface CrystalPropertyItem {

    int getMaxSize(ItemStack stack);

    @Nullable
    CrystalProperties provideCurrentPropertiesOrNull(ItemStack stack);

}
