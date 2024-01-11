package shordinger.wrapper.net.minecraft.network.status.server;

import java.io.IOException;

import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.status.INetHandlerStatusClient;

public class SPacketPong implements Packet<INetHandlerStatusClient> {

    private long clientTime;

    public SPacketPong() {
    }

    public SPacketPong(long clientTimeIn) {
        this.clientTime = clientTimeIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.clientTime = buf.readLong();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeLong(this.clientTime);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerStatusClient handler) {
        handler.handlePong(this);
    }
}
