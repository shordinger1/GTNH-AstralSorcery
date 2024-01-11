package shordinger.wrapper.net.minecraft.network.play.client;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketConfirmTeleport implements Packet<INetHandlerPlayServer> {

    private int telportId;

    public CPacketConfirmTeleport() {
    }

    @SideOnly(Side.CLIENT)
    public CPacketConfirmTeleport(int teleportIdIn) {
        this.telportId = teleportIdIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.telportId = buf.readVarInt();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.telportId);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler) {
        handler.processConfirmTeleport(this);
    }

    public int getTeleportId() {
        return this.telportId;
    }
}
