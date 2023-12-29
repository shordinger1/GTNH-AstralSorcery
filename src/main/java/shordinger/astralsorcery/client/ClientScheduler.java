/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;

import cpw.mods.fml.common.gameevent.TickEvent;

import shordinger.astralsorcery.common.auxiliary.tick.ITickHandler;
import shordinger.astralsorcery.common.util.Counter;
import shordinger.astralsorcery.common.util.data.Tuple;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientScheduler
 * Created by HellFirePvP
 * Date: 11.08.2016 / 13:04
 */
public class ClientScheduler implements ITickHandler {

    private static long clientTick = 0;
    private static final Object lock = new Object();

    private boolean inTick = false;
    private LinkedList<Tuple<Runnable, Counter>> queue = new LinkedList<>();
    private LinkedList<Tuple<Runnable, Integer>> waiting = new LinkedList<>();

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        clientTick++;

        inTick = true;
        synchronized (lock) {
            inTick = true;
            Iterator<Tuple<Runnable, Counter>> iterator = queue.iterator();
            while (iterator.hasNext()) {
                Tuple<Runnable, Counter> r = iterator.next();
                r.value.decrement();
                if (r.value.value <= 0) {
                    r.key.run();
                    iterator.remove();
                }
            }
            inTick = false;
            for (Tuple<Runnable, Integer> wait : waiting) {
                queue.addLast(new Tuple<>(wait.key, new Counter(wait.value)));
            }
        }
        waiting.clear();
    }

    public static long getClientTick() {
        return clientTick;
    }

    public static long getIndependentClientTick() {
        return System.currentTimeMillis() / 50;
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.CLIENT);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Client Scheduler";
    }

    public void addRunnable(Runnable r, int tickDelay) {
        synchronized (lock) {
            if (inTick) {
                waiting.addLast(new Tuple<>(r, tickDelay));
            } else {
                queue.addLast(new Tuple<>(r, new Counter(tickDelay)));
            }
        }
    }

}
