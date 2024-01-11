package shordinger.wrapper.net.minecraft.network.play.server;

import java.io.IOException;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.play.INetHandlerPlayClient;
import shordinger.wrapper.net.minecraft.world.World;

public class SPacketCamera implements Packet<INetHandlerPlayClient> {

    public int entityId;

    public SPacketCamera() {
    }

    public SPacketCamera(Entity entityIn) {
        this.entityId = entityIn.getEntityId();
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.entityId = buf.readVarInt();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.entityId);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleCamera(this);
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public Entity getEntity(World worldIn) {
        return worldIn.getEntityByID(this.entityId);
    }
}
