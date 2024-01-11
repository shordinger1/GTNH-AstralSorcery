/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.starlight.network;

import shordinger.wrapper.net.minecraft.util.math.ChunkPos;
import shordinger.wrapper.net.minecraft.world.chunk.Chunk;
import shordinger.wrapper.net.minecraftforge.event.world.ChunkEvent;
import shordinger.wrapper.net.minecraftforge.event.world.WorldEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TransmissionChunkTracker
 * Created by HellFirePvP
 * Date: 05.08.2016 / 10:08
 */
public class TransmissionChunkTracker {

    private static final TransmissionChunkTracker instance = new TransmissionChunkTracker();

    private TransmissionChunkTracker() {}

    public static TransmissionChunkTracker getInstance() {
        return instance;
    }

    @SubscribeEvent
    public void onChLoad(ChunkEvent.Load event) {
        TransmissionWorldHandler handle = StarlightTransmissionHandler.getInstance().getWorldHandler(event.getWorld());
        if(handle != null) {
            Chunk ch = event.getChunk();
            handle.informChunkLoad(new ChunkPos(ch.x, ch.z));
        }
    }

    @SubscribeEvent
    public void onChUnload(ChunkEvent.Unload event) {
        TransmissionWorldHandler handle = StarlightTransmissionHandler.getInstance().getWorldHandler(event.getWorld());
        if(handle != null) {
            Chunk ch = event.getChunk();
            handle.informChunkUnload(new ChunkPos(ch.x, ch.z));
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        //StarlightTransmissionHandler.getInstance().informWorldUnload(event.getWorld());
        //StarlightUpdateHandler.getInstance().informWorldUnload(event.getWorld());
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if(event.getWorld().isRemote) return;
        StarlightUpdateHandler.getInstance().informWorldLoad(event.getWorld());
    }

}
