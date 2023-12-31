/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.effect;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;

import shordinger.astralsorcery.common.constellation.IMajorConstellation;
import shordinger.astralsorcery.common.util.ILocatable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectPositionMap
 * Created by HellFirePvP
 * Date: 01.11.2016 / 01:24
 */
public abstract class CEffectPositionMap<K extends NBTBase, V extends NBTBase>
    extends CEffectPositionListGen<GenListEntries.PosDefinedTuple<K, V>> {

    public CEffectPositionMap(@Nullable ILocatable origin, IMajorConstellation c, String cfgName, int maxCount,
                              Verifier verifier) {
        super(origin, c, cfgName, maxCount, verifier, GenListEntries.PosDefinedTuple::new);
    }

}
