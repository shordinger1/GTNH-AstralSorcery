package shordinger.wrapper.net.minecraft.network.play.server;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketDestroyEntities implements Packet<INetHandlerPlayClient> {

    private int[] entityIDs;

    public SPacketDestroyEntities() {
    }

    public SPacketDestroyEntities(int... entityIdsIn) {
        this.entityIDs = entityIdsIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.entityIDs = new int[buf.readVarInt()];

        for (int i = 0; i < this.entityIDs.length; ++i) {
            this.entityIDs[i] = buf.readVarInt();
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.entityIDs.length);

        for (int i : this.entityIDs) {
            buf.writeVarInt(i);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleDestroyEntities(this);
    }

    @SideOnly(Side.CLIENT)
    public int[] getEntityIDs() {
        return this.entityIDs;
    }
}
