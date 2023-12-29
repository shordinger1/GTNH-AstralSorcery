/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.data;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import shordinger.astralsorcery.common.starlight.network.TransmissionChain;
import shordinger.astralsorcery.common.util.data.Tuple;
import shordinger.astralsorcery.migration.BlockPos;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DataStarlightConnections
 * Created by HellFirePvP
 * Date: 05.08.2016 / 20:14
 */
public class DataLightConnections extends AbstractData {

    private final Object lock = new Object();

    public boolean clientReceivingData = false;
    private final Map<Integer, Map<BlockPos, List<BlockPos>>> clientPosBuffer = new ConcurrentHashMap<>();
    private final Map<Integer, Map<BlockPos, List<BlockPos>>> serverPosBuffer = new HashMap<>();

    // Boolean flag: true=addition, false=removal
    private final Map<Integer, LinkedList<Tuple<TransmissionChain.LightConnection, Boolean>>> serverChangeBuffer = new HashMap<>();

    private NBTTagCompound clientReadBuffer = new NBTTagCompound();

    public void updateNewConnectionsThreaded(int dimensionId,
                                             List<TransmissionChain.LightConnection> newlyAddedConnections) {
        Map<BlockPos, List<BlockPos>> posBufferDim = serverPosBuffer.computeIfAbsent(dimensionId, k -> new HashMap<>());
        for (TransmissionChain.LightConnection c : newlyAddedConnections) {
            BlockPos start = c.getStart();
            BlockPos end = c.getEnd();
            List<BlockPos> endpoints = posBufferDim.computeIfAbsent(start, k -> new LinkedList<>());
            if (!endpoints.contains(end)) endpoints.add(end);
        }
        notifyConnectionAdd(dimensionId, newlyAddedConnections);
        if (newlyAddedConnections.size() > 0) {
            markDirty();
        }
    }

    public void removeOldConnectionsThreaded(int dimensionId,
                                             List<TransmissionChain.LightConnection> invalidConnections) {
        Map<BlockPos, List<BlockPos>> posBufferDim = serverPosBuffer.get(dimensionId);
        if (posBufferDim != null) {
            for (TransmissionChain.LightConnection c : invalidConnections) {
                BlockPos start = c.getStart();
                List<BlockPos> ends = posBufferDim.get(start);
                if (ends == null) continue;
                ends.remove(c.getEnd());
                if (ends.isEmpty()) posBufferDim.remove(start);
            }
        }
        notifyConnectionRemoval(dimensionId, invalidConnections);
        if (invalidConnections.size() > 0) {
            markDirty();
        }
    }

    public void clearDimensionPositions(int dimId) {
        if (serverPosBuffer.remove(dimId) != null) {
            setDimClearFlag(dimId);
            markDirty();
        }
    }

    private void setDimClearFlag(int dim) {
        synchronized (lock) {
            LinkedList<Tuple<TransmissionChain.LightConnection, Boolean>> ch = serverChangeBuffer.computeIfAbsent(dim, k -> new LinkedList<>());
            ch.clear();
            ch.add(new Tuple<>(null, false)); // null, false -> clear
        }
    }

    private void notifyConnectionAdd(int dimid, List<TransmissionChain.LightConnection> added) {
        synchronized (lock) {
            LinkedList<Tuple<TransmissionChain.LightConnection, Boolean>> ch = serverChangeBuffer.computeIfAbsent(dimid, k -> new LinkedList<>());
            for (TransmissionChain.LightConnection l : added) {
                ch.add(new Tuple<>(l, true));
            }
        }
    }

    private void notifyConnectionRemoval(int dimid, List<TransmissionChain.LightConnection> removal) {
        synchronized (lock) {
            LinkedList<Tuple<TransmissionChain.LightConnection, Boolean>> ch = serverChangeBuffer.computeIfAbsent(dimid, k -> new LinkedList<>());
            for (TransmissionChain.LightConnection l : removal) {
                ch.add(new Tuple<>(l, false));
            }
        }
    }

    @Nullable
    public Map<BlockPos, List<BlockPos>> getClientConnections(int dimId) {
        return clientPosBuffer.get(dimId);
    }

