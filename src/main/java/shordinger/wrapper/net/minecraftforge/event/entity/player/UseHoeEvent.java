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

package shordinger.wrapper.net.minecraftforge.event.entity.player;

import javax.annotation.Nonnull;

import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.Cancelable;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired when a player attempts to use a Hoe on a block, it
 * can be canceled to completely prevent any further processing.
 * <p>
 * You can also set the result to ALLOW to mark the event as processed
 * and damage the hoe.
 * <p>
 * setResult(ALLOW) is the same as the old setHandled();
 */
@Cancelable
@Event.HasResult
public class UseHoeEvent extends PlayerEvent {

    private final ItemStack current;
    private final World world;
    private final BlockPos pos;

    public UseHoeEvent(EntityPlayer player, @Nonnull ItemStack current, World world, BlockPos pos) {
        super(player);
        this.current = current;
        this.world = world;
        this.pos = pos;
    }

    @Nonnull
    public ItemStack getCurrent() {
        return current;
    }

    public World getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }
}
