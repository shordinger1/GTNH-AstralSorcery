/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.base.sets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import shordinger.astralsorcery.common.data.config.ConfigDataAdapter;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OreEntry
 * Created by HellFirePvP
 * Date: 05.11.2017 / 10:28
 */
public class OreEntry implements ConfigDataAdapter.DataSet {

    public final String oreName;
    public final int weight;

    public OreEntry(String oreName, int weight) {
        this.oreName = oreName;
        this.weight = weight;
    }

    @Nonnull
    @Override
    public String serialize() {
        return oreName + ";" + weight;
    }

    @Nullable
    public static OreEntry deserialize(String str) {
        String[] spl = str.split(";");
        if (spl.length != 2) {
            return null;
        }
        String oreDict = spl[0];
        int weight;
        try {
            weight = Integer.parseInt(spl[1]);
        } catch (Exception exc) {
            return null;
        }
        return new OreEntry(oreDict, weight);
    }

}
