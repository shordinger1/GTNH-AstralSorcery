/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.event.listener;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.render.tile.TESRTranslucentBlock;
import shordinger.astralsorcery.common.auxiliary.StorageNetworkHandler;
import shordinger.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import shordinger.astralsorcery.common.data.world.WorldCacheManager;
import shordinger.astralsorcery.common.tile.TileOreGenerator;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.ChunkPos;

import java.util.Iterator;
import java.util.List;

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
        World w = event.world;
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
            .doSave(event.world);
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (!event.world.isRemote) {
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
