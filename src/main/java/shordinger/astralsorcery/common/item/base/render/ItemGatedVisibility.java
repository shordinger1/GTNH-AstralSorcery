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
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemGatedVisibility
 * Created by HellFirePvP
 * Date: 13.01.2017 / 00:06
 */
public interface ItemGatedVisibility {

    default public PlayerProgress getClientProgress() {
        return ResearchManager.clientProgress;
    }

    @SideOnly(Side.CLIENT)
    public boolean isSupposedToSeeInRender(ItemStack stack);

}
