/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.structure.change;

import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.astralsorcery.common.data.world.WorldCacheManager;
import shordinger.astralsorcery.common.structure.StructureMatcher;
import shordinger.astralsorcery.common.util.log.LogCategory;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.ChunkPos;
import shordinger.wrapper.net.minecraft.world.World;

import java.util.Collection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ChangeSubscriber
 * Created by HellFirePvP
 * Date: 02.12.2018 / 11:53
 */
public class ChangeSubscriber<T extends StructureMatcher> {

    private BlockPos requester;
    private T matcher;

    private BlockStateChangeSet changeSet = new BlockStateChangeSet();
    private Boolean isMatching = null;

    private Collection<ChunkPos> affectedChunkCache = null;

    public ChangeSubscriber(BlockPos requester, T matcher) {
        this.requester = requester;
        this.matcher = matcher;
    }

    public BlockPos getRequester() {
        return requester;
    }

    public T getMatcher() {
        return matcher;
    }

    public Collection<ChunkPos> getObservableChunks() {
        if (affectedChunkCache == null) {
            affectedChunkCache = Lists.newArrayList(
                getMatcher().getObservableArea()
                    .getAffectedChunks(getRequester()));
        }
        return affectedChunkCache;
    }

    public boolean observes(BlockPos pos) {
        return this.getMatcher()
            .getObservableArea()
            .observes(pos.subtract(getRequester()));
    }

    public void addChange(BlockPos pos, IBlockState oldState, IBlockState newState) {
        this.changeSet.addChange(pos.subtract(getRequester()), oldState, newState);
    }

    public boolean matches(World world) {
        if (this.isMatching != null && this.changeSet.isEmpty()) {
            return isMatching;
        }

        Boolean matchedBefore = this.isMatching;
        LogCategory.STRUCTURE_MATCH.info(
            () -> "Updating matching for " + this.getRequester()
                + " with "
                + this.changeSet.getChanges()
                .size()
                + " changes.");

        this.isMatching = this.matcher.notifyChange(world, this.getRequester(), this.changeSet);
        this.changeSet.reset();
        WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.STRUCTURE_MATCH)
            .markDirty();

        LogCategory.STRUCTURE_MATCH.info(
            () -> "Updating matched-state from " + (matchedBefore == null ? "<unmatched>" : matchedBefore.toString())
                + " to "
                + this.isMatching);

        return this.isMatching;
    }

    public void readFromNBT(NBTTagCompound tag) {
        this.affectedChunkCache = null;

        this.matcher.readFromNBT(tag.getCompoundTag("matchData"));
        this.changeSet.readFromNBT(tag.getCompoundTag("changeData"));
        this.requester = NBTHelper.readBlockPosFromNBT(tag);
        if (tag.hasKey("isMatching")) {
            this.isMatching = tag.getBoolean("isMatching");
        } else {
            this.isMatching = null;
        }
    }

    public void writeToNBT(NBTTagCompound tag) {
        NBTHelper.setAsSubTag(tag, "matchData", this.matcher::writeToNBT);
        NBTHelper.setAsSubTag(tag, "changeData", this.changeSet::writeToNBT);

        NBTHelper.writeBlockPosToNBT(this.requester, tag);
        if (this.isMatching != null) {
            tag.setBoolean("isMatching", this.isMatching);
        }
    }
}
