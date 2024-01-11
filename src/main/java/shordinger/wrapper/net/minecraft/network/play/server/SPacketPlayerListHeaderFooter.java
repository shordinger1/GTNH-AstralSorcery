package shordinger.wrapper.net.minecraft.network.play.server;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.play.INetHandlerPlayClient;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;

public class SPacketPlayerListHeaderFooter implements Packet<INetHandlerPlayClient> {

    private ITextComponent header;
    private ITextComponent footer;

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.header = buf.readTextComponent();
        this.footer = buf.readTextComponent();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeTextComponent(this.header);
        buf.writeTextComponent(this.footer);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handlePlayerListHeaderFooter(this);
    }

    @SideOnly(Side.CLIENT)
    public ITextComponent getHeader() {
        return this.header;
    }

    @SideOnly(Side.CLIENT)
    public ITextComponent getFooter() {
        return this.footer;
    }
}
