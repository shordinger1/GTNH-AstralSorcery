/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.starlight.network;

import java.util.*;

import net.minecraft.world.World;

import cpw.mods.fml.common.gameevent.TickEvent;
import shordinger.astralsorcery.common.auxiliary.tick.ITickHandler;
import shordinger.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StarlightUpdateHandler
 * Created by HellFirePvP
 * Date: 01.10.2016 / 01:41
 */
public class StarlightUpdateHandler implements ITickHandler {

    private static final StarlightUpdateHandler instance = new StarlightUpdateHandler();
    private static final Map<Integer, List<IPrismTransmissionNode>> updateRequired = new HashMap<>();
    private static final Object accessLock = new Object();

    private StarlightUpdateHandler() {
    }

    public static StarlightUpdateHandler getInstance() {
        return instance;
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        World world = (World) context[0];
        if (world.isRemote) return;

        List<IPrismTransmissionNode> nodes = getNodes(world);
        synchronized (accessLock) {
            for (IPrismTransmissionNode node : nodes) {
                node.update(world);
            }
        }
    }

    private List<IPrismTransmissionNode> getNodes(World world) {
        int dimId = world.provider.dimensionId;
        List<IPrismTransmissionNode> nodes = updateRequired.computeIfAbsent(dimId, k -> new LinkedList<>());
        return nodes;
    }

    public void removeNode(World world, IPrismTransmissionNode node) {
        synchronized (accessLock) {
            getNodes(world).remove(node);
        }
    }

    public void addNode(World world, IPrismTransmissionNode node) {
        synchronized (accessLock) {
            getNodes(world).add(node);
        }
    }

    public void informWorldLoad(World world) {
        synchronized (accessLock) {
            updateRequired.remove(world.provider.dimensionId);
        }
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
        return "Starlight Update Handler";
    }

}
