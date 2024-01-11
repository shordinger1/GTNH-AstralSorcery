package shordinger.wrapper.net.minecraft.block;

import shordinger.wrapper.net.minecraft.block.material.MapColor;
import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

public class BlockHay extends BlockRotatedPillar {

    public BlockHay() {
        super(Material.GRASS, MapColor.YELLOW);
        this.setDefaultState(
            this.blockState.getBaseState()
                .withProperty(AXIS, EnumFacing.Axis.Y));
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    /**
     * Block's chance to react to a living entity falling on it.
     */
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        entityIn.fall(fallDistance, 0.2F);
    }
}
