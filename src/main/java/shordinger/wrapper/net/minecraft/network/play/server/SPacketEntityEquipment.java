package shordinger.wrapper.net.minecraft.network.play.server;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.inventory.EntityEquipmentSlot;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketEntityEquipment implements Packet<INetHandlerPlayClient> {

    private int entityID;
    private EntityEquipmentSlot equipmentSlot;
    private ItemStack itemStack = ItemStack.EMPTY;

    public SPacketEntityEquipment() {
    }

    public SPacketEntityEquipment(int entityIdIn, EntityEquipmentSlot equipmentSlotIn, ItemStack itemStackIn) {
        this.entityID = entityIdIn;
        this.equipmentSlot = equipmentSlotIn;
        this.itemStack = itemStackIn.copy();
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.entityID = buf.readVarInt();
        this.equipmentSlot = (EntityEquipmentSlot) buf.readEnumValue(EntityEquipmentSlot.class);
        this.itemStack = buf.readItemStack();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.entityID);
        buf.writeEnumValue(this.equipmentSlot);
        buf.writeItemStack(this.itemStack);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleEntityEquipment(this);
    }

    @SideOnly(Side.CLIENT)
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    @SideOnly(Side.CLIENT)
    public int getEntityID() {
        return this.entityID;
    }

    @SideOnly(Side.CLIENT)
    public EntityEquipmentSlot getEquipmentSlot() {
        return this.equipmentSlot;
    }
}
