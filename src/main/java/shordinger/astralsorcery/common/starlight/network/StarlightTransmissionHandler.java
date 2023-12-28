/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.starlight.network;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.world.World;

import cpw.mods.fml.common.gameevent.TickEvent;
import shordinger.astralsorcery.common.auxiliary.tick.ITickHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StarlightTransmissionHandler
 * Created by HellFirePvP
 * Date: 04.08.2016 / 23:24
 */
public class StarlightTransmissionHandler implements ITickHandler {

    private static final StarlightTransmissionHandler instance = new StarlightTransmissionHandler();
    private final Map<Integer, TransmissionWorldHandler> worldHandlers = new HashMap<>();

    private StarlightTransmissionHandler() {
    }

    public static StarlightTransmissionHandler getInstance() {
        return instance;
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        World world = (World) context[0];
        if (world.isRemote) return;

        int dimId = world.provider.dimensionId;
        TransmissionWorldHandler handle = worldHandlers.get(dimId);
        if (handle == null) {
            handle = new TransmissionWorldHandler(dimId);
            worldHandlers.put(dimId, handle);
        }
        handle.tick(world);
    }

    public void serverCleanHandlers() {
        for (int id : worldHandlers.keySet()) {
            worldHandlers.get(id)
                .clear(id);
        }
        worldHandlers.clear();
    }

    public void informWorldUnload(World world) {
        int dimId = world.provider.dimensionId;
        TransmissionWorldHandler handle = worldHandlers.get(dimId);
        if (handle != null) {
            handle.clear(world.provider.dimensionId);
        }
        this.worldHandlers.remove(dimId);
    }

    @Nullable
    public TransmissionWorldHandler getWorldHandler(World world) {
        if (world == null) return null;
        return worldHandlers.get(world.provider.dimensionId);
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.WORLD);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Starlight Transmission Handler";
    }

}
