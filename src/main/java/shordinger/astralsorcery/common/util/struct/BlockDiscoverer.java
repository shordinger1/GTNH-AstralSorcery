/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util.struct;

import com.google.common.collect.Lists;
import shordinger.astralsorcery.common.structure.array.BlockArray;
import shordinger.astralsorcery.common.util.BlockStateCheck;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockDiscoverer
 * Created by HellFirePvP
 * Date: 07.02.2017 / 01:09
 */
public class BlockDiscoverer {

    public static BlockArray discoverBlocksWithSameStateAroundChain(World world, BlockPos origin, IBlockState match,
                                                                    int length, @Nullable EnumFacing originalBreakDirection, BlockStateCheck.WorldSpecific addCheck) {
        BlockArray out = new BlockArray();

        BlockPos offset = new BlockPos(origin);
        lbl:
        while (length > 0) {
            List<EnumFacing> faces = new ArrayList<>();
            Collections.addAll(faces, EnumFacing.VALUES);
            if (originalBreakDirection != null && out.isEmpty()) {
                faces.remove(originalBreakDirection);
                faces.remove(originalBreakDirection.getOpposite());
            }
            Collections.shuffle(faces);
            for (EnumFacing face : faces) {
                BlockPos at = offset.offset(face);
                if (out.getPattern()
                    .containsKey(at)) {
                    continue;
                }
                IBlockState test = world.getBlockState(at);
                if (MiscUtils.matchStateExact(match, test) && addCheck.isStateValid(world, at, test)) {
                    out.addBlock(at.getX(), at.getY(), at.getZ(), test);
                    length--;
                    offset = at;
                    continue lbl;
                }
            }
            break;
        }

        return out;
    }

    public static BlockArray searchForBlocksAround(World world, BlockPos origin, int cubeSize, BlockStateCheck match) {
        return searchForBlocksAround(world, origin, cubeSize, BlockStateCheck.WorldSpecific.wrap(match));
    }

    public static BlockArray searchForBlocksAround(World world, BlockPos origin, int cubeSize,
                                                   BlockStateCheck.WorldSpecific match) {
        BlockArray out = new BlockArray();

        BlockPos.PooledMutableBlockPos offset = BlockPos.PooledMutableBlockPos.retain();
        for (int xx = -cubeSize; xx <= cubeSize; xx++) {
            for (int zz = -cubeSize; zz <= cubeSize; zz++) {
                for (int yy = -cubeSize; yy <= cubeSize; yy++) {
                    offset.setPos(origin.getX() + xx, origin.getY() + yy, origin.getZ() + zz);
                    if (world.isBlockLoaded(offset)) {
                        IBlockState atState = world.getBlockState(offset);
                        if (match.isStateValid(world, offset, atState)) {
                            out.addBlock(new BlockPos(offset), atState);
                        }
                    }
                }
            }
        }
        offset.release();
        return out;
    }

