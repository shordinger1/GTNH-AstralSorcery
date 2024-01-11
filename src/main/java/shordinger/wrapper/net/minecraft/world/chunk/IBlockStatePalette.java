package shordinger.wrapper.net.minecraft.world.chunk;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;

public interface IBlockStatePalette {

    int idFor(IBlockState state);

    /**
     * Gets the block state by the palette id.
     */
    @Nullable
    IBlockState getBlockState(int indexKey);

    @SideOnly(Side.CLIENT)
    void read(PacketBuffer buf);

    void write(PacketBuffer buf);

    int getSerializedSize();
}
