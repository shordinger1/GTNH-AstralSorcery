/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.starlight;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.world.World;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.data.world.WorldCacheManager;
import shordinger.astralsorcery.common.data.world.data.LightNetworkBuffer;
import shordinger.astralsorcery.common.starlight.network.StarlightTransmissionHandler;
import shordinger.astralsorcery.common.starlight.network.TransmissionWorldHandler;
import shordinger.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import shordinger.astralsorcery.common.starlight.transmission.ITransmissionSource;
import shordinger.astralsorcery.common.starlight.transmission.NodeConnection;
import shordinger.astralsorcery.common.util.data.Tuple;
import shordinger.astralsorcery.common.util.data.WorldBlockPos;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.ChunkPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldNetworkHandler
 * Created by HellFirePvP
 * Date: 02.08.2016 / 23:09
 */
public class WorldNetworkHandler {

    private final LightNetworkBuffer buffer;
    private final World world;

    public WorldNetworkHandler(LightNetworkBuffer lightNetworkBuffer, World world) {
        this.buffer = lightNetworkBuffer;
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public static WorldNetworkHandler getNetworkHandler(World world) {
        LightNetworkBuffer buffer = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.LIGHT_NETWORK);
        return buffer.getNetworkHandler(world);
    }

    public void informBlockChange(BlockPos at) {
        List<LightNetworkBuffer.ChunkSectionNetworkData> relatedData = getAffectedChunkSections(at);
        if (relatedData.isEmpty()) return; // lucky. nothing to do.
        TransmissionWorldHandler handle = StarlightTransmissionHandler.getInstance()
            .getWorldHandler(getWorld());

        for (LightNetworkBuffer.ChunkSectionNetworkData data : relatedData) {
            if (data == null) continue;
            Collection<IPrismTransmissionNode> transmissionNodes = data.getAllTransmissionNodes();
            for (IPrismTransmissionNode node : transmissionNodes) {
                if (node.notifyBlockChange(getWorld(), at)) {
                    if (handle != null) {
                        handle.notifyTransmissionNodeChange(node);
                    }
                }
            }
        }
    }

    public void attemptAutoLinkTo(BlockPos at) {
        TransmissionWorldHandler handle = StarlightTransmissionHandler.getInstance()
            .getWorldHandler(world);
        for (Tuple<BlockPos, IIndependentStarlightSource> source : getAllSources()) {
            if (!source.value.providesAutoLink()) continue;

            if (source.key.distanceSq((WorldBlockPos) at) <= 256) {
                IPrismTransmissionNode node = getTransmissionNode(source.key);
                if (node == null) {
                    AstralSorcery.log
                        .warn("Didn't find a TransmissionNode at a position that's supposed to be a source!");
                    AstralSorcery.log.warn("Details: Dim=" + getWorld().provider.dimensionId + " at " + source.key);
                    continue;
                }
                if (!(node instanceof ITransmissionSource)) {
                    AstralSorcery.log.warn("Found TransmissionNode that isn't a source at a source position!");
                    AstralSorcery.log.warn("Details: Dim=" + getWorld().provider.dimensionId + " at " + source.key);
                    continue;
                }
                ITransmissionSource sourceNode = (ITransmissionSource) node;
                if (sourceNode.getLocationPos()
                    .getY() <= at.getY()) continue;
                sourceNode.notifyLink(getWorld(), at);
                markDirty();

                if (handle != null) {
                    handle.notifyTransmissionNodeChange(sourceNode);
                }
            }
        }
    }

    public void removeAutoLinkTo(BlockPos at) {
        TransmissionWorldHandler handle = StarlightTransmissionHandler.getInstance()
            .getWorldHandler(world);
        for (Tuple<BlockPos, IIndependentStarlightSource> source : getAllSources()) {
            if (!source.value.providesAutoLink()) continue;

            if (source.key.distanceSq((WorldBlockPos) at) <= 256) {
                IPrismTransmissionNode node = getTransmissionNode(source.key);
                if (node == null) {
                    AstralSorcery.log
                        .warn("Didn't find a TransmissionNode at a position that's supposed to be a source!");
                    AstralSorcery.log.warn("Details: Dim=" + getWorld().provider.dimensionId + " at " + source.key);
                    continue;
                }
                if (!(node instanceof ITransmissionSource)) {
                    AstralSorcery.log.warn("Found TransmissionNode that isn't a source at a source position!");
                    AstralSorcery.log.warn("Details: Dim=" + getWorld().provider.dimensionId + " at " + source.key);
                    continue;
                }
                ITransmissionSource sourceNode = (ITransmissionSource) node;
                if (sourceNode.notifyUnlink(getWorld(), at)) {
                    markDirty();
                    if (handle != null) {
                        handle.notifyTransmissionNodeChange(sourceNode);
                    }
                }
            }
        }
    }

    @Nullable
    public IPrismTransmissionNode getTransmissionNode(@Nullable BlockPos pos) {
        if (pos == null) return null;
        LightNetworkBuffer.ChunkSectionNetworkData section = getNetworkData(pos);
        if (section != null) {
            return section.getTransmissionNode(pos);
        }
        return null;
    }

    public void markDirty() {
        buffer.markDirty();
    }

    @Nullable
    public IIndependentStarlightSource getSourceAt(BlockPos pos) {
        return buffer.getSource(pos);
    }

    public Collection<Tuple<BlockPos, IIndependentStarlightSource>> getAllSources() {
        return buffer.getAllSources();
    }

    public void removeSource(IStarlightSource source) {
        removeThisSourceFromNext(source);
        removeThisNextFromSources(source);

        buffer.removeSource(source.getTrPos());
    }

