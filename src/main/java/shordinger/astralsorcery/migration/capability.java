//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package shordinger.astralsorcery.migration;

import java.util.concurrent.Callable;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.util.ForgeDirection;

public class Capability<T> {

    private final String name;
    private final IStorage<T> storage;
    private final Callable<? extends T> factory;

    public String getName() {
        return this.name;
    }

    public IStorage<T> getStorage() {
        return this.storage;
    }

    public void readNBT(T instance, ForgeDirection side, NBTBase nbt) {
        this.storage.readNBT(this, instance, side, nbt);
    }

    @Nullable
    public NBTBase writeNBT(T instance, ForgeDirection side) {
        return this.storage.writeNBT(this, instance, side);
    }

    @Nullable
    public T getDefaultInstance() {
        try {
            return this.factory.call();
        } catch (Exception var2) {
            // Throwables.throwIfUnchecked(var2);
            throw new RuntimeException(var2);
        }
    }

    public <R> R cast(T instance) {
        return (R) instance;
    }

    Capability(String name, IStorage<T> storage, Callable<? extends T> factory) {
        this.name = name;
        this.storage = storage;
        this.factory = factory;
    }

    public interface IStorage<T> {

        @Nullable
        NBTBase writeNBT(Capability<T> var1, T var2, ForgeDirection var3);

        void readNBT(Capability<T> var1, T var2, ForgeDirection var3, NBTBase var4);
    }
}
