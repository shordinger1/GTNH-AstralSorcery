package vazkii.botania.api.lexicon.multiblock.component;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import shordinger.astralsorcery.migration.BlockPos;

// Multiblock component that only compares blocks, not blockstates
public class StateInsensitiveComponent extends MultiblockComponent {

    public StateInsensitiveComponent(BlockPos relPos, Block block) {
        super(relPos, block.getDefaultState(), false, null);
    }

    @Override
    public boolean matches(World world, BlockPos pos) {
        return WorldHelper.getBlockState(world, pos)
            .getBlock() == getBlockState().getBlock();
    }

    @Override
    public MultiblockComponent copy() {
        return new StateInsensitiveComponent(getRelativePosition(), getBlockState().getBlock());
    }

}
