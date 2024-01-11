package shordinger.wrapper.net.minecraft.server.network;

import shordinger.wrapper.net.minecraft.network.NetworkManager;
import shordinger.wrapper.net.minecraft.network.status.INetHandlerStatusServer;
import shordinger.wrapper.net.minecraft.network.status.client.CPacketPing;
import shordinger.wrapper.net.minecraft.network.status.client.CPacketServerQuery;
import shordinger.wrapper.net.minecraft.network.status.server.SPacketPong;
import shordinger.wrapper.net.minecraft.network.status.server.SPacketServerInfo;
import shordinger.wrapper.net.minecraft.server.MinecraftServer;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;
import shordinger.wrapper.net.minecraft.util.text.TextComponentString;

public class NetHandlerStatusServer implements INetHandlerStatusServer {

    private static final ITextComponent EXIT_MESSAGE = new TextComponentString("Status request has been handled.");
    private final MinecraftServer server;
    private final NetworkManager networkManager;
    private boolean handled;

    public NetHandlerStatusServer(MinecraftServer serverIn, NetworkManager netManager) {
        this.server = serverIn;
        this.networkManager = netManager;
    }

    /**
     * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
     */
    public void onDisconnect(ITextComponent reason) {
    }

    public void processServerQuery(CPacketServerQuery packetIn) {
        if (this.handled) {
            this.networkManager.closeChannel(EXIT_MESSAGE);
        } else {
            this.handled = true;
            this.networkManager.sendPacket(new SPacketServerInfo(this.server.getServerStatusResponse()));
        }
    }

    public void processPing(CPacketPing packetIn) {
        this.networkManager.sendPacket(new SPacketPong(packetIn.getClientTime()));
        this.networkManager.closeChannel(EXIT_MESSAGE);
    }
}
