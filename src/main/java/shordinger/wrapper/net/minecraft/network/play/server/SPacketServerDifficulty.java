package shordinger.wrapper.net.minecraft.network.play.server;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.play.INetHandlerPlayClient;
import shordinger.wrapper.net.minecraft.world.EnumDifficulty;

public class SPacketServerDifficulty implements Packet<INetHandlerPlayClient> {

    private EnumDifficulty difficulty;
    private boolean difficultyLocked;

    public SPacketServerDifficulty() {
    }

    public SPacketServerDifficulty(EnumDifficulty difficultyIn, boolean difficultyLockedIn) {
        this.difficulty = difficultyIn;
        this.difficultyLocked = difficultyLockedIn;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleServerDifficulty(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.difficulty = EnumDifficulty.getDifficultyEnum(buf.readUnsignedByte());
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeByte(this.difficulty.getDifficultyId());
    }

    @SideOnly(Side.CLIENT)
    public boolean isDifficultyLocked() {
        return this.difficultyLocked;
    }

    @SideOnly(Side.CLIENT)
    public EnumDifficulty getDifficulty() {
        return this.difficulty;
    }
}
