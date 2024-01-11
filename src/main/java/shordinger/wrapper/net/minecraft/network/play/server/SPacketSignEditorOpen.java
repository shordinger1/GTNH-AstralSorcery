package shordinger.wrapper.net.minecraft.network.play.server;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.play.INetHandlerPlayClient;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;

public class SPacketSignEditorOpen implements Packet<INetHandlerPlayClient> {

    private BlockPos signPosition;

    public SPacketSignEditorOpen() {
    }

    public SPacketSignEditorOpen(BlockPos posIn) {
        this.signPosition = posIn;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleSignEditorOpen(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.signPosition = buf.readBlockPos();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeBlockPos(this.signPosition);
    }

    @SideOnly(Side.CLIENT)
    public BlockPos getSignPosition() {
        return this.signPosition;
    }
}
