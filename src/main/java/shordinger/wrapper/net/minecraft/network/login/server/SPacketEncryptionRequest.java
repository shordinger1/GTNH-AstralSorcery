package shordinger.wrapper.net.minecraft.network.login.server;

import java.io.IOException;
import java.security.PublicKey;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.login.INetHandlerLoginClient;
import shordinger.wrapper.net.minecraft.util.CryptManager;

public class SPacketEncryptionRequest implements Packet<INetHandlerLoginClient> {

    private String hashedServerId;
    private PublicKey publicKey;
    private byte[] verifyToken;

    public SPacketEncryptionRequest() {
    }

    public SPacketEncryptionRequest(String serverIdIn, PublicKey publicKeyIn, byte[] verifyTokenIn) {
        this.hashedServerId = serverIdIn;
        this.publicKey = publicKeyIn;
        this.verifyToken = verifyTokenIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.hashedServerId = buf.readString(20);
        this.publicKey = CryptManager.decodePublicKey(buf.readByteArray());
        this.verifyToken = buf.readByteArray();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeString(this.hashedServerId);
        buf.writeByteArray(this.publicKey.getEncoded());
        buf.writeByteArray(this.verifyToken);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerLoginClient handler) {
        handler.handleEncryptionRequest(this);
    }

    @SideOnly(Side.CLIENT)
    public String getServerId() {
        return this.hashedServerId;
    }

    @SideOnly(Side.CLIENT)
    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    @SideOnly(Side.CLIENT)
    public byte[] getVerifyToken() {
        return this.verifyToken;
    }
}
