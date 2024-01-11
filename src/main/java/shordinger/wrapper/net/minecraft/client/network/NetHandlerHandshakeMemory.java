package shordinger.wrapper.net.minecraft.client.network;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.network.NetworkManager;
import shordinger.wrapper.net.minecraft.network.handshake.INetHandlerHandshakeServer;
import shordinger.wrapper.net.minecraft.network.handshake.client.C00Handshake;
import shordinger.wrapper.net.minecraft.server.MinecraftServer;
import shordinger.wrapper.net.minecraft.server.network.NetHandlerLoginServer;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;

@SideOnly(Side.CLIENT)
public class NetHandlerHandshakeMemory implements INetHandlerHandshakeServer {

    private final MinecraftServer mcServer;
    private final NetworkManager networkManager;

    public NetHandlerHandshakeMemory(MinecraftServer mcServerIn, NetworkManager networkManagerIn) {
        this.mcServer = mcServerIn;
        this.networkManager = networkManagerIn;
    }

    /**
     * There are two recognized intentions for initiating a handshake: logging in and acquiring server status. The
     * NetworkManager's protocol will be reconfigured according to the specified intention, although a login-intention
     * must pass a versioncheck or receive a disconnect otherwise
     */
    public void processHandshake(C00Handshake packetIn) {
        if (!net.minecraftforge.fml.common.FMLCommonHandler.instance()
            .handleServerHandshake(packetIn, this.networkManager)) return;
        this.networkManager.setConnectionState(packetIn.getRequestedState());
        this.networkManager.setNetHandler(new NetHandlerLoginServer(this.mcServer, this.networkManager));
    }

    /**
     * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
     */
    public void onDisconnect(ITextComponent reason) {
    }
}
