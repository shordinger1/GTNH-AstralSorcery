package shordinger.wrapper.net.minecraft.util.registry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

public class RegistrySimple<K, V> implements IRegistry<K, V> {

    net.minecraft.util.RegistrySimple oldData;
    private ArrayList<V> values;

    public void setOldData(net.minecraft.util.RegistrySimple oldData) {
        this.oldData = oldData;
    }

    @Nullable
    public V getObject(@Nullable K name) {
        return (V) this.oldData.getObject(name);
    }

    /**
     * Register an object on this registry.
     */
    public void putObject(K key, V value) {
        oldData.putObject(key, value);
    }

    /**
     * Gets all the keys recognized by this registry.
     */
    public Set<K> getKeys() {
        return oldData.getKeys();
    }

    @Nullable
    public V getRandomObject(Random random) {
        if (this.values == null) {
            ArrayList<V> collection = new ArrayList<>();
            for (var key : getKeys()) {
                collection.add(getObject(key));
            }
            if (collection.isEmpty()) {
                return (V) null;
            }
            this.values = collection;
        }

        return this.values.get(random.nextInt(this.values.size()));
    }

    /**
     * Does this registry contain an entry for the given key?
     */
    public boolean containsKey(K key) {
        return oldData.containsKey(key);
    }

    public Iterator<V> iterator() {
        return values.iterator();
    }
}
