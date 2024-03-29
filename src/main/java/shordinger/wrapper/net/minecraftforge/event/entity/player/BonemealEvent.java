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
import javax.annotation.Nullable;

import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.Cancelable;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is called when a player attempts to use Bonemeal on a block.
 * It can be canceled to completely prevent any further processing.
 * <p>
 * You can also set the result to ALLOW to mark the event as processed
 * and use up a bonemeal from the stack but do no further processing.
 * <p>
 * setResult(ALLOW) is the same as the old setHandled()
 */
@Cancelable
@Event.HasResult
public class BonemealEvent extends PlayerEvent {

    private final World world;
    private final BlockPos pos;
    private final IBlockState block;
    private final EnumHand hand;
    private final ItemStack stack;

    public BonemealEvent(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos,
                         @Nonnull IBlockState block, @Nullable EnumHand hand, @Nonnull ItemStack stack) {
        super(player);
        this.world = world;
        this.pos = pos;
        this.block = block;
        this.hand = hand;
        this.stack = stack;
    }

    public World getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }

    public IBlockState getBlock() {
        return block;
    }

    @Nullable
    public EnumHand getHand() {
        return hand;
    }

    @Nonnull
    public ItemStack getStack() {
        return stack;
    }
}
