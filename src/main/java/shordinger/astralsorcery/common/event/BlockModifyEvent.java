/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.event;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import cpw.mods.fml.common.eventhandler.Event;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockModifyEvent
 * Created by HellFirePvP
 * Date: 04.08.2016 / 10:49
 */
public class BlockModifyEvent extends Event {

    private final Chunk chunk;
    private final World world;
    private final BlockPos at;
    private final IBlockState oldState, newState;

    public BlockModifyEvent(World world, Chunk chunk, BlockPos at, IBlockState oldState, IBlockState newState) {
        this.at = at;
        this.chunk = chunk;
        this.world = world;
        this.oldState = oldState;
        this.newState = newState;
    }

    public BlockPos getPos() {
        return at;
    }

    public World getWorld() {
        return world;
    }

    public Chunk getChunk() {
        return chunk;
    }

    @Nullable
    public TileEntity getTileEntity() {
        return world.getTileEntity(getPos());
    }

    public IBlockState getOldState() {
        return oldState;
    }

    public IBlockState getNewState() {
        return newState;
    }

    public Block getOldBlock() {
        return oldState.getBlock();
    }

    public Block getNewBlock() {
        return newState.getBlock();
    }

}
