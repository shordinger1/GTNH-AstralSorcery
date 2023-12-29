/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.registry.internal;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: InternalRegistryPrimer
 * Created by HellFirePvP
 * Date: 26.06.2017 / 14:49
 */
public class InternalRegistryPrimer {

    private Map<Class<?>, List<IForgeRegistryEntry<?>>> primed = new HashMap<>();

    public <V extends IForgeRegistryEntry<V>> V register(V entry) {
        Class<V> type = entry.getRegistryType();
        List<IForgeRegistryEntry<?>> entries = primed.get(type);
        if (entries == null) {
            entries = Lists.newLinkedList();
            primed.put(type, entries);
        }
        entries.add(entry);
        return entry;
    }

    <T extends IForgeRegistryEntry<T>> List<?> getEntries(Class<T> type) {
        return primed.get(type);
    }

    void wipe(Class<?> type) {
        primed.remove(type);
    }

}
