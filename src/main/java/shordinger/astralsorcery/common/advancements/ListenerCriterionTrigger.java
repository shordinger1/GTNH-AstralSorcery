/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import shordinger.wrapper.net.minecraft.advancements.ICriterionInstance;
import shordinger.wrapper.net.minecraft.advancements.ICriterionTrigger;
import shordinger.wrapper.net.minecraft.advancements.PlayerAdvancements;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ListenerCriterionTrigger
 * Created by HellFirePvP
 * Date: 27.10.2018 / 10:57
 */
public abstract class ListenerCriterionTrigger<T extends ICriterionInstance> implements ICriterionTrigger<T> {

    protected final Map<PlayerAdvancements, Listeners<T>> listeners = Maps.newHashMap();
    private final ResourceLocation id;

    public ListenerCriterionTrigger(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public final ResourceLocation getId() {
        return id;
    }

    public final void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<T> listener) {
        Listeners<T> listeners = this.listeners.get(playerAdvancementsIn);

        if (listeners == null) {
            listeners = new Listeners<>(playerAdvancementsIn);
            this.listeners.put(playerAdvancementsIn, listeners);
        }

        listeners.add(listener);
    }

    public final void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<T> listener) {
        Listeners<T> listeners = this.listeners.get(playerAdvancementsIn);

        if (listeners != null) {
            listeners.remove(listener);

            if (listeners.isEmpty()) {
                this.listeners.remove(playerAdvancementsIn);
            }
        }
    }

    public final void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
        this.listeners.remove(playerAdvancementsIn);
    }

    public static class Listeners<T extends ICriterionInstance> {

        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<T>> listeners = Sets.newHashSet();

        public Listeners(PlayerAdvancements playerAdvancementsIn) {
            this.playerAdvancements = playerAdvancementsIn;
        }

        public final boolean isEmpty() {
            return this.listeners.isEmpty();
        }

        public final void add(ICriterionTrigger.Listener<T> listener) {
            this.listeners.add(listener);
        }

        public final void remove(ICriterionTrigger.Listener<T> listener) {
            this.listeners.remove(listener);
        }

        public final void trigger(Predicate<T> test) {
            List<Listener<T>> list = Lists.newArrayList();

            for (ICriterionTrigger.Listener<T> listener : this.listeners) {
                if (test.test(listener.getCriterionInstance())) {
                    list.add(listener);
                }
            }

            for (ICriterionTrigger.Listener<T> listener1 : list) {
                listener1.grantCriterion(this.playerAdvancements);
            }
        }
    }

}
