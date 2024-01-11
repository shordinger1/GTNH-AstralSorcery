package shordinger.wrapper.net.minecraft.network.status;

import shordinger.wrapper.net.minecraft.network.INetHandler;
import shordinger.wrapper.net.minecraft.network.status.client.CPacketPing;
import shordinger.wrapper.net.minecraft.network.status.client.CPacketServerQuery;

public interface INetHandlerStatusServer extends INetHandler {

    void processPing(CPacketPing packetIn);

    void processServerQuery(CPacketServerQuery packetIn);
}
