/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.tool.wand;

import javax.annotation.Nullable;

import shordinger.astralsorcery.common.constellation.IMajorConstellation;
import shordinger.astralsorcery.common.lib.Constellations;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WandAugment
 * Created by HellFirePvP
 * Date: 30.07.2017 / 14:05
 */
public enum WandAugment {

    AEVITAS(() -> Constellations.aevitas),
    DISCIDIA(() -> Constellations.discidia),
    VICIO(() -> Constellations.vicio),
    ARMARA(() -> Constellations.armara),
    EVORSIO(() -> Constellations.evorsio);

    private final ConstellationGetter constellationGetter;

    WandAugment(ConstellationGetter constellationGetter) {
        this.constellationGetter = constellationGetter;
    }

    public IMajorConstellation getAssociatedConstellation() {
        return constellationGetter.get();
    }

    @Nullable
    public static WandAugment getByConstellation(IMajorConstellation cst) {
        for (WandAugment wa : values()) {
            if (cst.equals(wa.getAssociatedConstellation())) {
                return wa;
            }
        }
        return null;
    }

    private static interface ConstellationGetter {

        IMajorConstellation get();

    }

}
