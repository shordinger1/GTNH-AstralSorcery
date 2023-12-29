package shordinger.astralsorcery.migration.EntityData;

import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public interface DataSerializer<T> {

    void write(PacketBuffer var1, T var2);

    T read(PacketBuffer var1) throws IOException;

    DataParameter<T> createKey(int var1);

    T copyValue(T var1);
}
