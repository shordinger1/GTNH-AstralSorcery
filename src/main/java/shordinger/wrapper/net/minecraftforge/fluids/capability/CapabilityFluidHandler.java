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

package shordinger.wrapper.net.minecraftforge.fluids.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraftforge.common.capabilities.Capability;
import shordinger.wrapper.net.minecraftforge.common.capabilities.CapabilityInject;
import shordinger.wrapper.net.minecraftforge.common.capabilities.CapabilityManager;
import shordinger.wrapper.net.minecraftforge.fluids.Fluid;
import shordinger.wrapper.net.minecraftforge.fluids.FluidStack;
import shordinger.wrapper.net.minecraftforge.fluids.FluidTank;
import shordinger.wrapper.net.minecraftforge.fluids.IFluidTank;
import shordinger.wrapper.net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

public class CapabilityFluidHandler {

    @CapabilityInject(IFluidHandler.class)
    public static Capability<IFluidHandler> FLUID_HANDLER_CAPABILITY = null;
    @CapabilityInject(IFluidHandlerItem.class)
    public static Capability<IFluidHandlerItem> FLUID_HANDLER_ITEM_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(
            IFluidHandler.class,
            new DefaultFluidHandlerStorage<>(),
            () -> new FluidTank(Fluid.BUCKET_VOLUME));

        CapabilityManager.INSTANCE.register(
            IFluidHandlerItem.class,
            new DefaultFluidHandlerStorage<>(),
            () -> new FluidHandlerItemStack(new ItemStack(Items.BUCKET), Fluid.BUCKET_VOLUME));
    }

    private static class DefaultFluidHandlerStorage<T extends IFluidHandler> implements Capability.IStorage<T> {

        @Override
        public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
            if (!(instance instanceof IFluidTank))
                throw new RuntimeException("IFluidHandler instance does not implement IFluidTank");
            NBTTagCompound nbt = new NBTTagCompound();
            IFluidTank tank = (IFluidTank) instance;
            FluidStack fluid = tank.getFluid();
            if (fluid != null) {
                fluid.writeToNBT(nbt);
            } else {
                nbt.setString("Empty", "");
            }
            nbt.setInteger("Capacity", tank.getCapacity());
            return nbt;
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {
            if (!(instance instanceof FluidTank))
                throw new RuntimeException("IFluidHandler instance is not instance of FluidTank");
            NBTTagCompound tags = (NBTTagCompound) nbt;
            FluidTank tank = (FluidTank) instance;
            tank.setCapacity(tags.getInteger("Capacity"));
            tank.readFromNBT(tags);
        }
    }
}
