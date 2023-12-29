/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util.data;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.util.WeightedRandom;

import shordinger.astralsorcery.common.util.WRItemObject;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: NonDuplicateCappedWeightedList
 * Created by HellFirePvP
 * Date: 11.02.2018 / 16:04
 */
public class NonDuplicateCappedWeightedList<T> extends NonDuplicateCappedList<WRItemObject<T>> {

    public NonDuplicateCappedWeightedList(int cap) {
        super(cap);
    }

    @Nullable
    public WRItemObject<T> getRandomElement() {
        return WeightedRandom.getRandomItem(rand, this.elements);
    }

    @Nullable
    public WRItemObject<T> getRandomElementByChance(Random rand, float rngMultiplier) {
        return WeightedRandom.getRandomItem(NonDuplicateCappedList.rand, this.elements);
    }

    @Override
    public boolean removeElement(WRItemObject<T> element) {
        return elements.remove(element) || elements.remove(element.getValue());
    }

}
