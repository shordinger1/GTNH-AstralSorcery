package shordinger.wrapper.net.minecraft.network.play.server;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.play.INetHandlerPlayClient;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;

public class SPacketDisconnect implements Packet<INetHandlerPlayClient> {

    private ITextComponent reason;

    public SPacketDisconnect() {
    }

    public SPacketDisconnect(ITextComponent messageIn) {
        this.reason = messageIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.reason = buf.readTextComponent();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeTextComponent(this.reason);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleDisconnect(this);
    }

    @SideOnly(Side.CLIENT)
    public ITextComponent getReason() {
        return this.reason;
    }
}
