/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.data.world.data;

import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import shordinger.astralsorcery.common.data.world.CachedWorldData;
import shordinger.astralsorcery.common.data.world.WorldCacheManager;
import shordinger.astralsorcery.common.tile.storage.StorageNetwork;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.ChunkPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StorageNetworkBuffer
 * Created by HellFirePvP
 * Date: 13.12.2017 / 21:52
 */
public class StorageNetworkBuffer extends CachedWorldData {

    private static final Random rand = new Random();

    private final Map<BlockPos, StorageNetwork> rawNetworks = Maps.newHashMap();
    private final Map<ChunkPos, List<StorageNetwork>> availableNetworks = Maps.newHashMap();

    public StorageNetworkBuffer() {
        super(WorldCacheManager.SaveKey.STORAGE_BUFFER);
    }

    @Override
    public void updateTick(World world) {
    }

    @Nullable
    public StorageNetwork getNetwork(BlockPos masterPos) {
        return this.rawNetworks.get(masterPos);
    }

    public void rebuildAccessContext() {
        this.availableNetworks.clear();

        for (StorageNetwork network : this.rawNetworks.values()) {
            for (StorageNetwork.CoreArea core : network.getCores()) {
                AxisAlignedBB box = core.getRealBox();
                ChunkPos from = Vector3.getMin(box)
                    .toChunkPos();
                ChunkPos to = Vector3.getMax(box)
                    .toChunkPos();

                for (int chX = from.x; chX <= to.x; chX++) {
                    for (int chZ = from.z; chZ <= to.z; chZ++) {
                        this.availableNetworks.computeIfAbsent(new ChunkPos(chX, chZ), pos -> Lists.newArrayList())
                            .add(network);
                    }
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.rawNetworks.clear();

        NBTTagList networks = compound.getTagList("networks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < networks.tagCount(); i++) {
            NBTTagCompound tag = networks.getCompoundTagAt(i);
            StorageNetwork net = new StorageNetwork();
            net.readFromNBT(tag);
            if (net.getCores()
                .isEmpty()) {
                continue; // No network..
            }
            StorageNetwork.CoreArea master = net.getMaster();
            if (master == null) {
                master = MiscUtils.getRandomEntry(net.getCores(), rand);
            }
            if (master != null) {
                this.rawNetworks.put(master.getPos(), net);
            }
        }

        this.rebuildAccessContext();
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTTagList networks = new NBTTagList();
        for (StorageNetwork network : this.rawNetworks.values()) {
            NBTTagCompound tag = new NBTTagCompound();
            network.writeToNBT(tag);
            networks.appendTag(tag);
        }
        compound.setTag("networks", networks);
    }

}
