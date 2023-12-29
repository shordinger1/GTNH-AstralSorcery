/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;

import cpw.mods.fml.common.gameevent.TickEvent;
import shordinger.astralsorcery.common.auxiliary.tick.ITickHandler;

import shordinger.astralsorcery.common.util.data.Tuple;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CommonScheduler
 * Created by HellFirePvP
 * Date: 30.03.2017 / 22:23
 */
public class CommonScheduler implements ITickHandler {

    private static final Object lock = new Object();

    private boolean inTick = false;
    private final LinkedList<Tuple<Runnable, Integer>> queue = new LinkedList<>();
    private final LinkedList<Tuple<Runnable, Integer>> waiting = new LinkedList<>();

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        inTick = true;
        synchronized (lock) {
            inTick = true;
            Iterator<Tuple<Runnable, Integer>> iterator = queue.iterator();
            while (iterator.hasNext()) {
                Tuple<Runnable, Integer> r = iterator.next();
                r.value--;
                if (r.value <= 0) {
                    r.key.run();
                    iterator.remove();
                }
            }
            inTick = false;
            for (Tuple<Runnable, Integer> wait : waiting) {
                queue.addLast(new Tuple<>(wait.key, wait.value));
            }
        }
        waiting.clear();
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.SERVER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Common Scheduler";
    }

    public void addRunnable(Runnable r, int tickDelay) {
        synchronized (lock) {
            if (inTick) {
                waiting.addLast(new Tuple<>(r, tickDelay));
            } else {
                queue.addLast(new Tuple<>(r, tickDelay));
            }
        }
    }

}
