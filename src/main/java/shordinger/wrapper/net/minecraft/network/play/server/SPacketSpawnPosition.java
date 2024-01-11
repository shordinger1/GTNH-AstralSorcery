package shordinger.wrapper.net.minecraft.network.play.server;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.play.INetHandlerPlayClient;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;

public class SPacketSpawnPosition implements Packet<INetHandlerPlayClient> {

    private BlockPos spawnBlockPos;

    public SPacketSpawnPosition() {
    }

    public SPacketSpawnPosition(BlockPos posIn) {
        this.spawnBlockPos = posIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.spawnBlockPos = buf.readBlockPos();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeBlockPos(this.spawnBlockPos);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleSpawnPosition(this);
    }

    @SideOnly(Side.CLIENT)
    public BlockPos getSpawnPos() {
        return this.spawnBlockPos;
    }
}
