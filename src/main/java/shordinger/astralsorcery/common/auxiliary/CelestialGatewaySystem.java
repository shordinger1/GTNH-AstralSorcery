/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.auxiliary;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.data.world.WorldCacheManager;
import shordinger.astralsorcery.common.data.world.data.GatewayCache;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.server.PktUpdateGateways;
import shordinger.astralsorcery.common.util.FileStorageUtil;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.nbt.CompressedStreamTools;
import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.nbt.NBTTagInt;
import shordinger.wrapper.net.minecraft.nbt.NBTTagList;
import shordinger.wrapper.net.minecraft.server.MinecraftServer;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.WorldServer;
import shordinger.wrapper.net.minecraftforge.common.DimensionManager;
import shordinger.wrapper.net.minecraftforge.common.ForgeChunkManager;
import shordinger.wrapper.net.minecraftforge.common.util.Constants;
import shordinger.wrapper.net.minecraftforge.event.world.WorldEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.FMLCommonHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CelestialGatewaySystem
 * Created by HellFirePvP
 * Date: 16.04.2017 / 18:50
 */
public class CelestialGatewaySystem {

    private boolean startup = false;

    public static CelestialGatewaySystem instance = new CelestialGatewaySystem();
    private Map<Integer, List<GatewayCache.GatewayNode>> serverCache = new HashMap<>();
    private Map<Integer, List<GatewayCache.GatewayNode>> clientCache = new HashMap<>();

    private CelestialGatewaySystem() {}

    public GatewayWorldFilter getFilter() {
        File f = FileStorageUtil.getGeneralSubDirectory("gatewayFilter");
        File worldFilter = new File(f, "worldFilter.dat");
        if (!worldFilter.exists()) {
            try {
                worldFilter.createNewFile();
            } catch (IOException exc) {
                throw new IllegalStateException(
                    "Couldn't create plain world filter file! Are we missing file permissions?",
                    exc);
            }
        }
        return new GatewayWorldFilter(worldFilter);
    }

    public void onServerStart() {
        startup = true;
        Integer[] worlds = DimensionManager.getStaticDimensionIDs(); // Should be loaded during startup = we should grab
        // those.
        MinecraftServer server = FMLCommonHandler.instance()
            .getMinecraftServerInstance();
        List<Integer> involved = getFilter().getInvolvedWorlds();
        for (Integer id : worlds) {
            if (id == null || !involved.contains(id)) continue;
            WorldServer world = server.getWorld(id);
            loadWorldCache(world);

            if (world.getChunkProvider()
                .getLoadedChunkCount() <= 0
                && ForgeChunkManager.getPersistentChunksFor(world)
                .size() == 0
                && !world.provider.getDimensionType()
                .shouldLoadSpawn()) {
                DimensionManager.unloadWorld(world.provider.getDimension());
            }
        }
        startup = false;
        syncToAll();
    }

    @SubscribeEvent
    public void onWorldInit(WorldEvent.Load event) {
        if (startup) return; // We're already loading up there.

        World world = event.getWorld();
        if (world.isRemote) return;

        loadWorldCache(world);
        syncToAll();
    }

    public void syncTo(EntityPlayer pl) {
        PktUpdateGateways pkt = new PktUpdateGateways(serverCache);
        PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) pl);
    }

    public void syncToAll() {
        PktUpdateGateways pkt = new PktUpdateGateways(serverCache);
        PacketChannel.CHANNEL.sendToAll(pkt);
    }

    public List<GatewayCache.GatewayNode> getGatewaysForWorld(World world, Side side) {
        return (side == Side.SERVER ? serverCache : clientCache).get(world.provider.getDimension());
    }

    public Map<Integer, List<GatewayCache.GatewayNode>> getGatewayCache(Side side) {
        return Collections.unmodifiableMap(side == Side.SERVER ? serverCache : clientCache);
    }

    public void addPosition(World world, GatewayCache.GatewayNode pos) {
        if (world.isRemote) return;

        Integer dim = world.provider.getDimension();
        if (!serverCache.containsKey(dim)) {
            forceLoad(dim);
        }
        if (!serverCache.containsKey(dim)) {
            AstralSorcery.log
                .info("Couldn't add position for world " + dim + "! - Force loading the world resulted in... nothing.");
            return;
        }

        getFilter().appendAndSave(dim);
        List<GatewayCache.GatewayNode> cache = serverCache.get(dim);
        if (!cache.contains(pos)) {
            cache.add(pos);
            syncToAll();
        }
    }

    public void removePosition(World world, BlockPos pos) {
        if (world.isRemote) return;

        Integer dim = world.provider.getDimension();
        if (!serverCache.containsKey(dim)) {
            return;
        }
        if (serverCache.get(dim)
            .remove(pos)) {
            if (serverCache.get(dim)
                .isEmpty()) {
                getFilter().removeAndSave(dim);
            }
            syncToAll();
        }
    }

    private void forceLoad(int dim) {
        WorldServer serv = DimensionManager.getWorld(dim);
        if (serv == null) {
            DimensionManager.initDimension(dim);
        }
    }

    public void updateClientCache(Map<Integer, List<GatewayCache.GatewayNode>> positions) {
        this.clientCache = positions;
    }

    private void loadWorldCache(World world) {
        GatewayCache cache = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.GATEWAY_DATA);
        serverCache.put(world.provider.getDimension(), cache.getGatewayPositions());
    }

    public static class GatewayWorldFilter {

        private final File gatewayCacheFile;
        private List<Integer> cache = null;

        private GatewayWorldFilter(File gatewayCacheFile) {
            this.gatewayCacheFile = gatewayCacheFile;
        }

        public List<Integer> getInvolvedWorlds() {
            if (cache == null) {
                loadCache();
            }
            return cache;
        }

        private void loadCache() {
            try {
                NBTTagCompound tag = CompressedStreamTools.read(this.gatewayCacheFile);
                NBTTagList list = tag.getTagList("list", Constants.NBT.TAG_INT);
                cache = Lists.newArrayList();
                for (int i = 0; i < list.tagCount(); i++) {
                    Integer id = list.getIntAt(i);
                    if (!cache.contains(id)) {
                        cache.add(id);
                    }
                }
            } catch (IOException ignored) {
                cache = Lists.newArrayList();
            }
        }

        private void appendAndSave(Integer id) {
            if (cache == null) {
                loadCache();
            }
            if (!cache.contains(id)) {
                cache.add(id);
                try {
                    NBTTagList list = new NBTTagList();
                    for (int dimId : cache) {
                        list.appendTag(new NBTTagInt(dimId));
                    }
                    NBTTagCompound cmp = new NBTTagCompound();
                    cmp.setTag("list", list);
                    CompressedStreamTools.write(cmp, this.gatewayCacheFile);
                } catch (IOException ignored) {
                }
            }
        }

        private void removeAndSave(Integer dim) {
            if (cache == null) {
                loadCache();
            }
            if (cache.contains(dim)) {
                cache.remove(dim);
                try {
                    NBTTagList list = new NBTTagList();
                    for (int dimId : cache) {
                        list.appendTag(new NBTTagInt(dimId));
                    }
                    NBTTagCompound cmp = new NBTTagCompound();
                    cmp.setTag("list", list);
                    CompressedStreamTools.write(cmp, this.gatewayCacheFile);
                } catch (IOException ignored) {
                }
            }
        }

    }

}
