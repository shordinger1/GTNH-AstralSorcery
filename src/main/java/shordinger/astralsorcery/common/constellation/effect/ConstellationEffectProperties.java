/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.effect;

import shordinger.astralsorcery.common.constellation.IMinorConstellation;
import shordinger.astralsorcery.common.lib.Constellations;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationEffectProperties
 * Created by HellFirePvP
 * Date: 01.02.2018 / 19:14
 */
public class ConstellationEffectProperties {

    private double size;
    private double potency = 1;
    private double effectAmplifier = 1;
    private boolean corrupted = false;
    private double fracturationLower = 1F, fracturationRate = 1F;

    public ConstellationEffectProperties(double size) {
        this.size = size;
    }

    public double getSize() {
        return size;
    }

    public double getPotency() {
        return potency;
    }

    public double getEffectAmplifier() {
        return effectAmplifier;
    }

    public boolean isCorrupted() {
        return corrupted;
    }

    public double getFracturationLowerBoundaryMultiplier() {
        return fracturationLower;
    }

    public double getFracturationRate() {
        return fracturationRate;
    }

    public ConstellationEffectProperties modify(IMinorConstellation trait) {
        if (trait != null) {
            if (trait.equals(Constellations.gelu)) {
                potency *= 0.15F;
                size *= 3.5F;
            }
            if (trait.equals(Constellations.ulteria)) {
                effectAmplifier *= 4F;
                size *= 0.2F;
            }
            if (trait.equals(Constellations.alcara)) {
                fracturationLower *= 0.015F;
                fracturationRate *= 50_000F;

                size *= 2F;
                effectAmplifier *= 2F;
                corrupted = true;
            }
            if (trait.equals(Constellations.vorux)) {
                fracturationLower *= 0.25F;
                fracturationRate *= 3_000F;

                effectAmplifier *= 2F;
                size *= 1.75F;
            }
        }
        return this;
    }

}
