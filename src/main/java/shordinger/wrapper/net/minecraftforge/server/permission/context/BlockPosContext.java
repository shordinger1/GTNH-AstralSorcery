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

package shordinger.wrapper.net.minecraftforge.server.permission.context;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.ChunkPos;

public class BlockPosContext extends PlayerContext {

    private final BlockPos blockPos;
    private IBlockState blockState;
    private EnumFacing facing;

    public BlockPosContext(EntityPlayer ep, BlockPos pos, @Nullable IBlockState state, @Nullable EnumFacing f) {
        super(ep);
        blockPos = Preconditions.checkNotNull(pos, "BlockPos can't be null in BlockPosContext!");
        blockState = state;
        facing = f;
    }

    public BlockPosContext(EntityPlayer ep, ChunkPos pos) {
        this(ep, new BlockPos(pos.getXStart() + 8, 0, pos.getZStart() + 8), null, null);
    }

    @Override
    @Nullable
    public <T> T get(ContextKey<T> key) {
        if (key.equals(ContextKeys.POS)) {
            return (T) blockPos;
        } else if (key.equals(ContextKeys.BLOCK_STATE)) {
            if (blockState == null) {
                blockState = getWorld().getBlockState(blockPos);
            }

            return (T) blockState;
        } else if (key.equals(ContextKeys.FACING)) {
            return (T) facing;
        }

        return super.get(key);
    }

    @Override
    protected boolean covers(ContextKey<?> key) {
        return key.equals(ContextKeys.POS) || key.equals(ContextKeys.BLOCK_STATE)
            || (facing != null && key.equals(ContextKeys.FACING));
    }
}
