package shordinger.wrapper.net.minecraft.server.network;

import shordinger.wrapper.net.minecraft.network.EnumConnectionState;
import shordinger.wrapper.net.minecraft.network.NetworkManager;
import shordinger.wrapper.net.minecraft.network.handshake.INetHandlerHandshakeServer;
import shordinger.wrapper.net.minecraft.network.handshake.client.C00Handshake;
import shordinger.wrapper.net.minecraft.network.login.server.SPacketDisconnect;
import shordinger.wrapper.net.minecraft.server.MinecraftServer;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;
import shordinger.wrapper.net.minecraft.util.text.TextComponentTranslation;

public class NetHandlerHandshakeTCP implements INetHandlerHandshakeServer {

    private final MinecraftServer server;
    private final NetworkManager networkManager;

    public NetHandlerHandshakeTCP(MinecraftServer serverIn, NetworkManager netManager) {
        this.server = serverIn;
        this.networkManager = netManager;
    }

    /**
     * There are two recognized intentions for initiating a handshake: logging in and acquiring server status. The
     * NetworkManager's protocol will be reconfigured according to the specified intention, although a login-intention
     * must pass a versioncheck or receive a disconnect otherwise
     */
    public void processHandshake(C00Handshake packetIn) {
        if (!net.minecraftforge.fml.common.FMLCommonHandler.instance()
            .handleServerHandshake(packetIn, this.networkManager)) return;

        switch (packetIn.getRequestedState()) {
            case LOGIN:
                this.networkManager.setConnectionState(EnumConnectionState.LOGIN);

                if (packetIn.getProtocolVersion() > 340) {
                    ITextComponent itextcomponent = new TextComponentTranslation(
                        "multiplayer.disconnect.outdated_server",
                        new Object[]{"1.12.2"});
                    this.networkManager.sendPacket(new SPacketDisconnect(itextcomponent));
                    this.networkManager.closeChannel(itextcomponent);
                } else if (packetIn.getProtocolVersion() < 340) {
                    ITextComponent itextcomponent1 = new TextComponentTranslation(
                        "multiplayer.disconnect.outdated_client",
                        new Object[]{"1.12.2"});
                    this.networkManager.sendPacket(new SPacketDisconnect(itextcomponent1));
                    this.networkManager.closeChannel(itextcomponent1);
                } else {
                    this.networkManager.setNetHandler(new NetHandlerLoginServer(this.server, this.networkManager));
                }

                break;
            case STATUS:
                this.networkManager.setConnectionState(EnumConnectionState.STATUS);
                this.networkManager.setNetHandler(new NetHandlerStatusServer(this.server, this.networkManager));
                break;
            default:
                throw new UnsupportedOperationException("Invalid intention " + packetIn.getRequestedState());
        }
    }

    /**
     * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
     */
    public void onDisconnect(ITextComponent reason) {
    }
}
