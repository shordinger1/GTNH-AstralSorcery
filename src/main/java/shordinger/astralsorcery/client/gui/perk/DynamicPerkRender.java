/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.gui.perk;

import shordinger.astralsorcery.common.constellation.perk.tree.PerkTreePoint;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DynamicPerkRender
 * Created by HellFirePvP
 * Date: 24.11.2018 / 12:40
 */
public interface DynamicPerkRender {

    public void renderAt(PerkTreePoint.AllocationStatus status, long spriteOffsetTick, float pTicks, double x, double y,
                         double scale);

}
