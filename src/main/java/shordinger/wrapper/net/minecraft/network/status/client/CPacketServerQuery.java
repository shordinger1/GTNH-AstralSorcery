package shordinger.wrapper.net.minecraft.network.status.client;

import java.io.IOException;

import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.status.INetHandlerStatusServer;

public class CPacketServerQuery implements Packet<INetHandlerStatusServer> {

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerStatusServer handler) {
        handler.processServerQuery(this);
    }
}