    public static BlockArray discoverBlocksWithSameStateAroundLimited(Map<IBlockState, Integer> stateLimits,
                                                                      World world, BlockPos origin, boolean onlyExposed, int cubeSize, int limit, boolean searchCorners) {
        IBlockState testState = world.getBlockState(origin);

        BlockArray foundResult = new BlockArray();
        foundResult.addBlock(origin, testState);
        List<BlockPos> visited = new LinkedList<>();

        Deque<BlockPos> searchNext = new LinkedList<>();
        searchNext.addFirst(origin);

        while (!searchNext.isEmpty()) {
            Deque<BlockPos> currentSearch = searchNext;
            searchNext = new LinkedList<>();

            for (BlockPos offsetPos : currentSearch) {
                if (searchCorners) {
                    for (int xx = -1; xx <= 1; xx++) {
                        for (int yy = -1; yy <= 1; yy++) {
                            for (int zz = -1; zz <= 1; zz++) {
                                BlockPos search = offsetPos.add(xx, yy, zz);
                                if (visited.contains(search)) continue;
                                if (getCubeDistance(search, origin) > cubeSize) continue;
                                if (limit != -1 && foundResult.getBlockSize() + 1 > limit) continue;

                                visited.add(search);

                                if (!onlyExposed || isExposedToAir(world, search)) {
                                    IBlockState current = world.getBlockState(search);
                                    if (MiscUtils.matchStateExact(current, testState)) {
                                        foundResult.addBlock(search, current);
                                        searchNext.add(search);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    for (EnumFacing face : EnumFacing.VALUES) {
                        BlockPos search = offsetPos.offset(face);
                        if (visited.contains(search)) continue;
                        if (getCubeDistance(search, origin) > cubeSize) continue;
                        if (limit != -1 && foundResult.getBlockSize() + 1 > limit) continue;

                        visited.add(search);

                        if (!onlyExposed || isExposedToAir(world, search)) {
                            IBlockState current = world.getBlockState(search);
                            if (MiscUtils.matchStateExact(current, testState)) {
                                foundResult.addBlock(search, current);
                                searchNext.add(search);
                            }
                        }
                    }
                }
            }
        }

        return foundResult;
    }

    public static BlockArray discoverBlocksWithSameStateAround(List<IBlockState> states, World world, BlockPos origin,
                                                               boolean onlyExposed, int cubeSize, int limit, boolean searchCorners) {
        BlockArray foundResult = new BlockArray();
        foundResult.addBlock(origin, world.getBlockState(origin));
        List<BlockPos> visited = new LinkedList<>();

        Deque<BlockPos> searchNext = new LinkedList<>();
        searchNext.addFirst(origin);

        while (!searchNext.isEmpty()) {
            Deque<BlockPos> currentSearch = searchNext;
            searchNext = new LinkedList<>();

            for (BlockPos offsetPos : currentSearch) {
                if (searchCorners) {
                    for (int xx = -1; xx <= 1; xx++) {
                        for (int yy = -1; yy <= 1; yy++) {
                            for (int zz = -1; zz <= 1; zz++) {
                                BlockPos search = offsetPos.add(xx, yy, zz);
                                if (visited.contains(search)) continue;
                                if (getCubeDistance(search, origin) > cubeSize) continue;
                                if (limit != -1 && foundResult.getBlockSize() + 1 > limit) continue;

                                visited.add(search);

                                if (!onlyExposed || isExposedToAir(world, search)) {
                                    IBlockState current = world.getBlockState(search);
                                    if (MiscUtils.getMatchingState(states, current) != null) {
                                        foundResult.addBlock(search, current);
                                        searchNext.add(search);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    for (EnumFacing face : EnumFacing.VALUES) {
                        BlockPos search = offsetPos.offset(face);
                        if (visited.contains(search)) continue;
                        if (getCubeDistance(search, origin) > cubeSize) continue;
                        if (limit != -1 && foundResult.getBlockSize() + 1 > limit) continue;

                        visited.add(search);

                        if (!onlyExposed || isExposedToAir(world, search)) {
                            IBlockState current = world.getBlockState(search);
                            if (MiscUtils.getMatchingState(states, current) != null) {
                                foundResult.addBlock(search, current);
                                searchNext.add(search);
                            }
                        }
                    }
                }
            }
        }

        return foundResult;
    }

    public static BlockArray discoverBlocksWithSameStateAround(World world, BlockPos origin, boolean onlyExposed,
                                                               int cubeSize, int limit, boolean searchCorners) {
        IBlockState toMatch = world.getBlockState(origin);
        return discoverBlocksWithSameStateAround(
            Lists.newArrayList(toMatch),
            world,
            origin,
            onlyExposed,
            cubeSize,
            limit,
            searchCorners);
    }

    public static int getCubeDistance(BlockPos p1, BlockPos p2) {
        return (int) MathHelper
            .absMax(MathHelper.absMax(p1.getX() - p2.getX(), p1.getY() - p2.getY()), p1.getZ() - p2.getZ());
    }

    public static boolean isExposedToAir(World world, BlockPos pos) {
        for (EnumFacing face : EnumFacing.VALUES) {
            BlockPos offset = pos.offset(face);
            if (world.isAirBlock(offset) || world.getBlockState(offset)
                .getBlock()
                .isReplaceable(world, offset)) return true;
        }
        return false;
    }

}
