/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util.data;

import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Tuple
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:14
 */
public class Tuple<K, V> {

    public final K key;
    public V value;

    public Tuple(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple tuple = (Tuple) o;
        return (Objects.equals(key, tuple.key)) && (Objects.equals(value, tuple.value));
    }

    @Override
    public int hashCode() {
        int result = key == null ? 0 : key.hashCode();
        result = 31 * result + (value == null ? 0 : value.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Tuple{" + "key=" + key + ", value=" + value + '}';
    }
}
