/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraft.world.World;

import com.google.common.collect.Lists;

import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStateCheck
 * Created by HellFirePvP
 * Date: 17.10.2016 / 00:30
 */
public interface BlockStateCheck {

    public boolean isStateValid(IBlockState state);

    public static interface WorldSpecific {

        public boolean isStateValid(World world, BlockPos pos, IBlockState state);

        public static WorldSpecific wrap(BlockStateCheck check) {
            return (world, pos, state) -> check.isStateValid(state);
        }

    }

    public static class Block implements BlockStateCheck {

        private final List<net.minecraft.block.Block> toCheck;

        public Block(net.minecraft.block.Block... toCheck) {
            this.toCheck = Lists.newArrayList(toCheck);
        }

        @Override
        public boolean isStateValid(IBlockState state) {
            return toCheck.contains(state.getBlock());
        }
    }

    public static class Meta implements BlockStateCheck {

        private final int toCheck;
        private final net.minecraft.block.Block block;

        public Meta(net.minecraft.block.Block block, int toCheck) {
            this.toCheck = toCheck;
            this.block = block;
        }

        public AnyMeta copyWithAdditionalMeta(int add) {
            AnyMeta ret = new AnyMeta(this.block, Lists.newArrayList(toCheck));
            if (!ret.passableMetadataValues.contains(add)) {
                ret.passableMetadataValues.add(add);
            }
            return ret;
        }

        @Override
        public boolean isStateValid(IBlockState state) {
            return state.getBlock()
                .equals(block)
                && state.getBlock()
                .getMetaFromState(state) == toCheck;
        }
    }

    public static class AnyMeta implements BlockStateCheck {

        private final Collection<Integer> passableMetadataValues;
        private final net.minecraft.block.Block block;

        public AnyMeta(net.minecraft.block.Block block, int meta) {
            this(block, new int[]{meta});
        }

        public AnyMeta(net.minecraft.block.Block block, int... values) {
            this.passableMetadataValues = new ArrayList<>(values.length);
            for (int val : values) {
                this.passableMetadataValues.add(val);
            }
            this.block = block;
        }

        public AnyMeta(net.minecraft.block.Block block, Integer... values) {
            this.passableMetadataValues = Arrays.asList(values);
            this.block = block;
        }

        public AnyMeta(net.minecraft.block.Block block, Collection<Integer> passableMetadataValues) {
            this.passableMetadataValues = passableMetadataValues;
            this.block = block;
        }

        public AnyMeta copyWithAdditionalMeta(int add) {
            AnyMeta ret = new AnyMeta(this.block, this.passableMetadataValues);
            if (!ret.passableMetadataValues.contains(add)) {
                ret.passableMetadataValues.add(add);
            }
            return ret;
        }

        @Override
        public boolean isStateValid(IBlockState state) {
            return state.getBlock()
                .equals(block)
                && passableMetadataValues.contains(
                state.getBlock()
                    .getMetaFromState(state));
        }
    }

}
