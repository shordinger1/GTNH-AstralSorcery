/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.starlight.transmission.base;

import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.starlight.IIndependentStarlightSource;
import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

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
        if(starlightType != null) {
            starlightType.writeToNBT(compound);
        }
    }

}
