/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * <p>
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * <p>
 * File Created @ [01/11/2015, 19:05:57 (GMT)]
 */
package vazkii.botania.api.lexicon.multiblock.component;

import net.minecraft.world.World;

import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;

/**
 * A multiblock component that matches any non air blocks that have a collision box.
 */
public class AnyComponent extends MultiblockComponent {

    public AnyComponent(BlockPos relPos, IBlockState state) {
        super(relPos, state);
    }

    @Override
    public boolean matches(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return !state.getBlock()
            .isAir(state, world, pos) && state.getCollisionBoundingBox(world, pos) != null;
    }

}
