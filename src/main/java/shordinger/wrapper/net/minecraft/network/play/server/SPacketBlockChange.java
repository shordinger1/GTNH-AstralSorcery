package shordinger.wrapper.net.minecraft.network.play.server;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.play.INetHandlerPlayClient;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

public class SPacketBlockChange implements Packet<INetHandlerPlayClient> {

    private BlockPos blockPosition;
    public IBlockState blockState;

    public SPacketBlockChange() {
    }

    public SPacketBlockChange(World worldIn, BlockPos posIn) {
        this.blockPosition = posIn;
        this.blockState = worldIn.getBlockState(posIn);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.blockPosition = buf.readBlockPos();
        this.blockState = Block.BLOCK_STATE_IDS.getByValue(buf.readVarInt());
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeBlockPos(this.blockPosition);
        buf.writeVarInt(Block.BLOCK_STATE_IDS.get(this.blockState));
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleBlockChange(this);
    }

    @SideOnly(Side.CLIENT)
    public IBlockState getBlockState() {
        return this.blockState;
    }

    @SideOnly(Side.CLIENT)
    public BlockPos getBlockPosition() {
        return this.blockPosition;
    }
}
