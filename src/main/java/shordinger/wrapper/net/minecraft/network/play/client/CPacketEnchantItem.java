package shordinger.wrapper.net.minecraft.network.play.client;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketEnchantItem implements Packet<INetHandlerPlayServer> {

    private int windowId;
    private int button;

    public CPacketEnchantItem() {
    }

    @SideOnly(Side.CLIENT)
    public CPacketEnchantItem(int windowIdIn, int buttonIn) {
        this.windowId = windowIdIn;
        this.button = buttonIn;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler) {
        handler.processEnchantItem(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.windowId = buf.readByte();
        this.button = buf.readByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeByte(this.windowId);
        buf.writeByte(this.button);
    }

    public int getWindowId() {
        return this.windowId;
    }

    public int getButton() {
        return this.button;
    }
}
