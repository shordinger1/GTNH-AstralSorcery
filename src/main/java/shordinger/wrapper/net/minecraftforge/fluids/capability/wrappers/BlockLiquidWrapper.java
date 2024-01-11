/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package shordinger.wrapper.net.minecraftforge.fluids.capability.wrappers;

import javax.annotation.Nullable;

import shordinger.wrapper.net.minecraft.block.BlockLiquid;
import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.item.ItemBucket;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.common.util.Constants;
import shordinger.wrapper.net.minecraftforge.fluids.Fluid;
import shordinger.wrapper.net.minecraftforge.fluids.FluidRegistry;
import shordinger.wrapper.net.minecraftforge.fluids.FluidStack;
import shordinger.wrapper.net.minecraftforge.fluids.capability.FluidTankProperties;
import shordinger.wrapper.net.minecraftforge.fluids.capability.IFluidHandler;
import shordinger.wrapper.net.minecraftforge.fluids.capability.IFluidTankProperties;

/**
 * Wrapper to handle vanilla Water or Lava as an IFluidHandler.
 * Methods are modeled after {@link ItemBucket#onItemRightClick(World, EntityPlayer, EnumHand)}
 */
public class BlockLiquidWrapper implements IFluidHandler {

    protected final BlockLiquid blockLiquid;
    protected final World world;
    protected final BlockPos blockPos;

    public BlockLiquidWrapper(BlockLiquid blockLiquid, World world, BlockPos blockPos) {
        this.blockLiquid = blockLiquid;
        this.world = world;
        this.blockPos = blockPos;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        FluidStack containedStack = null;
        IBlockState blockState = world.getBlockState(blockPos);
        if (blockState.getBlock() == blockLiquid) {
            containedStack = getStack(blockState);
        }
        return new FluidTankProperties[]{new FluidTankProperties(containedStack, Fluid.BUCKET_VOLUME, false, true)};
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        // NOTE: "Filling" means placement in this context!
        if (resource.amount < Fluid.BUCKET_VOLUME) {
            return 0;
        }

        if (doFill) {
            Material material = blockLiquid.getDefaultState()
                .getMaterial();
            BlockLiquid block = BlockLiquid.getFlowingBlock(material);
            world.setBlockState(
                blockPos,
                block.getDefaultState()
                    .withProperty(BlockLiquid.LEVEL, 0),
                11);
        }

        return Fluid.BUCKET_VOLUME;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (resource == null || resource.amount < Fluid.BUCKET_VOLUME) {
            return null;
        }

        IBlockState blockState = world.getBlockState(blockPos);
        if (blockState.getBlock() == blockLiquid && blockState.getValue(BlockLiquid.LEVEL) == 0) {
            FluidStack containedStack = getStack(blockState);
            if (containedStack != null && resource.containsFluid(containedStack)) {
                if (doDrain) {
                    world.setBlockState(
                        blockPos,
                        Blocks.AIR.getDefaultState(),
                        Constants.BlockFlags.DEFAULT_AND_RERENDER);
                }
                return containedStack;
            }

        }
        return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (maxDrain < Fluid.BUCKET_VOLUME) {
            return null;
        }

        IBlockState blockState = world.getBlockState(blockPos);
        if (blockState.getBlock() == blockLiquid) {
            FluidStack containedStack = getStack(blockState);
            if (containedStack != null && containedStack.amount <= maxDrain) {
                if (doDrain) {
                    world.setBlockState(
                        blockPos,
                        Blocks.AIR.getDefaultState(),
                        Constants.BlockFlags.DEFAULT_AND_RERENDER);
                }
                return containedStack;
            }

        }
        return null;
    }

    @Nullable
    private FluidStack getStack(IBlockState blockState) {
        Material material = blockState.getMaterial();
        if (material == Material.WATER && blockState.getValue(BlockLiquid.LEVEL) == 0) {
            return new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);
        } else if (material == Material.LAVA && blockState.getValue(BlockLiquid.LEVEL) == 0) {
            return new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME);
        } else {
            return null;
        }
    }
}
