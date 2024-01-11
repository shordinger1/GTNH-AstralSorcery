package shordinger.wrapper.net.minecraft.network.login;

import shordinger.wrapper.net.minecraft.network.INetHandler;
import shordinger.wrapper.net.minecraft.network.login.client.CPacketEncryptionResponse;
import shordinger.wrapper.net.minecraft.network.login.client.CPacketLoginStart;

public interface INetHandlerLoginServer extends INetHandler {

    void processLoginStart(CPacketLoginStart packetIn);

    void processEncryptionResponse(CPacketEncryptionResponse packetIn);
}
