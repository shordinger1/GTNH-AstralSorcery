package shordinger.wrapper.net.minecraft.network.play.server;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.play.INetHandlerPlayClient;
import shordinger.wrapper.net.minecraft.world.World;

public class SPacketEntityStatus implements Packet<INetHandlerPlayClient> {

    private int entityId;
    private byte logicOpcode;

    public SPacketEntityStatus() {
    }

    public SPacketEntityStatus(Entity entityIn, byte opcodeIn) {
        this.entityId = entityIn.getEntityId();
        this.logicOpcode = opcodeIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.entityId = buf.readInt();
        this.logicOpcode = buf.readByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeInt(this.entityId);
        buf.writeByte(this.logicOpcode);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleEntityStatus(this);
    }

    @SideOnly(Side.CLIENT)
    public Entity getEntity(World worldIn) {
        return worldIn.getEntityByID(this.entityId);
    }

    @SideOnly(Side.CLIENT)
    public byte getOpCode() {
        return this.logicOpcode;
    }
}
