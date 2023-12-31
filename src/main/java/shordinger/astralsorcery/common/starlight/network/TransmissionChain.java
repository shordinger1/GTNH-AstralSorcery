/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.starlight.network;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.block.network.IBlockStarlightRecipient;
import shordinger.astralsorcery.common.data.DataLightBlockEndpoints;
import shordinger.astralsorcery.common.data.DataLightConnections;
import shordinger.astralsorcery.common.data.SyncDataHolder;
import shordinger.astralsorcery.common.item.crystal.CrystalProperties;
import shordinger.astralsorcery.common.starlight.IIndependentStarlightSource;
import shordinger.astralsorcery.common.starlight.WorldNetworkHandler;
import shordinger.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import shordinger.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import shordinger.astralsorcery.common.starlight.transmission.NodeConnection;
import shordinger.astralsorcery.common.util.CrystalCalculations;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.ChunkPos;
import shordinger.astralsorcery.migration.block.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TransmissionChain
 * Created by HellFirePvP
 * Date: 05.08.2016 / 11:18
 */
public class TransmissionChain {

    private final List<ChunkPos> involvedChunks = new LinkedList<>();
    private final List<LightConnection> foundConnections = new LinkedList<>();
    private final Map<BlockPos, Float> remainMultiplierMap = new HashMap<>();

    private final List<BlockPos> uncheckedEndpointsBlock = new LinkedList<>(); // Might be IBlockSLRecipient or just a
    // normal
    // block.
    private final List<BlockPos> resolvedNormalBlockPositions = new LinkedList<>();
    private final List<ITransmissionReceiver> endpointsNodes = new LinkedList<>(); // Safe to assume those are endpoints
    private final List<IPrismTransmissionNode> transmissionUpdateList = new LinkedList<>();

    private final WorldNetworkHandler handler;
    private final IPrismTransmissionNode sourceNode;

    private TransmissionChain(WorldNetworkHandler netHandler, IPrismTransmissionNode sourceNode) {
        this.handler = netHandler;
        this.sourceNode = sourceNode;
    }

    public static void threadedBuildTransmissionChain(World world, TransmissionWorldHandler handle,
                                                      IIndependentStarlightSource source, WorldNetworkHandler netHandler, BlockPos sourcePos) {
        Thread tr = new Thread(() -> {
            TransmissionChain chain = buildFromSource(netHandler, sourcePos);
            handle.threadTransmissionChainCallback(world, chain, source, netHandler, sourcePos);
            AstralSorcery.proxy.scheduleDelayed(() -> {
                DataLightConnections connections = SyncDataHolder.getDataServer(SyncDataHolder.DATA_LIGHT_CONNECTIONS);
                connections.updateNewConnectionsThreaded(
                    netHandler.getWorld().provider.dimensionId,
                    chain.getFoundConnections());
                DataLightBlockEndpoints endpoints = SyncDataHolder
                    .getDataServer(SyncDataHolder.DATA_LIGHT_BLOCK_ENDPOINTS);
                endpoints
                    .updateNewEndpoints(netHandler.getWorld().provider.dimensionId, chain.resolvedNormalBlockPositions);
            });
        });
        tr.setName("TrChainCalculationThread");
        tr.start();
    }

    private static TransmissionChain buildFromSource(WorldNetworkHandler netHandler, BlockPos at) {
        TransmissionChain chain = new TransmissionChain(netHandler, null);

        IPrismTransmissionNode node = netHandler.getTransmissionNode(at);
        if (node != null) { // Well otherwise we don't need to do anything huh...
            chain = new TransmissionChain(netHandler, node);
            chain.recBuildChain(node, 1F, new LinkedList<>());
        }

        chain.calculateInvolvedChunks();
        chain.resolveLoadedEndpoints(netHandler.getWorld());
        return chain;
    }

    private void resolveLoadedEndpoints(World world) {
        for (BlockPos pos : uncheckedEndpointsBlock) {
            if (MiscUtils.isChunkLoaded(world, new ChunkPos(pos))) {
                IBlockState state = WorldHelper.getBlockState(world, pos);
                Block b = state.getBlock();
                if (b instanceof IBlockStarlightRecipient) continue;
                if (!resolvedNormalBlockPositions.contains(pos)) {
                    resolvedNormalBlockPositions.add(pos);
                }
                // iterator.remove();
            }
        }
    }

