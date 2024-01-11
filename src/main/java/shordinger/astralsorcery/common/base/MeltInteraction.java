/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.base;

import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MeltInteraction
 * Created by HellFirePvP
 * Date: 20.04.2017 / 20:40
 */
public interface MeltInteraction {

    public boolean isMeltable(World world, BlockPos pos, IBlockState state);

    default public int getMeltTickDuration() {
        return 100; //Def. Furance duration halved
    }

    //Result can be a blockstate and/or an itemstack.
    //The BlockState result, if present, takes precedence.
    @Nullable
    public IBlockState getMeltResultState();

    @Nonnull
    public ItemStack getMeltResultStack();

    default public void placeResultAt(World world, BlockPos pos) {
        IBlockState result = getMeltResultState();
        if(result != null) {
            world.setBlockState(pos, result, 3);
        } else {
            if (world.setBlockToAir(pos)) {
                ItemStack resultStack = getMeltResultStack();
                if(!resultStack.isEmpty()) {
                    ItemUtils.dropItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, resultStack);
                }
            }
        }
    }

}
