package shordinger.wrapper.net.minecraft.util.registry;

import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.HashBiMap;

import shordinger.wrapper.net.minecraft.util.IObjectIntIterable;

public class RegistryNamespaced<K, V> extends RegistrySimple<K, V> implements IObjectIntIterable<V> {
    // /**
    // * The backing store that maps Integers to objects.
    // */
    // protected final IntIdentityHashBiMap<V> underlyingIntegerMap = new IntIdentityHashBiMap(256);
    // /**
    // * A BiMap of objects (key) to their names (value).
    // */
    // protected final Map<V, K> inverseObjectRegistry;

    public net.minecraft.util.RegistryNamespaced oldData;

    public void setOldData(net.minecraft.util.RegistryNamespaced oldData) {
        this.oldData = oldData;
    }

    public RegistryNamespaced() {

    }

    public RegistryNamespaced(net.minecraft.util.RegistryNamespaced oldData) {
        this.oldData = oldData;
    }

    public void register(int id, K key, V value) {
        oldData.addObject(id, (String) key, value);
    }

    /**
     * Creates the Map we will use to map keys to their registered values.
     */
    protected Map<K, V> createUnderlyingMap() {
        return HashBiMap.<K, V>create();
    }

    @Nullable
    public V getObject(@Nullable K name) {
        return (V) super.getObject(name);
    }

    /**
     * Gets the name we use to identify the given object.
     */
    @Nullable
    public String getNameForObject(V value) {
        return this.oldData.getNameForObject(value);
    }

    /**
     * Does this registry contain an entry for the given key?
     */
    public boolean containsKey(K key) {
        return super.containsKey(key);
    }

    /**
     * Gets the integer ID we use to identify the given object.
     */
    public int getIDForObject(@Nullable V value) {
        return oldData.getIDForObject(value);
    }

    /**
     * Gets the object identified by the given ID.
     */
    @Nullable
    public V getObjectById(int id) {
        return (V) oldData.getObjectById(id);
    }

    public Iterator<V> iterator() {
        return oldData.iterator();
    }

}
