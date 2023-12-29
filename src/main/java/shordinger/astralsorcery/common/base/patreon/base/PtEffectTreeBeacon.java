/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.base.patreon.base;

import java.util.UUID;

import shordinger.astralsorcery.common.base.patreon.PatreonEffectHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PtEffectTreeBeacon
 * Created by HellFirePvP
 * Date: 20.06.2018 / 19:59
 */
public class PtEffectTreeBeacon extends PatreonEffectHelper.PatreonEffect {

    private int overlay = 0xFFFFFFFF, tree = 0xFFFFFFFF, drain = 0xFFFFFFFF;

    public PtEffectTreeBeacon(UUID uniqueId, PatreonEffectHelper.FlareColor chosenColor) {
        super(uniqueId, chosenColor);
    }

    public PtEffectTreeBeacon setOverlayColor(int overlay) {
        this.overlay = overlay;
        return this;
    }

    public PtEffectTreeBeacon setDrainColor(int drain) {
        this.drain = drain;
        return this;
    }

    public PtEffectTreeBeacon setTreeColor(int tree) {
        this.tree = tree;
        return this;
    }

    public int getColorTranslucentOverlay() {
        return this.overlay;
    }

    public int getColorTreeEffects() {
        return this.tree;
    }

    public int getColorTreeDrainEffects() {
        return this.drain;
    }

}
