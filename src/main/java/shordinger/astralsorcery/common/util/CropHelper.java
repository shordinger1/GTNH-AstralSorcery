/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.*;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IPlantable;

import com.google.common.collect.Lists;

import shordinger.astralsorcery.common.constellation.effect.CEffectPositionListGen;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.ChunkPos;
import shordinger.astralsorcery.migration.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CropHelper
 * Created by HellFirePvP
 * Date: 08.11.2016 / 13:05
 */
// Intended for mostly Server-Side use
public class CropHelper {

    @Nullable
    public static GrowablePlant wrapPlant(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Block b = state.getBlock();
        if (state.getBlock() instanceof IGrowable) {
            if (b instanceof BlockGrass) return null;
            if (b instanceof BlockTallGrass) return null;
            if (b instanceof BlockDoublePlant) return null;
            return new GrowableWrapper(pos);
        }
        if (state.getBlock()
            .equals(Blocks.reeds)) {
            if (isReedBase(world, pos)) {
                return new GrowableReedWrapper(pos);
            }
        }
        if (state.getBlock()
            .equals(Blocks.cactus)) {
            if (isCactusBase(world, pos)) {
                return new GrowableCactusWrapper(pos);
            }
        }
        if (state.getBlock()
            .equals(Blocks.nether_wart)) {
            return new GrowableNetherwartWrapper(pos);
        }
        return null;
    }

    @Nullable
    public static HarvestablePlant wrapHarvestablePlant(World world, BlockPos pos) {
        GrowablePlant growable = wrapPlant(world, pos);
        if (growable == null) return null; // Every plant has to be growable.
        IBlockState state = world.getBlockState(growable.pos());
        if (state.getBlock()
            .equals(Blocks.reeds) && growable instanceof GrowableReedWrapper) {
            return (GrowableReedWrapper) growable;
        }
        if (state.getBlock()
            .equals(Blocks.cactus) && growable instanceof GrowableCactusWrapper) {
            return (GrowableCactusWrapper) growable;
        }
        if (state.getBlock()
            .equals(Blocks.nether_wart) && growable instanceof GrowableNetherwartWrapper) {
            return (GrowableNetherwartWrapper) growable;
        }
        if (state.getBlock() instanceof IPlantable) {
            return new HarvestableWrapper(pos);
        }
        return null;
    }

    private static boolean isReedBase(World world, BlockPos pos) {
        return !world.getBlockState(pos.down())
            .getBlock()
            .equals(Blocks.reeds);
    }

    private static boolean isCactusBase(World world, BlockPos pos) {
        return !world.getBlockState(pos.down())
            .getBlock()
            .equals(Blocks.cactus);
    }

    public static interface GrowablePlant extends CEffectPositionListGen.CEffectGenListEntry {

        public boolean isValid(World world, boolean forceChunkLoad);

        public boolean canGrow(World world);

        public boolean tryGrow(World world, Random rand);

    }

    public static interface HarvestablePlant extends GrowablePlant {

        public boolean canHarvest(World world);

        public List<ItemStack> harvestDropsAndReplant(World world, Random rand, int harvestFortune);

    }

    public static class HarvestableWrapper implements HarvestablePlant {

        private final BlockPos pos;

        public HarvestableWrapper(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public boolean canHarvest(World world) {
            IBlockState at = world.getBlockState(pos);
            if (!(at.getBlock() instanceof IGrowable)) return false;
            return !((IGrowable) at.getBlock()).canGrow(world, pos, at, false);
        }

        @Override
        public List<ItemStack> harvestDropsAndReplant(World world, Random rand, int harvestFortune) {
            List<ItemStack> drops = Lists.newLinkedList();
            if (canHarvest(world)) {
                BlockPos pos = pos();
                IBlockState at = world.getBlockState(pos());
                if (at.getBlock() instanceof IPlantable) {
                    drops.addAll(
                        at.getBlock()
                            .getDrops(world, pos, at, harvestFortune));
                    world.setBlockToAir(pos);
                    world.setBlockState(pos, ((IPlantable) at.getBlock()).getPlant(world, pos));
                }
            }
            return drops;
        }

        @Override
        public BlockPos pos() {
            return pos;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
        }

        @Override
        public void writeToNBT(NBTTagCompound nbt) {
        }

        @Override
        public boolean isValid(World world, boolean forceChunkLoad) {
            if (!forceChunkLoad && !MiscUtils.isChunkLoaded(world, new ChunkPos(pos()))) return true; // We stall
            // until it's
            // loaded.
            HarvestablePlant plant = wrapHarvestablePlant(world, pos());
            return plant instanceof HarvestableWrapper;
        }

        @Override
        public boolean canGrow(World world) {
            IBlockState at = world.getBlockState(pos);
            return at.getBlock() instanceof IGrowable && ((IGrowable) at.getBlock()).canGrow(world, pos, at, false);
        }

        @Override
        public boolean tryGrow(World world, Random rand) {
            IBlockState at = world.getBlockState(pos);
            if (at.getBlock() instanceof IGrowable) {
                if (((IGrowable) at.getBlock()).canGrow(world, pos, at, false)) {
                    ((IGrowable) at.getBlock()).grow(world, rand, pos, at);
                    return true;
                }
            }
            return false;
        }

    }

