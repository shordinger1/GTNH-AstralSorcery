package shordinger.wrapper.net.minecraft.network.play.server;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.network.play.INetHandlerPlayClient;
import shordinger.wrapper.net.minecraft.world.EnumDifficulty;
import shordinger.wrapper.net.minecraft.world.GameType;
import shordinger.wrapper.net.minecraft.world.WorldType;

public class SPacketRespawn implements Packet<INetHandlerPlayClient> {

    private int dimensionID;
    private EnumDifficulty difficulty;
    private GameType gameType;
    private WorldType worldType;

    public SPacketRespawn() {
    }

    public SPacketRespawn(int dimensionIdIn, EnumDifficulty difficultyIn, WorldType worldTypeIn, GameType gameModeIn) {
        this.dimensionID = dimensionIdIn;
        this.difficulty = difficultyIn;
        this.gameType = gameModeIn;
        this.worldType = worldTypeIn;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleRespawn(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.dimensionID = buf.readInt();
        this.difficulty = EnumDifficulty.getDifficultyEnum(buf.readUnsignedByte());
        this.gameType = GameType.getByID(buf.readUnsignedByte());
        this.worldType = WorldType.parseWorldType(buf.readString(16));

        if (this.worldType == null) {
            this.worldType = WorldType.DEFAULT;
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeInt(this.dimensionID);
        buf.writeByte(this.difficulty.getDifficultyId());
        buf.writeByte(this.gameType.getID());
        buf.writeString(this.worldType.getName());
    }

    @SideOnly(Side.CLIENT)
    public int getDimensionID() {
        return this.dimensionID;
    }

    @SideOnly(Side.CLIENT)
    public EnumDifficulty getDifficulty() {
        return this.difficulty;
    }

    @SideOnly(Side.CLIENT)
    public GameType getGameType() {
        return this.gameType;
    }

    @SideOnly(Side.CLIENT)
    public WorldType getWorldType() {
        return this.worldType;
    }
}
