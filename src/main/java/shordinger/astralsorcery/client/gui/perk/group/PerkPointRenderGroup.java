/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.gui.perk.group;

import shordinger.astralsorcery.client.gui.perk.BatchPerkContext;
import shordinger.astralsorcery.client.gui.perk.PerkRenderGroup;
import shordinger.astralsorcery.client.util.SpriteLibrary;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkPointRenderGroup
 * Created by HellFirePvP
 * Date: 24.11.2018 / 11:52
 */
@SideOnly(Side.CLIENT)
public class PerkPointRenderGroup extends PerkRenderGroup {

    public static final PerkPointRenderGroup INSTANCE = new PerkPointRenderGroup();

    private PerkPointRenderGroup() {
        add(SpriteLibrary.spritePerkInactive,     BatchPerkContext.PRIORITY_BACKGROUND + 1);
        add(SpriteLibrary.spritePerkActive,       BatchPerkContext.PRIORITY_BACKGROUND + 2);
        add(SpriteLibrary.spritePerkActivateable, BatchPerkContext.PRIORITY_BACKGROUND + 3);
    }

}
