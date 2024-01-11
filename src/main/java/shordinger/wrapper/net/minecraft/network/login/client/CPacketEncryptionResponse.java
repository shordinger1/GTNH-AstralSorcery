package shordinger.wrapper.net.minecraft.network.login.client;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.login.INetHandlerLoginServer;
import shordinger.wrapper.net.minecraft.util.CryptManager;

public class CPacketEncryptionResponse implements Packet<INetHandlerLoginServer> {

    private byte[] secretKeyEncrypted = new byte[0];
    private byte[] verifyTokenEncrypted = new byte[0];

    public CPacketEncryptionResponse() {
    }

    @SideOnly(Side.CLIENT)
    public CPacketEncryptionResponse(SecretKey secret, PublicKey key, byte[] verifyToken) {
        this.secretKeyEncrypted = CryptManager.encryptData(key, secret.getEncoded());
        this.verifyTokenEncrypted = CryptManager.encryptData(key, verifyToken);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.secretKeyEncrypted = buf.readByteArray();
        this.verifyTokenEncrypted = buf.readByteArray();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeByteArray(this.secretKeyEncrypted);
        buf.writeByteArray(this.verifyTokenEncrypted);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerLoginServer handler) {
        handler.processEncryptionResponse(this);
    }

    public SecretKey getSecretKey(PrivateKey key) {
        return CryptManager.decryptSharedKey(key, this.secretKeyEncrypted);
    }

    public byte[] getVerifyToken(PrivateKey key) {
        return key == null ? this.verifyTokenEncrypted : CryptManager.decryptData(key, this.verifyTokenEncrypted);
    }
}
