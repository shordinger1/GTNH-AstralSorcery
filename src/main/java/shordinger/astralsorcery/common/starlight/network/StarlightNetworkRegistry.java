/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.starlight.network;

import shordinger.astralsorcery.common.block.network.IBlockStarlightRecipient;
import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.starlight.network.handlers.BlockTransmutationHandler;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StarlightNetworkRegistry
 * Created by HellFirePvP
 * Date: 04.08.2016 / 22:25
 */
public class StarlightNetworkRegistry {

    //private static Map<Block, Map<Integer, IStarlightBlockHandler>> validEndpoints = new HashMap<>();
    private static List<IStarlightBlockHandler> dynamicBlockHandlers = new LinkedList<>();

    @Nullable
    public static IStarlightBlockHandler getStarlightHandler(World world, BlockPos pos, IBlockState state, IWeakConstellation cst) {
        Block b = state.getBlock();
        if(b instanceof IBlockStarlightRecipient) return null;
        for (IStarlightBlockHandler handler : dynamicBlockHandlers) {
            if(handler.isApplicable(world, pos, state, cst)) return handler;
        }
        return null;
    }

    public static void registerEndpoint(IStarlightBlockHandler handler) {
        dynamicBlockHandlers.add(handler);
    }

    public static void setupRegistry() {
        registerEndpoint(new BlockTransmutationHandler());
    }

    //1 instance is/should be created for 1 type of block+meta pair
    //This is NOT suggested as "first choice" - please implement IBlockStarlightRecipient instead if possible.
    public static interface IStarlightBlockHandler {

        /**
         * See Constellation-dependent method.
         */
        @Deprecated
        public boolean isApplicable(World world, BlockPos pos, IBlockState state);

        default public boolean isApplicable(World world, BlockPos pos, IBlockState state, @Nullable IWeakConstellation starlightType) {
            return isApplicable(world, pos, state);
        }

        public void receiveStarlight(World world, Random rand, BlockPos pos, @Nullable IWeakConstellation starlightType, double amount);

    }

}
