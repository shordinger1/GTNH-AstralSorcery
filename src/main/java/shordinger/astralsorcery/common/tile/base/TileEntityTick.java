/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile.base;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileEntityTick
 * Created by HellFirePvP
 * Date: 02.08.2016 / 17:34
 */
public abstract class TileEntityTick extends TileEntitySynchronized implements ITickable {

    protected int ticksExisted = 0;

    @Override
    public void update() {
        if (ticksExisted == 0) {
            onFirstTick();
        }

        ticksExisted++;
    }

    protected abstract void onFirstTick();

    public int getTicksExisted() {
        return ticksExisted;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        ticksExisted = compound.getInteger("ticksExisted");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setInteger("ticksExisted", ticksExisted);
    }

}
