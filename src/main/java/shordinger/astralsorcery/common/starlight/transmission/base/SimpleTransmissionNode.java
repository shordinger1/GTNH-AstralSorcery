/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.starlight.transmission.base;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.starlight.WorldNetworkHandler;
import shordinger.astralsorcery.common.starlight.network.StarlightTransmissionHandler;
import shordinger.astralsorcery.common.starlight.network.TransmissionWorldHandler;
import shordinger.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import shordinger.astralsorcery.common.starlight.transmission.ITransmissionNode;
import shordinger.astralsorcery.common.starlight.transmission.NodeConnection;
import shordinger.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import shordinger.astralsorcery.common.util.RaytraceAssist;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.nbt.NBTTagList;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TransmissionNodeLens
 * Created by HellFirePvP
 * Date: 03.08.2016 / 11:09
 */
public class SimpleTransmissionNode implements ITransmissionNode {

    private boolean ignoreBlockCollision = false;

    private boolean nextReachable = false;
    private BlockPos nextPos = null;
    private double dstToNextSq = 0;
    private RaytraceAssist assistNext = null;

    private BlockPos thisPos;

    private Set<BlockPos> sourcesToThis = new HashSet<>();

    public SimpleTransmissionNode(BlockPos thisPos) {
        this.thisPos = thisPos;
    }

    @Override
    public BlockPos getLocationPos() {
        return thisPos;
    }

    public void updateIgnoreBlockCollisionState(World world, boolean ignoreBlockCollision) {
        this.ignoreBlockCollision = ignoreBlockCollision;
        TransmissionWorldHandler handle = StarlightTransmissionHandler.getInstance()
            .getWorldHandler(world);
        if (assistNext != null && handle != null) {
            boolean oldState = this.nextReachable;
            this.nextReachable = ignoreBlockCollision || assistNext.isClear(world);
            if (nextReachable != oldState) {
                handle.notifyTransmissionNodeChange(this);
            }
        }
    }

    public boolean ignoresBlockCollision() {
        return ignoreBlockCollision;
    }

    @Override
    public boolean notifyUnlink(World world, BlockPos to) {
        if (to.equals(nextPos)) { // cleanup
            this.nextPos = null;
            this.assistNext = null;
            this.dstToNextSq = 0;
            this.nextReachable = false;
            return true;
        }
        return false;
    }

    @Override
    public void notifyLink(World world, BlockPos pos) {
        addLink(world, pos, true, false);
    }

    private void addLink(World world, BlockPos pos, boolean doRayTest, boolean oldRayState) {
        this.nextPos = pos;
        this.assistNext = new RaytraceAssist(thisPos, nextPos);
        if (doRayTest) {
            this.nextReachable = this.ignoreBlockCollision || assistNext.isClear(world);
        } else {
            this.nextReachable = oldRayState;
        }
        this.dstToNextSq = pos.distanceSq(thisPos.getX(), thisPos.getY(), thisPos.getZ());
    }

    @Override
    public boolean notifyBlockChange(World world, BlockPos at) {
        if (nextPos == null) return false;
        double dstStart = thisPos.distanceSq(at.getX(), at.getY(), at.getZ());
        double dstEnd = nextPos.distanceSq(at.getX(), at.getY(), at.getZ());
        if (dstStart > dstToNextSq || dstEnd > dstToNextSq) return false; // out of range
        boolean oldState = this.nextReachable;
        this.nextReachable = ignoreBlockCollision || assistNext.isClear(world);
        return this.nextReachable != oldState;
    }

    @Override
    public void notifySourceLink(World world, BlockPos source) {
        if (!sourcesToThis.contains(source)) sourcesToThis.add(source);
    }

    @Override
    public void notifySourceUnlink(World world, BlockPos source) {
        sourcesToThis.remove(source);
    }

    @Override
    public NodeConnection<IPrismTransmissionNode> queryNextNode(WorldNetworkHandler handler) {
        if (nextPos == null) return null;
        return new NodeConnection<>(handler.getTransmissionNode(nextPos), nextPos, nextReachable);
    }

    @Override
    public List<BlockPos> getSources() {
        return sourcesToThis.stream()
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public TransmissionClassRegistry.TransmissionProvider getProvider() {
        return new Provider();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.thisPos = NBTHelper.readBlockPosFromNBT(compound);
        this.sourcesToThis.clear();
        this.ignoreBlockCollision = compound.getBoolean("ignoreBlockCollision");

        NBTTagList list = compound.getTagList("sources", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            sourcesToThis.add(NBTHelper.readBlockPosFromNBT(list.getCompoundTagAt(i)));
        }

        if (compound.hasKey("nextPos")) {
            NBTTagCompound tag = compound.getCompoundTag("nextPos");
            BlockPos next = NBTHelper.readBlockPosFromNBT(tag);
            boolean oldRay = tag.getBoolean("rayState");
            addLink(null, next, false, oldRay);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTHelper.writeBlockPosToNBT(thisPos, compound);
        compound.setBoolean("ignoreBlockCollision", this.ignoreBlockCollision);

        NBTTagList sources = new NBTTagList();
        for (BlockPos source : sourcesToThis) {
            NBTTagCompound comp = new NBTTagCompound();
            NBTHelper.writeBlockPosToNBT(source, comp);
            sources.appendTag(comp);
        }
        compound.setTag("sources", sources);

        if (nextPos != null) {
            NBTTagCompound pos = new NBTTagCompound();
            NBTHelper.writeBlockPosToNBT(nextPos, pos);
            pos.setBoolean("rayState", nextReachable);
            compound.setTag("nextPos", pos);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleTransmissionNode that = (SimpleTransmissionNode) o;
        return !(thisPos != null ? !thisPos.equals(that.thisPos) : that.thisPos != null);
    }

    @Override
    public int hashCode() {
        return thisPos != null ? thisPos.hashCode() : 0;
    }

    public static class Provider implements TransmissionClassRegistry.TransmissionProvider {

        @Override
        public IPrismTransmissionNode provideEmptyNode() {
            return new SimpleTransmissionNode(null);
        }

        @Override
        public String getIdentifier() {
            return AstralSorcery.MODID + ":SimpleTransmissionNode";
        }

    }

}
