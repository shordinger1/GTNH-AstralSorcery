package shordinger.wrapper.net.minecraft.block;

import java.util.Random;

import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;

public class BlockPackedIce extends Block {

    public BlockPackedIce() {
        super(Material.PACKED_ICE);
        this.slipperiness = 0.98F;
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random) {
        return 0;
    }
}
