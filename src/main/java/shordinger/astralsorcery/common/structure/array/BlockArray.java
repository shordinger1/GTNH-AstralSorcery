/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.structure.array;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import shordinger.astralsorcery.common.block.BlockStructural;
import shordinger.astralsorcery.common.item.base.render.ISpecialStackDescriptor;
import shordinger.astralsorcery.common.util.BlockStateCheck;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockArray
 * Created by HellFirePvP
 * Date: 30.07.2016 / 16:23
 */
public class BlockArray {

    protected static final Random STATIC_RAND = new Random();

    protected Map<BlockPos, TileEntityCallback> tileCallbacks = new HashMap<>();
    protected Map<BlockPos, BlockInformation> pattern = new HashMap<>();
    private BlockPos min = new BlockPos(0, 0, 0), max = new BlockPos(0, 0, 0), size = new BlockPos(0, 0, 0);

    public void addAir(int x, int y, int z) {
        addBlock(x, y, z, Blocks.air.getDefaultState(), state -> state.getMaterial() == Material.air);
    }

    public void addBlock(int x, int y, int z, @Nonnull IBlockState state) {
        addBlock(new BlockPos(x, y, z), state);
    }

    public void addBlock(BlockPos offset, @Nonnull IBlockState state) {
        Block b = state.getBlock();
        pattern.put(offset, new BlockInformation(b, state));
        updateSize(offset);
    }

    public void addBlock(int x, int y, int z, @Nonnull IBlockState state, BlockStateCheck match) {
        addBlock(new BlockPos(x, y, z), state, match);
    }

    public void addBlock(BlockPos offset, @Nonnull IBlockState state, BlockStateCheck match) {
        Block b = state.getBlock();
        pattern.put(offset, new BlockInformation(b, state, match));
        updateSize(offset);
    }

    public void addAll(BlockArray other) {
        addAll(other, null);
    }

    public void addAll(BlockArray other, @Nullable Function<BlockPos, BlockPos> positionTransform) {
        for (Map.Entry<BlockPos, BlockInformation> patternEntry : other.pattern.entrySet()) {
            BlockPos to = patternEntry.getKey();
            if (positionTransform != null) {
                to = positionTransform.apply(to);
            }
            pattern.put(to, patternEntry.getValue());
            updateSize(to);
        }
        for (Map.Entry<BlockPos, TileEntityCallback> patternEntry : other.tileCallbacks.entrySet()) {
            BlockPos to = patternEntry.getKey();
            if (positionTransform != null) {
                to = positionTransform.apply(to);
            }
            tileCallbacks.put(to, patternEntry.getValue());
        }
    }

    public void addTileCallback(BlockPos pos, TileEntityCallback callback) {
        tileCallbacks.put(pos, callback);
    }

    public boolean hasBlockAt(BlockPos pos) {
        return pattern.containsKey(pos);
    }

    public boolean isEmpty() {
        return pattern.isEmpty();
    }

    public BlockPos getMax() {
        return max;
    }

    public BlockPos getMin() {
        return min;
    }

    public BlockPos getSize() {
        return size;
    }

    private void updateSize(BlockPos addedPos) {
        if (addedPos.getX() < min.getX()) {
            min = new BlockPos(addedPos.getX(), min.getY(), min.getZ());
        }
        if (addedPos.getX() > max.getX()) {
            max = new BlockPos(addedPos.getX(), max.getY(), max.getZ());
        }
        if (addedPos.getY() < min.getY()) {
            min = new BlockPos(min.getX(), addedPos.getY(), min.getZ());
        }
        if (addedPos.getY() > max.getY()) {
            max = new BlockPos(max.getX(), addedPos.getY(), max.getZ());
        }
        if (addedPos.getZ() < min.getZ()) {
            min = new BlockPos(min.getX(), min.getY(), addedPos.getZ());
        }
        if (addedPos.getZ() > max.getZ()) {
            max = new BlockPos(max.getX(), max.getY(), addedPos.getZ());
        }
        size = new BlockPos(max.getX() - min.getX() + 1, max.getY() - min.getY() + 1, max.getZ() - min.getZ() + 1);
    }

    public Map<BlockPos, BlockInformation> getPattern() {
        return pattern;
    }

    public Map<BlockPos, BlockInformation> getPatternSlice(int slice) {
        Map<BlockPos, BlockInformation> copy = new HashMap<>();
        for (BlockPos pos : pattern.keySet()) {
            if (pos.getY() == slice) {
                copy.put(pos, pattern.get(pos));
            }
        }
        return copy;
    }

    public int getBlockSize() {
        return this.getPattern()
            .size();
    }

    public Map<BlockPos, TileEntityCallback> getTileCallbacks() {
        return tileCallbacks;
    }

