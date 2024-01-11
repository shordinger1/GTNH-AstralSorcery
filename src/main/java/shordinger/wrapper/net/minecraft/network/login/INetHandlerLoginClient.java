package shordinger.wrapper.net.minecraft.network.login;

import shordinger.wrapper.net.minecraft.network.INetHandler;
import shordinger.wrapper.net.minecraft.network.login.server.SPacketDisconnect;
import shordinger.wrapper.net.minecraft.network.login.server.SPacketEnableCompression;
import shordinger.wrapper.net.minecraft.network.login.server.SPacketEncryptionRequest;
import shordinger.wrapper.net.minecraft.network.login.server.SPacketLoginSuccess;

public interface INetHandlerLoginClient extends INetHandler {

    void handleEncryptionRequest(SPacketEncryptionRequest packetIn);

    void handleLoginSuccess(SPacketLoginSuccess packetIn);

    void handleDisconnect(SPacketDisconnect packetIn);

    void handleEnableCompression(SPacketEnableCompression packetIn);
}
