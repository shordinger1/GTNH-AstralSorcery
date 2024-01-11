/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile.base;

import shordinger.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileSkybound
 * Created by HellFirePvP
 * Date: 15.09.2016 / 13:14
 */
public abstract class TileSkybound extends TileEntityTick {

    protected boolean doesSeeSky = false;

    @Override
    public void update() {
        super.update();

        if ((ticksExisted & 15) == 0) {
            updateSkyState(MiscUtils.canSeeSky(this.getWorld(), this.getPos(), true, this.doesSeeSky));
        }
    }

    protected void updateSkyState(boolean seesSky) {
        this.doesSeeSky = seesSky;
    }

    public boolean doesSeeSky() {
        return doesSeeSky;
    }

}
