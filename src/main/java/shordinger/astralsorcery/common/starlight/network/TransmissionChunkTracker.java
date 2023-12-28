/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.starlight.network;

import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import shordinger.astralsorcery.migration.ChunkPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TransmissionChunkTracker
 * Created by HellFirePvP
 * Date: 05.08.2016 / 10:08
 */
public class TransmissionChunkTracker {

    private static final TransmissionChunkTracker instance = new TransmissionChunkTracker();

    private TransmissionChunkTracker() {
    }

    public static TransmissionChunkTracker getInstance() {
        return instance;
    }

    @SubscribeEvent
    public void onChLoad(ChunkEvent.Load event) {
        TransmissionWorldHandler handle = StarlightTransmissionHandler.getInstance()
            .getWorldHandler(event.world);
        if (handle != null) {
            Chunk ch = event.getChunk();
            handle.informChunkLoad(new ChunkPos(ch.xPosition, ch.zPosition));
        }
    }

    @SubscribeEvent
    public void onChUnload(ChunkEvent.Unload event) {
        TransmissionWorldHandler handle = StarlightTransmissionHandler.getInstance()
            .getWorldHandler(event.world);
        if (handle != null) {
            Chunk ch = event.getChunk();
            handle.informChunkUnload(new ChunkPos(ch.xPosition, ch.zPosition));
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        // StarlightTransmissionHandler.getInstance().informWorldUnload(event.getWorld());
        // StarlightUpdateHandler.getInstance().informWorldUnload(event.getWorld());
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.world.isRemote) return;
        StarlightUpdateHandler.getInstance()
            .informWorldLoad(event.world);
    }

}
