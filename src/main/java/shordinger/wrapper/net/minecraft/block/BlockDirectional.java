package shordinger.wrapper.net.minecraft.block;

import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.properties.PropertyDirection;

public abstract class BlockDirectional extends Block {

    public static final PropertyDirection FACING = PropertyDirection.create("facing");

    protected BlockDirectional(Material materialIn) {
        super(materialIn);
    }
}