    public void clientClean() {
        clientPosBuffer.clear();
    }

    @Override
    public void writeAllDataToPacket(NBTTagCompound compound) {
        for (int dimId : serverPosBuffer.keySet()) {
            Map<BlockPos, List<BlockPos>> dat = serverPosBuffer.get(dimId);
            NBTTagList dataList = new NBTTagList();
            for (BlockPos pos : dat.keySet()) {
                List<BlockPos> connections = dat.get(pos);
                if (connections == null) continue;
                for (BlockPos end : connections) {
                    NBTTagCompound cmp = new NBTTagCompound();
                    cmp.setLong("sta", pos.toLong());
                    cmp.setLong("end", end.toLong());
                    cmp.setBoolean("s", true);
                    dataList.appendTag(cmp);
                }
            }

            compound.setTag(String.valueOf(dimId), dataList);
        }
    }

    @Override
    public void writeToPacket(NBTTagCompound compound) {
        synchronized (lock) {
            for (int dimId : serverChangeBuffer.keySet()) {
                LinkedList<Tuple<TransmissionChain.LightConnection, Boolean>> changes = serverChangeBuffer.get(dimId);
                if (!changes.isEmpty()) {
                    NBTTagList list = new NBTTagList();
                    for (Tuple<TransmissionChain.LightConnection, Boolean> tpl : changes) {
                        if (tpl.key == null) {
                            list = new NBTTagList();
                            NBTTagCompound cm = new NBTTagCompound();
                            cm.setBoolean("clear", true);
                            list.appendTag(cm);
                            break;
                        }

                        NBTTagCompound cmp = new NBTTagCompound();
                        cmp.setLong(
                            "sta",
                            tpl.key.getStart()
                                .toLong());
                        cmp.setLong(
                            "end",
                            tpl.key.getEnd()
                                .toLong());
                        cmp.setBoolean("s", tpl.value);
                        list.appendTag(cmp);
                    }

                    compound.setTag(String.valueOf(dimId), list);
                }
            }
            serverChangeBuffer.clear();
        }
    }

    @Override
    public void readRawFromPacket(NBTTagCompound compound) {
        this.clientReadBuffer = compound;
    }

    @Override
    public void handleIncomingData(AbstractData serverData) {
        if (!(serverData instanceof DataLightConnections)) return;

        clientReceivingData = true;
        try {
            for (Object dimStr : ((DataLightConnections) serverData).clientReadBuffer.func_150296_c()) {
                int dimId = Integer.parseInt((String) dimStr);
                NBTTagList list = ((DataLightConnections) serverData).clientReadBuffer.getTagList((String) dimStr, 10);
                Map<BlockPos, List<BlockPos>> connectionMap = clientPosBuffer.get(dimId);
                if (connectionMap == null) {
                    connectionMap = new ConcurrentHashMap<>();
                    clientPosBuffer.put(dimId, connectionMap);
                }
                for (int i = 0; i < list.tagCount(); i++) {
                    NBTTagCompound connection = list.getCompoundTagAt(i);
                    if (connection.hasKey("clear")) {
                        clientPosBuffer.remove(dimId);
                        break;
                    }

                    BlockPos start = BlockPos.fromLong(connection.getLong("sta"));
                    BlockPos end = BlockPos.fromLong(connection.getLong("end"));
                    boolean set = connection.getBoolean("s");
                    List<BlockPos> to = connectionMap.get(start);
                    if (set) {
                        if (to == null) {
                            to = new LinkedList<>();
                            connectionMap.put(start, to);
                        }
                        if (!to.contains(end)) {
                            to.add(end);
                        }
                    } else {
                        if (to != null) {
                            to.remove(end);
                            if (to.isEmpty()) {
                                connectionMap.remove(start);
                            }
                        }
                    }
                }
                if (connectionMap.isEmpty()) {
                    clientPosBuffer.remove(dimId);
                }
            }
        } finally {
            clientReceivingData = false;
        }
    }

    public static class Provider extends ProviderAutoAllocate<DataLightConnections> {

        public Provider(String key) {
            super(key);
        }

        @Override
        public DataLightConnections provideNewInstance(Side side) {
            return new DataLightConnections();
        }

    }

}
