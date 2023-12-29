/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.starlight.transmission.base;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;

import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.starlight.IIndependentStarlightSource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleIndependentSource
 * Created by HellFirePvP
 * Date: 05.08.2016 / 00:27
 */
public abstract class SimpleIndependentSource implements IIndependentStarlightSource {

    protected IWeakConstellation starlightType;

    public SimpleIndependentSource(IWeakConstellation constellation) {
        this.starlightType = constellation;
    }

    @Override
    @Nullable
    public IWeakConstellation getStarlightType() {
        return starlightType;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.starlightType = (IWeakConstellation) IConstellation.readFromNBT(compound);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        if (starlightType != null) {
            starlightType.writeToNBT(compound);
        }
    }

}
