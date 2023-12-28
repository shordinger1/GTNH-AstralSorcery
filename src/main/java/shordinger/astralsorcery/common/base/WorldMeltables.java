/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.base;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.World;

import shordinger.astralsorcery.common.constellation.effect.GenListEntries;
import shordinger.astralsorcery.common.util.BlockStateCheck;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.ChunkPos;
import shordinger.astralsorcery.migration.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldMeltables
 * Created by HellFirePvP
 * Date: 31.10.2016 / 22:49
 */
public enum WorldMeltables implements MeltInteraction {

    COBBLE(new BlockStateCheck.Block(Blocks.COBBLESTONE), Blocks.FLOWING_LAVA.getDefaultState(), 180),
    STONE(new BlockStateCheck.Block(Blocks.STONE), Blocks.FLOWING_LAVA.getDefaultState(), 100),
    OBSIDIAN(new BlockStateCheck.Block(Blocks.OBSIDIAN), Blocks.FLOWING_LAVA.getDefaultState(), 75),
    NETHERRACK(new BlockStateCheck.Block(Blocks.NETHERRACK), Blocks.FLOWING_LAVA.getDefaultState(), 40),
    NETHERBRICK(new BlockStateCheck.Block(Blocks.NETHER_BRICK), Blocks.FLOWING_LAVA.getDefaultState(), 60),
    MAGMA(new BlockStateCheck.Block(Blocks.MAGMA), Blocks.FLOWING_LAVA.getDefaultState(), 1),
    ICE(new BlockStateCheck.Block(Blocks.ICE), Blocks.FLOWING_WATER.getDefaultState(), 1),
    FROSTED_ICE(new BlockStateCheck.Block(Blocks.FROSTED_ICE), Blocks.FLOWING_WATER.getDefaultState(), 1),
    PACKED_ICE(new BlockStateCheck.Block(Blocks.PACKED_ICE), Blocks.FLOWING_WATER.getDefaultState(), 2);

    private final BlockStateCheck meltableCheck;
    private final IBlockState meltResult;
    private final int meltDuration;

    private WorldMeltables(BlockStateCheck meltableCheck, IBlockState meltResult, int meltDuration) {
        this.meltableCheck = meltableCheck;
        this.meltResult = meltResult;
        this.meltDuration = meltDuration;
    }

    @Override
    public boolean isMeltable(World world, BlockPos pos, IBlockState worldState) {
        return meltableCheck.isStateValid(worldState);
    }

    @Override
    @Nullable
    public IBlockState getMeltResultState() {
        return meltResult;
    }

    @Override
    @Nonnull
    public ItemStack getMeltResultStack() {
        return null;
    }

    @Override
    public int getMeltTickDuration() {
        return meltDuration;
    }

    @Nullable
    public static MeltInteraction getMeltable(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        for (WorldMeltables melt : values()) {
            if (melt.isMeltable(world, pos, state)) return melt;
        }
        ItemStack stack = ItemUtils.createBlockStack(state);
        if (!stack.isEmpty()) {
            ItemStack out = FurnaceRecipes.instance()
                .getSmeltingResult(stack);
            if (!out.isEmpty()) {
                return new FurnaceRecipeInteraction(state, out);
            }
        }
        return null;
    }

    public static class ActiveMeltableEntry extends GenListEntries.CounterListEntry {

        public ActiveMeltableEntry(BlockPos pos) {
            super(pos);
        }

        public boolean isValid(World world, boolean forceLoad) {
            if (!forceLoad && !MiscUtils.isChunkLoaded(world, new ChunkPos(pos()))) return true;
            return getMeltable(world) != null;
        }

        public MeltInteraction getMeltable(World world) {
            return WorldMeltables.getMeltable(world, pos());
        }

    }

    public static class FurnaceRecipeInteraction implements MeltInteraction {

        private final ItemStack out;
        private final BlockStateCheck.Meta matchInState;

        public FurnaceRecipeInteraction(IBlockState inState, ItemStack outStack) {
            this.matchInState = new BlockStateCheck.Meta(
                inState.getBlock(),
                inState.getBlock()
                    .getMetaFromState(inState));
            this.out = outStack;
        }

        @Override
        public boolean isMeltable(World world, BlockPos pos, IBlockState state) {
            return matchInState.isStateValid(state);
        }

        @Nullable
        @Override
        public IBlockState getMeltResultState() {
            return ItemUtils.createBlockState(out);
        }

        @Nonnull
        @Override
        public ItemStack getMeltResultStack() {
            return out.copy();
        }

    }

}
