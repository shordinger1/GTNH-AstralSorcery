/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile.base;

import net.minecraft.nbt.NBTTagCompound;

import shordinger.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileNetworkSkybound
 * Created by HellFirePvP
 * Date: 02.08.2016 / 17:35
 */
public abstract class TileNetworkSkybound extends TileNetwork {

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
        markForUpdate();
    }

    public boolean doesSeeSky() {
        return doesSeeSky;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.doesSeeSky = compound.getBoolean("doesSeeSky");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setBoolean("doesSeeSky", this.doesSeeSky);
    }
}
