/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.google.common.collect.Lists;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: NonDuplicateArrayList
 * Created by HellFirePvP
 * Date: 24.11.2018 / 11:56
 */
public class NonDuplicateArrayList<E> implements Collection<E> {

    private final ArrayList<E> managed = Lists.newArrayList();

    @Override
    public int size() {
        return managed.size();
    }

    @Override
    public boolean isEmpty() {
        return managed.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return managed.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return managed.iterator();
    }

    @Override
    public Object[] toArray() {
        return managed.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return managed.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return !managed.contains(e) && managed.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return managed.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return managed.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean changed = false;
        for (E e : c) {
            if (add(e)) changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return managed.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return managed.retainAll(c);
    }

    @Override
    public void clear() {
        managed.clear();
    }

}
