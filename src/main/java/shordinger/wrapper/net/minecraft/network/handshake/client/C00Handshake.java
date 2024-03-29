package shordinger.wrapper.net.minecraft.network.handshake.client;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.network.EnumConnectionState;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.handshake.INetHandlerHandshakeServer;

public class C00Handshake implements Packet<INetHandlerHandshakeServer> {

    private int protocolVersion;
    private String ip;
    private int port;
    private EnumConnectionState requestedState;
    private boolean hasFMLMarker = false;

    public C00Handshake() {
    }

    @SideOnly(Side.CLIENT)
    public C00Handshake(String p_i47613_1_, int p_i47613_2_, EnumConnectionState p_i47613_3_) {
        this.protocolVersion = 340;
        this.ip = p_i47613_1_;
        this.port = p_i47613_2_;
        this.requestedState = p_i47613_3_;
    }

    public C00Handshake(String address, int port, EnumConnectionState state, boolean addFMLMarker) {
        this(address, port, state);
        this.hasFMLMarker = addFMLMarker;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.protocolVersion = buf.readVarInt();
        this.ip = buf.readString(255);
        this.port = buf.readUnsignedShort();
        this.requestedState = EnumConnectionState.getById(buf.readVarInt());
        this.hasFMLMarker = this.ip.contains("\0FML\0");
        this.ip = this.ip.split("\0")[0];
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.protocolVersion);
        buf.writeString(this.ip + "\0FML\0");
        buf.writeShort(this.port);
        buf.writeVarInt(this.requestedState.getId());
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerHandshakeServer handler) {
        handler.processHandshake(this);
    }

    public EnumConnectionState getRequestedState() {
        return this.requestedState;
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    public boolean hasFMLMarker() {
        return this.hasFMLMarker;
    }
}
