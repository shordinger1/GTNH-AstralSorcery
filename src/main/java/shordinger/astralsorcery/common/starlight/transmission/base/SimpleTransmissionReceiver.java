/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.starlight.transmission.base;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import shordinger.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.astralsorcery.migration.block.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleTransmissionReceiver
 * Created by HellFirePvP
 * Date: 05.08.2016 / 13:59
 */
public abstract class SimpleTransmissionReceiver implements ITransmissionReceiver {

    private BlockPos thisPos;

    private final Set<BlockPos> sourcesToThis = new HashSet<>();

    public SimpleTransmissionReceiver(BlockPos thisPos) {
        this.thisPos = thisPos;
    }

    @Override
    public BlockPos getLocationPos() {
        return thisPos;
    }

    @Override
    public void notifySourceLink(World world, BlockPos source) {
        sourcesToThis.add(source);
    }

    @Override
    public void notifySourceUnlink(World world, BlockPos source) {
        sourcesToThis.remove(source);
    }

    @Override
    public boolean notifyBlockChange(World world, BlockPos changed) {
        return false;
    }

    @Override
    public List<BlockPos> getSources() {
        return new LinkedList<>(sourcesToThis);
    }

    @Nullable
    public <T extends TileEntity> T getTileAtPos(World world, Class<T> tileClass) {
        return MiscUtils.getTileAt(world, getLocationPos(), tileClass, false);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.thisPos = NBTHelper.readBlockPosFromNBT(compound);
        this.sourcesToThis.clear();

        NBTTagList list = compound.getTagList("sources", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            sourcesToThis.add(NBTHelper.readBlockPosFromNBT(list.getCompoundTagAt(i)));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTHelper.writeBlockPosToNBT(thisPos, compound);

        NBTTagList sources = new NBTTagList();
        for (BlockPos source : sourcesToThis) {
            NBTTagCompound comp = new NBTTagCompound();
            NBTHelper.writeBlockPosToNBT(source, comp);
            sources.appendTag(comp);
        }
        compound.setTag("sources", sources);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleTransmissionReceiver that = (SimpleTransmissionReceiver) o;
        return !(thisPos != null ? !thisPos.equals(that.thisPos) : that.thisPos != null);
    }

}