    protected void updatePosAsResolved(World world, BlockPos pos) {
        if (uncheckedEndpointsBlock.contains(pos) && !resolvedNormalBlockPositions.contains(pos)) {
            resolvedNormalBlockPositions.add(pos);
            DataLightBlockEndpoints endpoints = SyncDataHolder.getDataServer(SyncDataHolder.DATA_LIGHT_BLOCK_ENDPOINTS);
            endpoints.updateNewEndpoint(world.provider.dimensionId, pos);
        }
    }

    private void recBuildChain(IPrismTransmissionNode node, float lossMultiplier, LinkedList<BlockPos> prevPath) {
        if (lossMultiplier <= 0.001F) return; // No. we don't transfer a part less than 0.1% of the starlight.

        CrystalProperties properties = node.getTransmissionProperties();
        float lossPerc;
        if (properties != null) {
            lossPerc = CrystalCalculations.getThroughputMultiplier(properties);
        } else {
            lossPerc = 0;
        }
        lossPerc *= node.getAdditionalTransmissionLossMultiplier();
        List<NodeConnection<IPrismTransmissionNode>> next = node.queryNext(handler);
        float nextLoss = (lossMultiplier * lossPerc) / ((float) next.size());
        prevPath.push(node.getLocationPos());

        if (node.needsTransmissionUpdate() && !transmissionUpdateList.contains(node)) {
            transmissionUpdateList.add(node);
        }

        for (NodeConnection<IPrismTransmissionNode> nextNode : next) {
            IPrismTransmissionNode trNode = nextNode.node();
            if (nextNode.canConnect()) {
                BlockPos nextPos = nextNode.to();
                addIfNonExistentConnection(node.getLocationPos(), nextPos);
                if (!prevPath.contains(nextPos)) { // Saves us from cycles. cyclic starlight transmission to a cyclic
                    // node means 100% loss.

                    // This never exceeds 1F
                    remainMultiplierMap.merge(nextPos, nextLoss, Float::sum);

                    if (trNode != null) {
                        if (trNode instanceof ITransmissionReceiver) { // Tile endpoint
                            if (!this.endpointsNodes.contains(trNode))
                                this.endpointsNodes.add((ITransmissionReceiver) trNode);
                        } else {
                            recBuildChain(trNode, nextLoss, prevPath);
                        }
                    } else { // BlockPos endpoint - Check for IBlockStarlightRecipient is missing here, bc chunk
                        // is/might be unloaded.
                        if (!this.uncheckedEndpointsBlock.contains(nextPos)) this.uncheckedEndpointsBlock.add(nextPos);
                    }
                }
            }
        }

        prevPath.pop();
    }

    // After calculating everything...
    private void calculateInvolvedChunks() {
        for (BlockPos nodePos : remainMultiplierMap.keySet()) {
            ChunkPos ch = new ChunkPos(nodePos);
            if (!involvedChunks.contains(ch)) involvedChunks.add(ch);
        }
    }

    public List<BlockPos> getResolvedNormalBlockPositions() {
        return resolvedNormalBlockPositions;
    }

    public IPrismTransmissionNode getSourceNode() {
        return sourceNode;
    }

    // For rendering purposes.
    private void addIfNonExistentConnection(BlockPos start, BlockPos end) {
        LightConnection newCon = new LightConnection(start, end);
        if (!foundConnections.contains(newCon)) foundConnections.add(newCon);
    }

    public List<IPrismTransmissionNode> getTransmissionUpdateList() {
        return transmissionUpdateList;
    }

    public List<ChunkPos> getInvolvedChunks() {
        return involvedChunks;
    }

    public Map<BlockPos, Float> getLossMultipliers() {
        return remainMultiplierMap;
    }

    public List<LightConnection> getFoundConnections() {
        return foundConnections;
    }

    public List<ITransmissionReceiver> getEndpointsNodes() {
        return endpointsNodes;
    }

    public List<BlockPos> getUncheckedEndpointsBlock() {
        return uncheckedEndpointsBlock;
    }

    public static class LightConnection {

        private final BlockPos start, end;

        public LightConnection(BlockPos start, BlockPos end) {
            this.start = start;
            this.end = end;
        }

        public BlockPos getStart() {
            return start;
        }

        public BlockPos getEnd() {
            return end;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LightConnection that = (LightConnection) o;
            return Objects.equals(end, that.end) && Objects.equals(start, that.start);

        }

        @Override
        public int hashCode() {
            int result = start != null ? start.hashCode() : 0;
            result = 31 * result + (end != null ? end.hashCode() : 0);
            return result;
        }
    }

}
