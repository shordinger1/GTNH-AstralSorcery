package shordinger.wrapper.net.minecraft.network.status;

import shordinger.wrapper.net.minecraft.network.INetHandler;
import shordinger.wrapper.net.minecraft.network.status.server.SPacketPong;
import shordinger.wrapper.net.minecraft.network.status.server.SPacketServerInfo;

public interface INetHandlerStatusClient extends INetHandler {

    void handleServerInfo(SPacketServerInfo packetIn);

    void handlePong(SPacketPong packetIn);
}
