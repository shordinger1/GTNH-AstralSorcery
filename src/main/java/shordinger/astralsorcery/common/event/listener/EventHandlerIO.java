/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.event.listener;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.render.tile.TESRTranslucentBlock;
import shordinger.astralsorcery.common.auxiliary.StorageNetworkHandler;
import shordinger.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import shordinger.astralsorcery.common.data.world.WorldCacheManager;
import shordinger.astralsorcery.common.tile.TileOreGenerator;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.ChunkPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.event.world.ChunkEvent;
import shordinger.wrapper.net.minecraftforge.event.world.WorldEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerIO
 * Created by HellFirePvP
 * Date: 01.08.2017 / 18:45
 */
public class EventHandlerIO {

    public static List<TileOreGenerator> generatorQueue = Lists.newLinkedList();

    @SubscribeEvent
    public void onUnload(WorldEvent.Unload event) {
        World w = event.getWorld();
        ConstellationSkyHandler.getInstance()
            .informWorldUnload(w);
        StorageNetworkHandler.clearHandler(w);
        if (w.isRemote) {
            clientUnload();
        }
    }

    @SideOnly(Side.CLIENT)
    private void clientUnload() {
        AstralSorcery.proxy.scheduleClientside(TESRTranslucentBlock::cleanUp);
    }

    @SubscribeEvent
    public void onSave(WorldEvent.Save event) {
        WorldCacheManager.getInstance()
            .doSave(event.getWorld());
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (!event.getWorld().isRemote) {
            Iterator<TileOreGenerator> iterator = generatorQueue.iterator();
            while (iterator.hasNext()) {
                TileOreGenerator gen = iterator.next();
                BlockPos at = gen.getPos();
                if (event.getChunk()
                    .getPos()
                    .equals(new ChunkPos(at))) {
                    event.getChunk()
                        .getTileEntityMap()
                        .put(gen.getPos(), gen);
                    iterator.remove();
                }
            }
        }
    }

}
