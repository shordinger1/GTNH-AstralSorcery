package shordinger.wrapper.net.minecraft.network.play.server;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.play.INetHandlerPlayClient;
import shordinger.wrapper.net.minecraft.world.World;

public class SPacketEntityHeadLook implements Packet<INetHandlerPlayClient> {

    private int entityId;
    private byte yaw;

    public SPacketEntityHeadLook() {
    }

    public SPacketEntityHeadLook(Entity entityIn, byte yawIn) {
        this.entityId = entityIn.getEntityId();
        this.yaw = yawIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.entityId = buf.readVarInt();
        this.yaw = buf.readByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.entityId);
        buf.writeByte(this.yaw);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleEntityHeadLook(this);
    }

    @SideOnly(Side.CLIENT)
    public Entity getEntity(World worldIn) {
        return worldIn.getEntityByID(this.entityId);
    }

    @SideOnly(Side.CLIENT)
    public byte getYaw() {
        return this.yaw;
    }
}
