package shordinger.wrapper.net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.material.MapColor;
import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.IBlockAccess;

public class BlockBreakable extends Block {

    private final boolean ignoreSimilarity;

    protected BlockBreakable(Material materialIn, boolean ignoreSimilarityIn) {
        this(materialIn, ignoreSimilarityIn, materialIn.getMaterialMapColor());
    }

    protected BlockBreakable(Material materialIn, boolean ignoreSimilarityIn, MapColor mapColorIn) {
        super(materialIn, mapColorIn);
        this.ignoreSimilarity = ignoreSimilarityIn;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos,
                                        EnumFacing side) {
        IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
        Block block = iblockstate.getBlock();

        if (this == Blocks.GLASS || this == Blocks.STAINED_GLASS) {
            if (blockState != iblockstate) {
                return true;
            }

            if (block == this) {
                return false;
            }
        }

        return !this.ignoreSimilarity && block == this ? false
            : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }
}
