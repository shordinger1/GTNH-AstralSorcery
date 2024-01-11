package shordinger.wrapper.net.minecraft.network.play.server;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.play.INetHandlerPlayClient;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;

public class SPacketBlockAction implements Packet<INetHandlerPlayClient> {

    private BlockPos blockPosition;
    private int instrument;
    private int pitch;
    private Block block;

    public SPacketBlockAction() {
    }

    public SPacketBlockAction(BlockPos pos, Block blockIn, int instrumentIn, int pitchIn) {
        this.blockPosition = pos;
        this.instrument = instrumentIn;
        this.pitch = pitchIn;
        this.block = blockIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.blockPosition = buf.readBlockPos();
        this.instrument = buf.readUnsignedByte();
        this.pitch = buf.readUnsignedByte();
        this.block = Block.getBlockById(buf.readVarInt() & 4095);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeBlockPos(this.blockPosition);
        buf.writeByte(this.instrument);
        buf.writeByte(this.pitch);
        buf.writeVarInt(Block.getIdFromBlock(this.block) & 4095);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleBlockAction(this);
    }

    @SideOnly(Side.CLIENT)
    public BlockPos getBlockPosition() {
        return this.blockPosition;
    }

    /**
     * instrument data for noteblocks
     */
    @SideOnly(Side.CLIENT)
    public int getData1() {
        return this.instrument;
    }

    /**
     * pitch data for noteblocks
     */
    @SideOnly(Side.CLIENT)
    public int getData2() {
        return this.pitch;
    }

    @SideOnly(Side.CLIENT)
    public Block getBlockType() {
        return this.block;
    }
}
