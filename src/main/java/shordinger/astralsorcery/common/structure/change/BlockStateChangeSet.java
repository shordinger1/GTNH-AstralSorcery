/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.structure.change;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import com.google.common.collect.Maps;

import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStateChangeSet
 * Created by HellFirePvP
 * Date: 02.12.2018 / 01:09
 */
public final class BlockStateChangeSet {

    private final Map<BlockPos, StateChange> changes = Maps.newHashMap();

    public final void reset() {
        this.changes.clear();
    }

    public void addChange(BlockPos pos, IBlockState oldState, IBlockState newState) {
        StateChange oldChangeSet = this.changes.get(pos);
        if (oldChangeSet != null) { // Chain changes so absolute old one is still consistent!
            this.changes.put(pos, new StateChange(pos, oldChangeSet.oldState, newState));
        } else {
            this.changes.put(pos, new StateChange(pos, oldState, newState));
        }
    }

    public boolean hasChange(BlockPos pos) {
        return this.changes.containsKey(pos);
    }

    public boolean isEmpty() {
        return this.changes.isEmpty();
    }

    public Collection<StateChange> getChanges() {
        return Collections.unmodifiableCollection(this.changes.values());
    }

    public void readFromNBT(NBTTagCompound cmp) {
        this.changes.clear();

        NBTTagList changeList = cmp.getTagList("changeList", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < changeList.tagCount(); i++) {
            NBTTagCompound changeTag = changeList.getCompoundTagAt(i);

            BlockPos pos = NBTHelper.readBlockPosFromNBT(changeTag);
            IBlockState oldState = NBTHelper
                .getBlockStateFromTag(changeTag.getCompoundTag("oldState"), Blocks.AIR.getDefaultState());
            IBlockState newState = NBTHelper
                .getBlockStateFromTag(changeTag.getCompoundTag("newState"), Blocks.AIR.getDefaultState());
            this.changes.put(pos, new StateChange(pos, oldState, newState));
        }
    }

    public void writeToNBT(NBTTagCompound cmp) {
        NBTTagList changes = new NBTTagList();
        for (StateChange change : this.changes.values()) {
            NBTTagCompound tag = new NBTTagCompound();
            NBTHelper.writeBlockPosToNBT(change.pos, tag);
            tag.setTag("oldState", NBTHelper.getBlockStateNBTTag(change.oldState));
            tag.setTag("newState", NBTHelper.getBlockStateNBTTag(change.newState));
            changes.appendTag(tag);
        }

        cmp.setTag("changeList", changes);
    }

    public static final class StateChange {

        public final BlockPos pos;
        public final IBlockState oldState, newState;

        private StateChange(BlockPos pos, IBlockState oldState, IBlockState newState) {
            this.pos = pos;
            this.oldState = oldState;
            this.newState = newState;
        }

    }

}
