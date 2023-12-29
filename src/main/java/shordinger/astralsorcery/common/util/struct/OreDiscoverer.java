/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util.struct;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockOre;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import shordinger.astralsorcery.common.structure.array.BlockArray;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;
import shordinger.astralsorcery.migration.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OreDiscoverer
 * Created by HellFirePvP
 * Date: 12.03.2017 / 23:30
 */
public class OreDiscoverer {

    public static BlockArray startSearch(World world, Vector3 position, int xzLimit) {
        xzLimit = MathHelper.clamp(xzLimit, 0, 32);
        BlockPos originPos = position.toBlockPos();
        BlockArray out = new BlockArray();
        List<IBlockState> successfulOres = new ArrayList<>(12);
        BlockPos.MutableBlockPos.PooledMutableBlockPos pooledPos = BlockPos.PooledMutableBlockPos.retain();
        try {
            for (int xx = -xzLimit; xx <= xzLimit; xx++) {
                for (int zz = -xzLimit; zz <= xzLimit; zz++) {
                    pooledPos.setPos(originPos.getX() + xx, 0, originPos.getZ() + zz);
                    Chunk c = world.getChunkFromBlockCoords(pooledPos);
                    int highest = (c.getTopFilledSegment() + 1) * 16;
                    for (int y = 0; y < highest; y++) {
                        pooledPos.setY(y);
                        IBlockState at = c.getBlockState(pooledPos);
                        if (successfulOres.contains(at)) {
                            out.addBlock(new BlockPos(pooledPos), at);
                        } else if (isOre(at)) {
                            out.addBlock(new BlockPos(pooledPos), at);
                            successfulOres.add(at);
                        }
                    }
                }
            }
        } finally {
            pooledPos.release();
        }
        return out;
    }

    private static boolean isOre(IBlockState state) {
        if (state.getBlock() instanceof BlockOre) { // WELL that's easy enough.
            return true;
        }
        ItemStack blockStack = ItemUtils.createBlockStack(state);
        return !blockStack.isEmpty() && ItemUtils.hasOreNamePart(blockStack, "ore");
    }

}
