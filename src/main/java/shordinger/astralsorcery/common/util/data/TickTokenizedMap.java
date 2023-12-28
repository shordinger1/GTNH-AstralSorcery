/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util.data;

import java.util.EnumSet;
import java.util.Iterator;

import javax.annotation.Nonnull;

import cpw.mods.fml.common.gameevent.TickEvent;
import shordinger.astralsorcery.common.auxiliary.tick.ITickHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TickTokenizedMap
 * Created by HellFirePvP
 * Date: 07.11.2016 / 11:25
 */
public class TickTokenizedMap<K, V extends TickTokenizedMap.TickMapToken<?>> extends TokenizedMap<K, V>
    implements ITickHandler {

    private final EnumSet<TickEvent.Type> tickTypes;

    public TickTokenizedMap(@Nonnull TickEvent.Type first, TickEvent.Type... restTypes) {
        this.tickTypes = EnumSet.of(first, restTypes);
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        Iterator<Entry<K, V>> iteratorEntries = entrySet().iterator();
        while (iteratorEntries.hasNext()) {
            Entry<K, V> entry = iteratorEntries.next();
            entry.getValue()
                .tick();
            if (entry.getValue()
                .getRemainingTimeout() <= 0) {
                entry.getValue()
                    .onTimeout();
                iteratorEntries.remove();
            }
        }
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
        return "TickTokenMap";
    }

    public static class SimpleTickToken<E> implements TickMapToken<E> {

        @Nonnull
        private E value;
        private int timeout;

        public SimpleTickToken(@Nonnull E value, int initialTimeout) {
            this.value = value;
            this.timeout = initialTimeout;
        }

        @Override
        public int getRemainingTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public void addToTimeout(int timeout) {
            this.timeout += timeout;
        }

        @Override
        public void tick() {
            timeout--;
        }

        @Override
        public void onTimeout() {
        }

        @Override
        @Nonnull
        public E getValue() {
            return value;
        }

        public void setValue(@Nonnull E value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SimpleTickToken that = (SimpleTickToken) o;
            return value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

    public static interface TickMapToken<E> extends MapToken<E> {

        public int getRemainingTimeout();

        public void tick();

        void onTimeout();
    }

}
