package shordinger.wrapper.net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.EntityList;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.datasync.EntityDataManager;
import shordinger.wrapper.net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketSpawnMob implements Packet<INetHandlerPlayClient> {

    private int entityId;
    private UUID uniqueId;
    private int type;
    private double x;
    private double y;
    private double z;
    private int velocityX;
    private int velocityY;
    private int velocityZ;
    private byte yaw;
    private byte pitch;
    private byte headPitch;
    private EntityDataManager dataManager;
    private List<EntityDataManager.DataEntry<?>> dataManagerEntries;

    public SPacketSpawnMob() {
    }

    public SPacketSpawnMob(EntityLivingBase entityIn) {
        this.entityId = entityIn.getEntityId();
        this.uniqueId = entityIn.getUniqueID();
        this.type = EntityList.getID(entityIn.getClass());
        this.x = entityIn.posX;
        this.y = entityIn.posY;
        this.z = entityIn.posZ;
        this.yaw = (byte) ((int) (entityIn.rotationYaw * 256.0F / 360.0F));
        this.pitch = (byte) ((int) (entityIn.rotationPitch * 256.0F / 360.0F));
        this.headPitch = (byte) ((int) (entityIn.rotationYawHead * 256.0F / 360.0F));
        double d0 = 3.9D;
        double d1 = entityIn.motionX;
        double d2 = entityIn.motionY;
        double d3 = entityIn.motionZ;

        if (d1 < -3.9D) {
            d1 = -3.9D;
        }

        if (d2 < -3.9D) {
            d2 = -3.9D;
        }

        if (d3 < -3.9D) {
            d3 = -3.9D;
        }

        if (d1 > 3.9D) {
            d1 = 3.9D;
        }

        if (d2 > 3.9D) {
            d2 = 3.9D;
        }

        if (d3 > 3.9D) {
            d3 = 3.9D;
        }

        this.velocityX = (int) (d1 * 8000.0D);
        this.velocityY = (int) (d2 * 8000.0D);
        this.velocityZ = (int) (d3 * 8000.0D);
        this.dataManager = entityIn.getDataManager();
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.entityId = buf.readVarInt();
        this.uniqueId = buf.readUniqueId();
        this.type = buf.readVarInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readByte();
        this.pitch = buf.readByte();
        this.headPitch = buf.readByte();
        this.velocityX = buf.readShort();
        this.velocityY = buf.readShort();
        this.velocityZ = buf.readShort();
        this.dataManagerEntries = EntityDataManager.readEntries(buf);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.entityId);
        buf.writeUniqueId(this.uniqueId);
        buf.writeVarInt(this.type);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeByte(this.yaw);
        buf.writeByte(this.pitch);
        buf.writeByte(this.headPitch);
        buf.writeShort(this.velocityX);
        buf.writeShort(this.velocityY);
        buf.writeShort(this.velocityZ);
        this.dataManager.writeEntries(buf);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleSpawnMob(this);
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public List<EntityDataManager.DataEntry<?>> getDataManagerEntries() {
        return this.dataManagerEntries;
    }

    @SideOnly(Side.CLIENT)
    public int getEntityID() {
        return this.entityId;
    }

    @SideOnly(Side.CLIENT)
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @SideOnly(Side.CLIENT)
    public int getEntityType() {
        return this.type;
    }

    @SideOnly(Side.CLIENT)
    public double getX() {
        return this.x;
    }

    @SideOnly(Side.CLIENT)
    public double getY() {
        return this.y;
    }

    @SideOnly(Side.CLIENT)
    public double getZ() {
        return this.z;
    }

    @SideOnly(Side.CLIENT)
    public int getVelocityX() {
        return this.velocityX;
    }

    @SideOnly(Side.CLIENT)
    public int getVelocityY() {
        return this.velocityY;
    }

    @SideOnly(Side.CLIENT)
    public int getVelocityZ() {
        return this.velocityZ;
    }

    @SideOnly(Side.CLIENT)
    public byte getYaw() {
        return this.yaw;
    }

    @SideOnly(Side.CLIENT)
    public byte getPitch() {
        return this.pitch;
    }

    @SideOnly(Side.CLIENT)
    public byte getHeadPitch() {
        return this.headPitch;
    }
}
