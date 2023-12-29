/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile.storage;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;

import com.google.common.collect.Maps;

import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StorageNetwork
 * Created by HellFirePvP
 * Date: 01.12.2018 / 13:18
 */
public class StorageNetwork {

    private CoreArea master = null;
    private final Map<BlockPos, AxisAlignedBB> cores = Maps.newHashMap();

    // True if set.
    public boolean setMaster(@Nullable BlockPos pos) {
        if (pos == null) {
            this.master = null;
            return true;
        }
        if (cores.containsKey(pos)) {
            this.master = new CoreArea(pos, this.cores.get(pos));
            return true;
        }
        return false;
    }

    @Nullable
    public CoreArea getMaster() {
        return master;
    }

    // True if it didn't overwrite a previous one
    public boolean addCore(BlockPos pos, AxisAlignedBB box) {
        return this.cores.put(pos, box) == null;
    }

    // True if it had a position like that
    public boolean removeCore(BlockPos pos) {
        return this.cores.remove(pos) != null;
    }

    public List<CoreArea> getCores() {
        return MiscUtils.flatten(this.cores, CoreArea::new);
    }

    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();
        for (CoreArea coreData : this.getCores()) {
            NBTTagCompound coreTag = new NBTTagCompound();
            NBTHelper.writeBlockPosToNBT(coreData.getPos(), coreTag);
            NBTHelper.writeBoundingBox(coreData.getOffsetBox(), coreTag);
            list.appendTag(coreTag);
        }
        tag.setTag("cores", list);

        CoreArea master;
        if ((master = getMaster()) != null) {
            NBTHelper.setAsSubTag(tag, "master", nbt -> NBTHelper.writeBlockPosToNBT(master.getPos(), nbt));
        }
    }

    public void readFromNBT(NBTTagCompound tag) {
        this.cores.clear();

        NBTTagList list = tag.getTagList("cores", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound coreTag = list.getCompoundTagAt(i);
            BlockPos pos = NBTHelper.readBlockPosFromNBT(coreTag);
            AxisAlignedBB box = NBTHelper.readBoundingBox(coreTag);
            this.addCore(pos, box);
        }

        this.setMaster(null);
        if (tag.hasKey("master", Constants.NBT.TAG_COMPOUND)) {
            BlockPos pos = NBTHelper.readBlockPosFromNBT(tag.getCompoundTag("master"));
            this.setMaster(pos);
        }
    }

    public static class CoreArea {

        private final BlockPos pos;
        private final AxisAlignedBB offsetBox;

        private CoreArea(BlockPos pos, AxisAlignedBB offsetBox) {
            this.pos = pos;
            this.offsetBox = offsetBox;
        }

        public BlockPos getPos() {
            return pos;
        }

        public AxisAlignedBB getOffsetBox() {
            return offsetBox;
        }

        public AxisAlignedBB getRealBox() {
            return offsetBox.offset(getPos());
        }
    }

}
