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

package shordinger.wrapper.net.minecraftforge.client;

import javax.annotation.Nonnull;

import shordinger.wrapper.net.minecraft.client.renderer.color.IItemColor;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraftforge.fluids.FluidStack;
import shordinger.wrapper.net.minecraftforge.fluids.FluidUtil;

public class FluidContainerColorer implements IItemColor {

    @Override
    public int colorMultiplier(@Nonnull ItemStack stack, int tintIndex) {
        if (tintIndex != 1) return 0xFFFFFFFF;
        FluidStack fluidStack = FluidUtil.getFluidContained(stack);
        if (fluidStack == null) return 0xFFFFFFFF;
        return fluidStack.getFluid()
            .getColor(fluidStack);
    }
}
