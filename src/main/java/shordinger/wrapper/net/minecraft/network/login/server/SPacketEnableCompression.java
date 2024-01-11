package shordinger.wrapper.net.minecraft.network.login.server;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.login.INetHandlerLoginClient;

public class SPacketEnableCompression implements Packet<INetHandlerLoginClient> {

    private int compressionThreshold;

    public SPacketEnableCompression() {
    }

    public SPacketEnableCompression(int thresholdIn) {
        this.compressionThreshold = thresholdIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.compressionThreshold = buf.readVarInt();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.compressionThreshold);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerLoginClient handler) {
        handler.handleEnableCompression(this);
    }

    @SideOnly(Side.CLIENT)
    public int getCompressionThreshold() {
        return this.compressionThreshold;
    }
}
