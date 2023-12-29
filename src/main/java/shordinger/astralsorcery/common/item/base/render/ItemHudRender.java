/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
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
 * Class: ItemHudRender
 * Created by HellFirePvP
 * Date: 07.02.2017 / 03:10
 */
public interface ItemHudRender {

    default public boolean hasFadeIn() {
        return false;
    }

    default public int getFadeInTicks() {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    public void onRenderInHandHUD(ItemStack lastCacheInstance, float fadeAlpha, float pTicks);

}