    public void removeTransmission(IStarlightTransmission transmission) {
        removeThisSourceFromNext(transmission);
        removeThisNextFromSources(transmission);

        buffer.removeTransmission(transmission.getTrPos());
    }

    public void addNewSourceTile(IStarlightSource source) {
        buffer.addSource(source, source.getTrPos());

        linkNextToThisSources(source);
    }

    public void addTransmissionTile(IStarlightTransmission transmission) {
        buffer.addTransmission(transmission, transmission.getTrPos());

        linkNextToThisSources(transmission);
    }

    // For all sources of this "tr" inform the transmission system that the connection might've changed.
    private void removeThisNextFromSources(IStarlightTransmission tr) {
        TransmissionWorldHandler handle = StarlightTransmissionHandler.getInstance()
            .getWorldHandler(getWorld());
        if (handle == null) return;

        IPrismTransmissionNode node = tr.getNode();
        if (node == null) {
            new Throwable().printStackTrace();
            AstralSorcery.log.warn(
                "Could not find transmission node for Transmission tile '" + tr.getClass()
                    .getSimpleName() + "'");
            AstralSorcery.log.warn(
                "This is an implementation error. Report it along with the steps to create this, if you come across this.");
            return;
        }

        for (BlockPos pos : node.getSources()) {
            IPrismTransmissionNode sourceNode = getTransmissionNode(pos);
            if (sourceNode != null) {
                handle.notifyTransmissionNodeChange(sourceNode);
            }
        }
    }

    // What it does: For all "next"'s of this "tr" inform them that "tr" has been removed and
    // thus remove "tr" from their sources.
    private void removeThisSourceFromNext(IStarlightTransmission tr) {
        IPrismTransmissionNode node = tr.getNode();
        if (node == null) {
            AstralSorcery.log.warn(
                "Could not find transmission node for Transmission tile '" + tr.getClass()
                    .getSimpleName() + "'");
            AstralSorcery.log.warn(
                "This is an implementation error. Report it along with the steps to create this, if you come across this.");
            return;
        }
        TransmissionWorldHandler handle = StarlightTransmissionHandler.getInstance()
            .getWorldHandler(getWorld());
        if (handle != null) {
            handle.notifyTransmissionNodeChange(node);
        }

        BlockPos thisPos = tr.getTrPos();
        List<NodeConnection<IPrismTransmissionNode>> nodeConnections = node.queryNext(this);
        for (NodeConnection<IPrismTransmissionNode> connection : nodeConnections) {
            if (connection.node() != null) {
                connection.node()
                    .notifySourceUnlink(getWorld(), thisPos);
                if (handle != null) {
                    handle.notifyTransmissionNodeChange(connection.node());
                }
            }
        }
    }

    // That's what i call resource intensive.
    // What it does: Checks if there is a Node that has a "next" at this "tr"'s position.
    // If yes, it'll add that node as source for this "tr" to make backwards find possible.
    private void linkNextToThisSources(IStarlightTransmission tr) {
        IPrismTransmissionNode node = tr.getNode();
        if (node == null) {
            AstralSorcery.log.warn(
                "Previously added Transmission tile '" + tr.getClass()
                    .getSimpleName() + "' didn't create a Transmission node!");
            AstralSorcery.log.warn(
                "This is an implementation error. Report it along with the steps to create this, if you come across this.");
            return;
        }
        BlockPos thisPos = tr.getTrPos();
        TransmissionWorldHandler handle = StarlightTransmissionHandler.getInstance()
            .getWorldHandler(getWorld());
        List<LightNetworkBuffer.ChunkSectionNetworkData> dataList = getAffectedChunkSections(tr.getTrPos());
        for (LightNetworkBuffer.ChunkSectionNetworkData data : dataList) {
            if (data == null) continue;
            for (IPrismTransmissionNode otherNode : data.getAllTransmissionNodes()) {
                List<NodeConnection<IPrismTransmissionNode>> nodeConnections = otherNode.queryNext(this);
                for (NodeConnection<IPrismTransmissionNode> connection : nodeConnections) {
                    if (connection.to()
                        .equals(thisPos)) {
                        node.notifySourceLink(getWorld(), otherNode.getLocationPos());
                        if (handle != null) {
                            handle.notifyTransmissionNodeChange(otherNode);
                        }
                    }
                }
            }
        }
    }

    // Collects a 3x3 field of chunkData, centered around the given pos.
    // Might be empty, if there's no network nearby.
    private List<LightNetworkBuffer.ChunkSectionNetworkData> getAffectedChunkSections(BlockPos centralPos) {
        List<LightNetworkBuffer.ChunkSectionNetworkData> dataList = new LinkedList<>();
        ChunkPos central = new ChunkPos(centralPos);
        int posYLevel = (centralPos.getY() & 255) >> 4;
        for (int xx = -1; xx <= 1; xx++) {
            for (int zz = -1; zz <= 1; zz++) {
                for (int yy = -1; yy <= 1; yy++) {
                    queryData(new ChunkPos(central.x + xx, central.z + zz), posYLevel + yy, dataList);
                }
            }
        }
        return dataList;
    }

    private void queryData(ChunkPos pos, int yLevel, List<LightNetworkBuffer.ChunkSectionNetworkData> out) {
        LightNetworkBuffer.ChunkSectionNetworkData data = buffer.getSectionData(pos, yLevel);
        if (data != null && !data.isEmpty()) out.add(data);
    }

    @Nullable
    private LightNetworkBuffer.ChunkSectionNetworkData getNetworkData(BlockPos at) {
        LightNetworkBuffer.ChunkSectionNetworkData data = buffer.getSectionData(at);
        if (data != null && !data.isEmpty()) return data;
        return null;
    }
}
