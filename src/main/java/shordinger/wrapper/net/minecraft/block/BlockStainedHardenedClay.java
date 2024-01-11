package shordinger.wrapper.net.minecraft.block;

import shordinger.wrapper.net.minecraft.block.material.MapColor;
import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.item.EnumDyeColor;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.IBlockAccess;

public class BlockStainedHardenedClay extends BlockColored {

    private static final net.minecraft.block.material.MapColor[] MAP_COLORS = new net.minecraft.block.material.MapColor[]{
        MapColor.WHITE_STAINED_HARDENED_CLAY, MapColor.ORANGE_STAINED_HARDENED_CLAY,
        MapColor.MAGENTA_STAINED_HARDENED_CLAY, MapColor.LIGHT_BLUE_STAINED_HARDENED_CLAY,
        MapColor.YELLOW_STAINED_HARDENED_CLAY, MapColor.LIME_STAINED_HARDENED_CLAY, MapColor.PINK_STAINED_HARDENED_CLAY,
        MapColor.GRAY_STAINED_HARDENED_CLAY, MapColor.SILVER_STAINED_HARDENED_CLAY, MapColor.CYAN_STAINED_HARDENED_CLAY,
        MapColor.PURPLE_STAINED_HARDENED_CLAY, MapColor.BLUE_STAINED_HARDENED_CLAY,
        MapColor.BROWN_STAINED_HARDENED_CLAY, MapColor.GREEN_STAINED_HARDENED_CLAY, MapColor.RED_STAINED_HARDENED_CLAY,
        MapColor.BLACK_STAINED_HARDENED_CLAY};

    public BlockStainedHardenedClay() {
        super(Material.ROCK);
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    public net.minecraft.block.material.MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return MAP_COLORS[((EnumDyeColor) state.getValue(COLOR)).getMetadata()];
    }
}
