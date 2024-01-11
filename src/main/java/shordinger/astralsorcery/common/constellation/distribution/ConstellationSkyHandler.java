/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.distribution;

import shordinger.astralsorcery.common.auxiliary.tick.ITickHandler;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.client.PktRequestSeed;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.fml.common.gameevent.TickEvent;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationSkyHandler
 * Created by HellFirePvP
 * Date: 16.11.2016 / 20:47
 */
public class ConstellationSkyHandler implements ITickHandler {

    private static final ConstellationSkyHandler instance = new ConstellationSkyHandler();
    private static int activeSession = 0;

    private Map<Integer, Long> cacheSeedLookup = new HashMap<>();

    private Map<Integer, WorldSkyHandler> worldHandlersServer  = new HashMap<>();
    private Map<Integer, WorldSkyHandler> worldHandlersClient  = new HashMap<>();

    private Map<Integer, Boolean> skyRevertMap = new HashMap<>();

    private ConstellationSkyHandler() {}

    public static ConstellationSkyHandler getInstance() {
        return instance;
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        if(type == TickEvent.Type.WORLD) {
            World w = (World) context[0];
            if(!w.isRemote) {
                skyRevertMap.put(w.provider.getDimension(), false);
                WorldSkyHandler handle = worldHandlersServer.get(w.provider.getDimension());
                if(handle == null) {
                    handle = new WorldSkyHandler(new Random(w.getSeed()).nextLong());
                    worldHandlersServer.put(w.provider.getDimension(), handle);
                }
                handle.tick(w);
            }
        } else {
            handleClientTick();
        }
    }

    @SideOnly(Side.CLIENT)
    private void handleClientTick() {
        World w = Minecraft.getMinecraft().world;
        if(w != null) {
            WorldSkyHandler handle = worldHandlersClient.get(w.provider.getDimension());
            if(handle == null) {
                Integer dim = w.provider.getDimension();
                long seed;
                if(cacheSeedLookup.containsKey(dim)) {
                    try {
                        seed = cacheSeedLookup.get(dim);
                    } catch (Exception exc) { //lulwut
                        cacheSeedLookup.remove(dim);
                        PktRequestSeed req = new PktRequestSeed(activeSession, dim);
                        PacketChannel.CHANNEL.sendToServer(req);
                        return;
                    }
                } else {
                    PktRequestSeed req = new PktRequestSeed(activeSession, dim);
                    PacketChannel.CHANNEL.sendToServer(req);
                    return;
                }
                handle = new WorldSkyHandler(seed);
                worldHandlersClient.put(dim, handle);
            }
            handle.tick(w);
        }
    }

    public void updateSeedCache(int dimId, int session, long seed) {
        if(activeSession == session) {
            cacheSeedLookup.put(dimId, seed);
        }
    }

    @SideOnly(Side.CLIENT)
    public Optional<Long> getSeedIfPresent(World world) {
        if(world == null) return Optional.empty();
        return getSeedIfPresent(world.provider.getDimension());
    }

    @SideOnly(Side.CLIENT)
    public Optional<Long> getSeedIfPresent(int dim) {
        if(!cacheSeedLookup.containsKey(dim)) {
            PktRequestSeed req = new PktRequestSeed(activeSession, dim);
            PacketChannel.CHANNEL.sendToServer(req);
            return Optional.empty();
        }
        return Optional.of(cacheSeedLookup.get(dim));
    }

    //Convenience method

    public float getCurrentDaytimeDistribution(World world) {
        int dLength = Config.dayLength;
        float dayPart = ((world.getWorldTime() % dLength) + dLength) % dLength;
        if(dayPart < (dLength / 2F)) return 0F;
        float part = dLength / 7F;
        if(dayPart < ((dLength / 2F) + part)) return ((dayPart - ((dLength / 2F) + part)) / part) + 1F;
        if(dayPart > (dLength - part)) return 1F - (dayPart - (dLength - part)) / part;
        return 1F;
    }
    public boolean isNight(World world) {
        return getCurrentDaytimeDistribution(world) >= 0.6;
    }

    public boolean isDay(World world) {
        return getCurrentDaytimeDistribution(world) <= 0.4;
    }

    //For effect purposes to determine how long those events are/last
    public static int getSolarEclipseHalfDuration() {
        return Config.dayLength / 10;
    }

    public static int getLunarEclipseHalfDuration() {
        return Config.dayLength / 10;
    }

    @Nullable
    public WorldSkyHandler getWorldHandler(World world) {
        Map<Integer, WorldSkyHandler> handlerMap;
        if(world.isRemote) {
            handlerMap = worldHandlersClient;
        } else {
            handlerMap = worldHandlersServer;
        }
        return handlerMap.get(world.provider.getDimension());
    }

    public void revertWorldTimeTick(World world) {
        int dimId = world.provider.getDimension();
        Boolean state = skyRevertMap.get(dimId);
        if(!world.isRemote && state != null && !state) {
            skyRevertMap.put(dimId, true);
            world.setWorldTime(world.getWorldTime() - 1);
        }
    }

    public void clientClearCache() {
        activeSession++;
        cacheSeedLookup.clear();
        worldHandlersClient.clear();
    }

    public void informWorldUnload(World world) {
        worldHandlersServer.remove(world.provider.getDimension());
        worldHandlersClient.remove(world.provider.getDimension());
        cacheSeedLookup    .remove(world.provider.getDimension());
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.WORLD, TickEvent.Type.CLIENT);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "ConstellationSkyhandler";
    }

}