    public record GrowableNetherwartWrapper(BlockPos pos) implements HarvestablePlant {

        @Override
        public boolean isValid(World world, boolean forceChunkLoad) {
            if (!forceChunkLoad && !MiscUtils.isChunkLoaded(world, new ChunkPos(pos))) return true; // We stall until
            // it's loaded.
            return world.getBlockState(pos)
                .getBlock()
                .equals(Blocks.NETHER_WART);
        }

        @Override
        public boolean canGrow(World world) {
            IBlockState at = world.getBlockState(pos);
            return at.getBlock()
                .equals(Blocks.NETHER_WART) && at.getValue(BlockNetherWart.AGE) < 3;
        }

        @Override
        public boolean tryGrow(World world, Random rand) {
            if (rand.nextBoolean()) {
                IBlockState current = world.getBlockState(pos);
                return world.setBlockState(
                    pos,
                    current.withProperty(BlockNetherWart.AGE, (Math.min(3, current.getValue(BlockNetherWart.AGE) + 1))),
                    3);
            }
            return false;
        }

        @Override
        public boolean canHarvest(World world) {
            IBlockState current = world.getBlockState(pos);
            return current.getBlock()
                .equals(Blocks.NETHER_WART) && current.getValue(BlockNetherWart.AGE) >= 3;
        }

        @Override
        public List<ItemStack> harvestDropsAndReplant(World world, Random rand, int harvestFortune) {
            IBlockState current = world.getBlockState(pos);
            List<ItemStack> drops = current.getBlock()
                .getDrops(world, pos, current, harvestFortune);
            world.setBlockState(
                pos,
                Blocks.NETHER_WART.getDefaultState()
                    .withProperty(BlockNetherWart.AGE, 1),
                3);
            return drops;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
        }

        @Override
        public void writeToNBT(NBTTagCompound nbt) {
        }

    }

    public record GrowableCactusWrapper(BlockPos pos) implements HarvestablePlant {

        @Override
        public boolean canHarvest(World world) {
            return world.getBlockState(pos.up())
                .getBlock()
                .equals(Blocks.cactus);
        }

        @Override
        public boolean isValid(World world, boolean forceChunkLoad) {
            if (!forceChunkLoad && !MiscUtils.isChunkLoaded(world, new ChunkPos(pos))) return true; // We stall until
            // it's loaded.
            return world.getBlockState(pos)
                .getBlock()
                .equals(Blocks.cactus);
        }

        @Override
        public List<ItemStack> harvestDropsAndReplant(World world, Random rand, int harvestFortune) {
            List<ItemStack> drops = Lists.newLinkedList();
            for (int i = 2; i > 0; i--) {
                BlockPos bp = pos.up(i);
                IBlockState at = world.getBlockState(bp);
                if (at.getBlock()
                    .equals(Blocks.cactus)) {
                    MiscUtils.breakBlockWithoutPlayer((WorldServer) world, bp);
                }
            }
            return drops;
        }

