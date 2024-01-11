package shordinger.wrapper.net.minecraft.world.chunk;

import shordinger.wrapper.net.minecraft.block.state.IBlockState;

interface IBlockStatePaletteResizer {

    int onResize(int bits, IBlockState state);
}
