/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.auxiliary;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.world.World;

import shordinger.astralsorcery.common.data.world.WorldCacheManager;
import shordinger.astralsorcery.common.data.world.data.StorageNetworkBuffer;
import shordinger.astralsorcery.common.tile.TileStorageCore;
import shordinger.astralsorcery.common.tile.storage.StorageNetwork;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StorageNetworkHandler
 * Created by HellFirePvP
 * Date: 13.12.2017 / 20:19
 */
public class StorageNetworkHandler {

    // private static final AxisAlignedBB box = new AxisAlignedBB(-3, 0, -3, 3, 0, 3);
    private static Map<Integer, NetworkHelper> mappingHelpers = new HashMap<>();

    public static NetworkHelper getHandler(World world) {
        return mappingHelpers.computeIfAbsent(world.provider.dimensionId, id -> new NetworkHelper(world));
    }

    public static void clearHandler(World world) {
        clearHandler(world.provider.dimensionId);
    }

    public static void clearHandler(int dimId) {
        mappingHelpers.remove(dimId);
    }

    public static class NetworkHelper {

        private StorageNetworkBuffer buffer;

        private NetworkHelper(World world) {
            this.buffer = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.STORAGE_BUFFER);
        }

        @Nullable
        public StorageNetwork getNetwork(BlockPos networkMaster) {
            return buffer.getNetwork(networkMaster);
        }

        public void addCore(TileStorageCore core) {
            // TODO fusion logic
        }

        public void removeCore(TileStorageCore core) {
            // TODO division logic
        }

    }

    public static class MappingChange {

    }

}
