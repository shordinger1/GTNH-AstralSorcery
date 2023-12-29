/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util.data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: NonDuplicateCappedList
 * Created by HellFirePvP
 * Date: 30.12.2016 / 14:00
 */
public class NonDuplicateCappedList<T> implements Iterable<T> {

    protected static final Random rand = new Random();

    protected List<T> elements = new LinkedList<>();

    private int cap = 0;

    public NonDuplicateCappedList(int cap) {
        this.cap = cap;
    }

    public boolean offerElement(T element) {
        if (elements.size() + 1 > cap) return false;
        if (elements.contains(element)) return false;
        return elements.add(element);
    }

    @Nullable
    public T getRandomElement() {
        if (elements.isEmpty()) return null;
        return elements.get(rand.nextInt(elements.size()));
    }

    @Nullable
    public T getRandomElementByChance(Random rand, float rngMultiplier) {
        if (elements.isEmpty()) return null;
        if (Math.max(0, rand.nextInt(((int) ((cap - elements.size()) * rngMultiplier)) / 2 + 1)) == 0) {
            return getRandomElement();
        }
        return null;
    }

    public boolean removeElement(T element) {
        return elements.remove(element);
    }

    public void clear() {
        this.elements.clear();
    }

    public int getSize() {
        return elements.size();
    }

    public int getCap() {
        return cap;
    }

    public void setCap(int cap) {
        this.cap = cap;
    }

    @Override
    public Iterator<T> iterator() {
        return elements.iterator();
    }

}
