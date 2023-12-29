/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util.data;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.gameevent.TickEvent;
import shordinger.astralsorcery.common.auxiliary.tick.ITickHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TimeoutListMapContainer
 * Created by HellFirePvP
 * Date: 02.12.2016 / 21:20
 */
public class TimeoutListContainer<K, V> implements ITickHandler {

    private final EnumSet<TickEvent.Type> tickTypes;
    private final ContainerTimeoutDelegate<K, V> delegate;
    private final Map<K, TimeoutList<V>> timeoutListMap = new HashMap<>();

    public TimeoutListContainer(TickEvent.Type... restTypes) {
        this(null, restTypes);
    }

    public TimeoutListContainer(@Nullable ContainerTimeoutDelegate<K, V> delegate, TickEvent.Type... types) {
        this.tickTypes = EnumSet.noneOf(TickEvent.Type.class);
        for (TickEvent.Type type : types) {
            if (type != null) this.tickTypes.add(type);
        }
        this.delegate = delegate;
    }

    public boolean hasList(K key) {
        return timeoutListMap.containsKey(key);
    }

    @Nullable
    public TimeoutList<V> removeList(K key) {
        TimeoutList<V> ret = timeoutListMap.remove(key);
        ret.forEach((v) -> delegate.onContainerTimeout(key, v));
        return ret;
    }

    public TimeoutList<V> getOrCreateList(K key) {
        TimeoutList<V> list = timeoutListMap.get(key);
        if (list == null) {
            list = new TimeoutList<>(new RedirectTimeoutDelegate<>(key, delegate));
            timeoutListMap.put(key, list);
        }
        return list;
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        Iterator<Map.Entry<K, TimeoutList<V>>> it = timeoutListMap.entrySet()
            .iterator();
        while (it.hasNext()) {
            Map.Entry<K, TimeoutList<V>> entry = it.next();
            TimeoutList<V> list = entry.getValue();
            list.tick(type, context);
            if (list.isEmpty()) {
                it.remove();
            }
        }
    }

    public void clear() {
        Lists.newArrayList(timeoutListMap.keySet())
            .forEach(this::removeList);
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return tickTypes;
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "TimeoutListContainer";
    }

    private record RedirectTimeoutDelegate<K, V>(K key, ContainerTimeoutDelegate<K, V> delegate)
        implements TimeoutList.TimeoutDelegate<V> {

        private RedirectTimeoutDelegate(K key, @Nullable ContainerTimeoutDelegate<K, V> delegate) {
            this.key = key;
            this.delegate = delegate;
        }

        @Override
        public void onTimeout(V object) {
            if (delegate != null) {
                delegate.onContainerTimeout(key, object);
            }
        }

    }

    public static interface ContainerTimeoutDelegate<K, V> {

        public void onContainerTimeout(K key, V timedOut);

    }

}
