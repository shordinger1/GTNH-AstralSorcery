/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.gui.perk.group;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.gui.perk.BatchPerkContext;
import shordinger.astralsorcery.client.gui.perk.PerkRenderGroup;
import shordinger.astralsorcery.client.util.SpriteLibrary;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkPointHaloRenderGroup
 * Created by HellFirePvP
 * Date: 24.11.2018 / 13:05
 */
@SideOnly(Side.CLIENT)
public class PerkPointHaloRenderGroup extends PerkRenderGroup {

    public static final PerkPointHaloRenderGroup INSTANCE = new PerkPointHaloRenderGroup();

    private PerkPointHaloRenderGroup() {
        add(SpriteLibrary.spriteHalo4, BatchPerkContext.PRIORITY_BACKGROUND + 51);
        add(SpriteLibrary.spriteHalo5, BatchPerkContext.PRIORITY_BACKGROUND + 52);
        add(SpriteLibrary.spriteHalo6, BatchPerkContext.PRIORITY_BACKGROUND + 53);
    }

}
