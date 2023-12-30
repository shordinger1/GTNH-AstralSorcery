/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.starlight.transmission.base;

import java.util.*;
import java.util.stream.Collectors;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import shordinger.astralsorcery.Tags;
import shordinger.astralsorcery.common.starlight.WorldNetworkHandler;
import shordinger.astralsorcery.common.starlight.network.StarlightTransmissionHandler;
import shordinger.astralsorcery.common.starlight.network.TransmissionWorldHandler;
import shordinger.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import shordinger.astralsorcery.common.starlight.transmission.NodeConnection;
import shordinger.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import shordinger.astralsorcery.common.util.RaytraceAssist;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.astralsorcery.migration.block.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimplePrismTransmissionNode
 * Created by HellFirePvP
 * Date: 03.08.2016 / 16:58
 */
public class SimplePrismTransmissionNode implements IPrismTransmissionNode {

    private boolean ignoreBlockCollision = false;

    private final Map<BlockPos, PrismNext> nextNodes = new HashMap<>();

    private BlockPos thisPos;

    private final Set<BlockPos> sourcesToThis = new HashSet<>();

    public SimplePrismTransmissionNode(BlockPos thisPos) {
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
        if (handle != null) {
            boolean anyChange = false;
            for (PrismNext next : nextNodes.values()) {
                boolean oldState = next.reachable;
                next.reachable = ignoreBlockCollision || next.rayAssist.isClear(world);
                if (next.reachable != oldState) {
                    anyChange = true;
                }
            }
            if (anyChange) {
                handle.notifyTransmissionNodeChange(this);
            }
        }
    }

    public boolean ignoresBlockCollision() {
        return ignoreBlockCollision;
    }

    @Override
    public boolean notifyUnlink(World world, BlockPos to) {
        return nextNodes.remove(to) != null;
    }

    @Override
    public void notifyLink(World world, BlockPos pos) {
        addLink(world, pos, true, false);
    }

    private void addLink(World world, BlockPos pos, boolean doRayCheck, boolean previousRayState) {
        PrismNext nextNode = new PrismNext(this, world, thisPos, pos, doRayCheck, previousRayState);
        this.nextNodes.put(pos, nextNode);
    }

    @Override
    public boolean notifyBlockChange(World world, BlockPos at) {
        boolean anyChange = false;
        for (PrismNext next : nextNodes.values()) {
            if (next.notifyBlockPlace(world, thisPos, at)) anyChange = true;
        }
        return anyChange;
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
    public List<NodeConnection<IPrismTransmissionNode>> queryNext(WorldNetworkHandler handler) {
        List<NodeConnection<IPrismTransmissionNode>> nodes = new LinkedList<>();
        for (BlockPos pos : nextNodes.keySet()) {
            nodes.add(new NodeConnection<>(handler.getTransmissionNode(pos), pos, nextNodes.get(pos).reachable));
        }
        return nodes;
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

        NBTTagList nextList = compound.getTagList("nextList", 10);
        for (int i = 0; i < nextList.tagCount(); i++) {
            NBTTagCompound tag = nextList.getCompoundTagAt(i);
            BlockPos next = NBTHelper.readBlockPosFromNBT(tag);
            boolean oldState = tag.getBoolean("rayState");
            addLink(null, next, false, oldState); // Rebuild link.
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

        NBTTagList nextList = new NBTTagList();
        for (BlockPos next : nextNodes.keySet()) {
            PrismNext prism = nextNodes.get(next);
            NBTTagCompound pos = new NBTTagCompound();
            NBTHelper.writeBlockPosToNBT(next, pos);
            pos.setBoolean("rayState", prism.reachable);
            nextList.appendTag(pos);
        }
        compound.setTag("nextList", nextList);
    }

    private static class PrismNext {

        private final SimplePrismTransmissionNode parent;
        private boolean reachable = false;
        private double distanceSq;
        private final BlockPos pos;
        private RaytraceAssist rayAssist = null;

        private PrismNext(SimplePrismTransmissionNode parent, World world, BlockPos start, BlockPos end,
                          boolean doRayTest, boolean oldRayState) {
            this.parent = parent;
            this.pos = end;
            this.rayAssist = new RaytraceAssist(start, end);
            if (doRayTest) {
                this.reachable = parent.ignoreBlockCollision || rayAssist.isClear(world);
            } else {
                this.reachable = oldRayState;
            }
            this.distanceSq = end.distanceSq(start.getX(), start.getY(), start.getZ());
        }

        private boolean notifyBlockPlace(World world, BlockPos connect, BlockPos at) {
            double dstStart = connect.distanceSq(at.getX(), at.getY(), at.getZ());
            double dstEnd = pos.distanceSq(at.getX(), at.getY(), at.getZ());
            if (dstStart > distanceSq || dstEnd > distanceSq) return false;
            boolean oldState = this.reachable;
            this.reachable = parent.ignoreBlockCollision || rayAssist.isClear(world);
            return this.reachable != oldState;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimplePrismTransmissionNode that = (SimplePrismTransmissionNode) o;
        return !(thisPos != null ? !thisPos.equals(that.thisPos) : that.thisPos != null);

    }

    @Override
    public int hashCode() {
        return thisPos != null ? thisPos.hashCode() : 0;
    }

    public static class Provider implements TransmissionClassRegistry.TransmissionProvider {

        @Override
        public IPrismTransmissionNode provideEmptyNode() {
            return new SimplePrismTransmissionNode(null);
        }

        @Override
        public String getIdentifier() {
            return Tags.MODID + ":SimplePrismTransmissionNode";
        }

    }

}