        @Override
        public boolean canGrow(World world) {
            BlockPos cache = pos;
            for (int i = 1; i < 3; i++) {
                cache = cache.up();
                if (world.isAirBlock(cache.getX(), cache.getY(), cache.getZ())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean tryGrow(World world, Random rand) {
            BlockPos cache = pos;
            for (int i = 1; i < 3; i++) {
                cache = cache.up();
                if (world.isAirBlock(cache.getX(), cache.getY(), cache.getZ())) {
                    if (rand.nextBoolean()) {
                        return world.setBlockState(cache, Blocks.cactus.getDefaultState());
                    } else {
                        return false;
                    }
                }
            }
            return false;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
        }

        @Override
        public void writeToNBT(NBTTagCompound nbt) {
        }
    }

    public record GrowableReedWrapper(BlockPos pos) implements HarvestablePlant {

        @Override
        public boolean canHarvest(World world) {
            return world.getBlockState(pos.up())
                .getBlock()
                .equals(Blocks.reeds);
        }

        @Override
        public List<ItemStack> harvestDropsAndReplant(World world, Random rand, int harvestFortune) {
            List<ItemStack> drops = Lists.newLinkedList();
            for (int i = 2; i > 0; i--) {
                BlockPos bp = pos.up(i);
                IBlockState at = world.getBlockState(bp);
                if (at.getBlock()
                    .equals(Blocks.reeds)) {
                    drops.addAll(
                        at.getBlock()
                            .getDrops(world, bp, at, harvestFortune));
                    world.setBlockToAir(bp);
                }
            }
            return drops;
        }

        @Override
        public boolean isValid(World world, boolean forceChunkLoad) {
            if (!forceChunkLoad && !MiscUtils.isChunkLoaded(world, new ChunkPos(pos))) return true; // We stall until
            // it's loaded.
            return world.getBlockState(pos)
                .getBlock()
                .equals(Blocks.reeds);
        }

        @Override
        public boolean canGrow(World world) {
            BlockPos cache = pos;
            for (int i = 1; i < 3; i++) {
                cache = cache.up();
                if (world.isAirBlock(cache.getX(), cache.getY(), cache.getZ())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean tryGrow(World world, Random rand) {
            BlockPos cache = pos;
            for (int i = 1; i < 3; i++) {
                cache = cache.up();
                if (world.isAirBlock(cache.getX(), cache.getY(), cache.getZ())) {
                    if (rand.nextBoolean()) {
                        return world.setBlockState(cache, Blocks.reeds.getDefaultState());
                    } else {
                        return false;
                    }
                }
            }
            return false;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
        }

        @Override
        public void writeToNBT(NBTTagCompound nbt) {
        }

    }

    public record GrowableWrapper(BlockPos pos) implements GrowablePlant {

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
        }

        @Override
        public void writeToNBT(NBTTagCompound nbt) {
        }

        @Override
        public boolean isValid(World world, boolean forceChunkLoad) {
            if (!forceChunkLoad && !MiscUtils.isChunkLoaded(world, new ChunkPos(pos))) return true; // We stall until
            // it's loaded.
            GrowablePlant res = wrapPlant(world, pos);
            return res instanceof GrowableWrapper;
        }

        @Override
        public boolean canGrow(World world) {
            IBlockState at = world.getBlockState(pos);
            return at.getBlock() instanceof IGrowable && (((IGrowable) at.getBlock()).canGrow(world, pos, at, false)
                || (at.getBlock() instanceof BlockStem && !stemHasCrop(world)));
        }

        private boolean stemHasCrop(World world) {
            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                Block offset = world.getBlockState(pos.offset(enumfacing))
                    .getBlock();
                if (offset.equals(Blocks.melon_block) || offset.equals(Blocks.pumpkin)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean tryGrow(World world, Random rand) {
            IBlockState at = world.getBlockState(pos);
            if (at.getBlock() instanceof IGrowable) {
                if (((IGrowable) at.getBlock()).canGrow(world, pos, at, false)) {
                    if (!((IGrowable) at.getBlock()).canUseBonemeal(world, rand, pos, at)) {
                        if (world.rand.nextInt(20) != 0) return true; // Returning true to say it could've been
                        // potentially grown - So this doesn't invalidate
                        // caches.
                    }
                    ((IGrowable) at.getBlock()).grow(world, rand, pos, at);
                    return true;
                }
                if (at.getBlock() instanceof BlockStem) {
                    for (int i = 0; i < 10; i++) {
                        at.getBlock()
                            .updateTick(world, pos, at, rand);
                    }
                    return true;
                }
            }
            return false;
        }
    }

}
