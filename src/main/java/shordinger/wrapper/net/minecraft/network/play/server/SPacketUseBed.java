package shordinger.wrapper.net.minecraft.network.play.server;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.play.INetHandlerPlayClient;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

public class SPacketUseBed implements Packet<INetHandlerPlayClient> {

    private int playerID;
    /**
     * Block location of the head part of the bed
     */
    private BlockPos bedPos;

    public SPacketUseBed() {
    }

    public SPacketUseBed(EntityPlayer player, BlockPos posIn) {
        this.playerID = player.getEntityId();
        this.bedPos = posIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.playerID = buf.readVarInt();
        this.bedPos = buf.readBlockPos();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.playerID);
        buf.writeBlockPos(this.bedPos);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleUseBed(this);
    }

    @SideOnly(Side.CLIENT)
    public EntityPlayer getPlayer(World worldIn) {
        return (EntityPlayer) worldIn.getEntityByID(this.playerID);
    }

    @SideOnly(Side.CLIENT)
    public BlockPos getBedPosition() {
        return this.bedPos;
    }
}
