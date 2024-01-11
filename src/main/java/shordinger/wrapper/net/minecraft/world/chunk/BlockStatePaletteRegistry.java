package shordinger.wrapper.net.minecraft.world.chunk;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;

public class BlockStatePaletteRegistry implements IBlockStatePalette {

    public int idFor(IBlockState state) {
        int i = Block.BLOCK_STATE_IDS.get(state);
        return i == -1 ? 0 : i;
    }

    /**
     * Gets the block state by the palette id.
     */
    public IBlockState getBlockState(int indexKey) {
        IBlockState iblockstate = Block.BLOCK_STATE_IDS.getByValue(indexKey);
        return iblockstate == null ? Blocks.AIR.getDefaultState() : iblockstate;
    }

    @SideOnly(Side.CLIENT)
    public void read(PacketBuffer buf) {
        buf.readVarInt();
    }

    public void write(PacketBuffer buf) {
        buf.writeVarInt(0);
    }

    public int getSerializedSize() {
        return PacketBuffer.getVarIntSize(0);
    }
}
