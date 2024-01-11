package shordinger.wrapper.net.minecraft.network.play.client;

import java.io.IOException;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.play.INetHandlerPlayServer;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;

public class CPacketTabComplete implements Packet<INetHandlerPlayServer> {

    private String message;
    private boolean hasTargetBlock;
    @Nullable
    private BlockPos targetBlock;

    public CPacketTabComplete() {
    }

    @SideOnly(Side.CLIENT)
    public CPacketTabComplete(String messageIn, @Nullable BlockPos targetBlockIn, boolean hasTargetBlockIn) {
        this.message = messageIn;
        this.targetBlock = targetBlockIn;
        this.hasTargetBlock = hasTargetBlockIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.message = buf.readString(32767);
        this.hasTargetBlock = buf.readBoolean();
        boolean flag = buf.readBoolean();

        if (flag) {
            this.targetBlock = buf.readBlockPos();
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeString(StringUtils.substring(this.message, 0, 32767));
        buf.writeBoolean(this.hasTargetBlock);
        boolean flag = this.targetBlock != null;
        buf.writeBoolean(flag);

        if (flag) {
            buf.writeBlockPos(this.targetBlock);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler) {
        handler.processTabComplete(this);
    }

    public String getMessage() {
        return this.message;
    }

    @Nullable
    public BlockPos getTargetBlock() {
        return this.targetBlock;
    }

    public boolean hasTargetBlock() {
        return this.hasTargetBlock;
    }
}