    public void addAirCube(int ox, int oy, int oz, int tx, int ty, int tz) {
        addBlockCube(
            Blocks.AIR.getDefaultState(),
            state -> state.getMaterial() == Material.AIR,
            ox,
            oy,
            oz,
            tx,
            ty,
            tz);
    }

    public void addBlockCube(@Nonnull IBlockState state, int ox, int oy, int oz, int tx, int ty, int tz) {
        addBlockCube(
            state,
            new BlockStateCheck.Meta(
                state.getBlock(),
                state.getBlock()
                    .getMetaFromState(state)),
            ox,
            oy,
            oz,
            tx,
            ty,
            tz);
    }

    public void addBlockCube(@Nonnull IBlockState state, BlockStateCheck match, int ox, int oy, int oz, int tx, int ty,
                             int tz) {
        int lx, ly, lz;
        int hx, hy, hz;
        if (ox < tx) {
            lx = ox;
            hx = tx;
        } else {
            lx = tx;
            hx = ox;
        }
        if (oy < ty) {
            ly = oy;
            hy = ty;
        } else {
            ly = ty;
            hy = oy;
        }
        if (oz < tz) {
            lz = oz;
            hz = tz;
        } else {
            lz = tz;
            hz = oz;
        }

        for (int xx = lx; xx <= hx; xx++) {
            for (int zz = lz; zz <= hz; zz++) {
                for (int yy = ly; yy <= hy; yy++) {
                    addBlock(new BlockPos(xx, yy, zz), state, match);
                }
            }
        }
    }

    public Map<BlockPos, IBlockState> placeInWorld(World world, BlockPos center) {
        Map<BlockPos, IBlockState> result = new HashMap<>();
        for (Map.Entry<BlockPos, BlockInformation> entry : pattern.entrySet()) {
            BlockInformation info = entry.getValue();
            BlockPos at = center.add(entry.getKey());
            IBlockState state = info.state;
            if (!world.setBlockState(at, state, 3)) {
                continue;
            }
            result.put(at, state);

            if (MiscUtils.isFluidBlock(state)) {
                world.neighborChanged(at, state.getBlock(), at);
            }

            TileEntity placed = world.getTileEntity(at);
            if (tileCallbacks.containsKey(entry.getKey())) {
                TileEntityCallback callback = tileCallbacks.get(entry.getKey());
                if (callback.isApplicable(placed)) {
                    callback.onPlace(world, at, placed);
                }
            }
        }
        return result;
    }

    public List<ItemStack> getAsDescriptiveStacks() {
        List<ItemStack> out = new LinkedList<>();
        for (BlockInformation info : pattern.values()) {
            int meta = info.metadata;
            ItemStack s;
            if (info.type instanceof BlockFluidBase) {
                s = FluidUtil
                    .getFilledBucket(new FluidStack(((BlockFluidBase) info.type).getFluid(), Fluid.BUCKET_VOLUME));
            } else if (info.type instanceof BlockStructural) {
                continue;
                // IBlockState otherState = info.state.getValue(BlockStructural.BLOCK_TYPE).getSupportedState();
                // Item i = Item.getItemFromBlock(otherState.getBlock());
                // if(i == null) continue;
                // s = new ItemStack(i, 1, otherState.getBlock().getMetaFromState(otherState));
            } else if (info.type instanceof ISpecialStackDescriptor) {
                s = ((ISpecialStackDescriptor) info.type).getDecriptor(info.state);
            } else {
                Item i = Item.getItemFromBlock(info.type);
                if (i == Items.AIR) continue;
                s = new ItemStack(i, 1, meta);
            }
            if (s.stackSize!=0) {
                boolean found = false;
                for (ItemStack stack : out) {
                    if (stack.getItem()
                        .getUnlocalizedName()
                        .equals(
                            s.getItem()
                                .getUnlocalizedName())
                        && stack.getItemDamage() == s.getItemDamage()) {
                        stack.stackSize++;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    out.add(s);
                }
            }
        }
        return out;
    }

    public static interface TileEntityCallback {

        public boolean isApplicable(TileEntity te);

        public void onPlace(IBlockAccess access, BlockPos at, TileEntity te);

    }

    public static class BlockInformation {

        public final Block type;
        public final IBlockState state;
        public final int metadata;
        public final BlockStateCheck matcher;

        protected BlockInformation(Block type, IBlockState state) {
            this(type, state, new BlockStateCheck.Meta(type, type.getMetaFromState(state)));
        }

        protected BlockInformation(Block type, IBlockState state, BlockStateCheck matcher) {
            this.type = type;
            this.state = state;
            this.metadata = type.getMetaFromState(state);
            this.matcher = matcher;
        }

    }

}
