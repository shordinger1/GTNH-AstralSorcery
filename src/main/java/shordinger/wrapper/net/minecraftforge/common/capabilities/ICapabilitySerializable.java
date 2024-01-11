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

package shordinger.wrapper.net.minecraftforge.common.capabilities;

import shordinger.wrapper.net.minecraft.nbt.NBTBase;
import shordinger.wrapper.net.minecraftforge.common.util.INBTSerializable;

// Just a mix of the two, useful in patches to lower the size.
public interface ICapabilitySerializable<T extends NBTBase> extends ICapabilityProvider, INBTSerializable<T> {
}
