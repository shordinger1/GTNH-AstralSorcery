package shordinger.wrapper.net.minecraft.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.material.MapColor;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.IBlockAccess;

public class BlockGravel extends BlockFalling {

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        if (fortune > 3) {
            fortune = 3;
        }

        return rand.nextInt(10 - fortune * 3) == 0 ? Items.FLINT : super.getItemDropped(state, rand, fortune);
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    public net.minecraft.block.material.MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return MapColor.STONE;
    }

    @SideOnly(Side.CLIENT)
    public int getDustColor(IBlockState state) {
        return -8356741;
    }
}
