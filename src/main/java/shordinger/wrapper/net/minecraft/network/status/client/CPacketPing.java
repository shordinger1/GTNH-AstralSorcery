package shordinger.wrapper.net.minecraft.network.status.client;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.status.INetHandlerStatusServer;

public class CPacketPing implements Packet<INetHandlerStatusServer> {

    private long clientTime;

    public CPacketPing() {
    }

    @SideOnly(Side.CLIENT)
    public CPacketPing(long clientTimeIn) {
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
    public void processPacket(INetHandlerStatusServer handler) {
        handler.processPing(this);
    }

    public long getClientTime() {
        return this.clientTime;
    }
}
