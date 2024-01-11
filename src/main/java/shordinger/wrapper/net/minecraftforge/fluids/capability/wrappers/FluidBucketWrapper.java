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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemBucketMilk;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraftforge.common.ForgeModContainer;
import shordinger.wrapper.net.minecraftforge.common.capabilities.Capability;
import shordinger.wrapper.net.minecraftforge.common.capabilities.ICapabilityProvider;
import shordinger.wrapper.net.minecraftforge.fluids.Fluid;
import shordinger.wrapper.net.minecraftforge.fluids.FluidRegistry;
import shordinger.wrapper.net.minecraftforge.fluids.FluidStack;
import shordinger.wrapper.net.minecraftforge.fluids.FluidUtil;
import shordinger.wrapper.net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import shordinger.wrapper.net.minecraftforge.fluids.capability.FluidTankProperties;
import shordinger.wrapper.net.minecraftforge.fluids.capability.IFluidHandlerItem;
import shordinger.wrapper.net.minecraftforge.fluids.capability.IFluidTankProperties;

/**
 * Wrapper for vanilla and forge buckets.
 * Swaps between empty bucket and filled bucket of the correct type.
 */
public class FluidBucketWrapper implements IFluidHandlerItem, ICapabilityProvider {

    @Nonnull
    protected ItemStack container;

    public FluidBucketWrapper(@Nonnull ItemStack container) {
        this.container = container;
    }

    @Nonnull
    @Override
    public ItemStack getContainer() {
        return container;
    }

    public boolean canFillFluidType(FluidStack fluidStack) {
        Fluid fluid = fluidStack.getFluid();
        if (fluid == FluidRegistry.WATER || fluid == FluidRegistry.LAVA
            || fluid.getName()
            .equals("milk")) {
            return true;
        }
        return FluidRegistry.isUniversalBucketEnabled() && FluidRegistry.hasBucket(fluid);
    }

    @Nullable
    public FluidStack getFluid() {
        Item item = container.getItem();
        if (item == Items.WATER_BUCKET) {
            return new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);
        } else if (item == Items.LAVA_BUCKET) {
            return new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME);
        } else if (item == Items.MILK_BUCKET) {
            return FluidRegistry.getFluidStack("milk", Fluid.BUCKET_VOLUME);
        } else if (item == ForgeModContainer.getInstance().universalBucket) {
            return ForgeModContainer.getInstance().universalBucket.getFluid(container);
        } else {
            return null;
        }
    }

    /**
     * @deprecated use the NBT-sensitive version {@link #setFluid(FluidStack)}
     */
    @Deprecated
    protected void setFluid(@Nullable Fluid fluid) {
        setFluid(new FluidStack(fluid, Fluid.BUCKET_VOLUME));
    }

    protected void setFluid(@Nullable FluidStack fluidStack) {
        if (fluidStack == null) container = new ItemStack(Items.BUCKET);
        else container = FluidUtil.getFilledBucket(fluidStack);
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new FluidTankProperties[]{new FluidTankProperties(getFluid(), Fluid.BUCKET_VOLUME)};
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (container.getCount() != 1 || resource == null
            || resource.amount < Fluid.BUCKET_VOLUME
            || container.getItem() instanceof ItemBucketMilk
            || getFluid() != null
            || !canFillFluidType(resource)) {
            return 0;
        }

        if (doFill) {
            setFluid(resource);
        }

        return Fluid.BUCKET_VOLUME;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (container.getCount() != 1 || resource == null || resource.amount < Fluid.BUCKET_VOLUME) {
            return null;
        }

        FluidStack fluidStack = getFluid();
        if (fluidStack != null && fluidStack.isFluidEqual(resource)) {
            if (doDrain) {
                setFluid((FluidStack) null);
            }
            return fluidStack;
        }

        return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (container.getCount() != 1 || maxDrain < Fluid.BUCKET_VOLUME) {
            return null;
        }

        FluidStack fluidStack = getFluid();
        if (fluidStack != null) {
            if (doDrain) {
                setFluid((FluidStack) null);
            }
            return fluidStack;
        }

        return null;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
    }

    @Override
    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.cast(this);
        }
        return null;
    }
}
