/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util;

import net.minecraft.util.WeightedRandom;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WRItemObject
 * Created by HellFirePvP
 * Date: 07.05.2016 / 15:20
 */
public class WRItemObject<T> extends WeightedRandom.Item {

    private final T object;

    public WRItemObject(int itemWeightIn, T value) {
        super(itemWeightIn);
        this.object = value;
    }

    public T getValue() {
        return object;
    }

}
