package shordinger.wrapper.net.minecraft.network.login.server;

import java.io.IOException;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.login.INetHandlerLoginClient;

public class SPacketLoginSuccess implements Packet<INetHandlerLoginClient> {

    private GameProfile profile;

    public SPacketLoginSuccess() {
    }

    public SPacketLoginSuccess(GameProfile profileIn) {
        this.profile = profileIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        String s = buf.readString(36);
        String s1 = buf.readString(16);
        UUID uuid = s.length() > 0 ? UUID.fromString(s) : null;
        this.profile = new GameProfile(uuid, s1);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        UUID uuid = this.profile.getId();
        buf.writeString(uuid == null ? "" : uuid.toString());
        buf.writeString(this.profile.getName());
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerLoginClient handler) {
        handler.handleLoginSuccess(this);
    }

    @SideOnly(Side.CLIENT)
    public GameProfile getProfile() {
        return this.profile;
    }
}
