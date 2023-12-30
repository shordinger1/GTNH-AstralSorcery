/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.structure.change;

import java.util.List;

import net.minecraft.world.World;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import shordinger.astralsorcery.common.data.world.WorldCacheManager;
import shordinger.astralsorcery.common.data.world.data.StructureMatchingBuffer;
import shordinger.astralsorcery.common.event.BlockModifyEvent;
import shordinger.astralsorcery.common.structure.BlockStructureObserver;
import shordinger.astralsorcery.common.util.log.LogCategory;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.ChunkPos;
import shordinger.astralsorcery.migration.block.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureIntegrityObserver
 * Created by HellFirePvP
 * Date: 02.12.2018 / 11:45
 */
public class StructureIntegrityObserver {

    public static final StructureIntegrityObserver INSTANCE = new StructureIntegrityObserver();

    private StructureIntegrityObserver() {
    }

    @SubscribeEvent
    public void onChange(BlockModifyEvent event) {
        World world = event.world;
        if (world.isRemote || !event.getChunk().isTerrainPopulated) {
            return;
        }

        StructureMatchingBuffer buf = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.STRUCTURE_MATCH);
        ChunkPos ch = event.getChunk()
            .getPos();
        BlockPos pos = event.getPos();
        IBlockState oldS = event.getOldState();
        IBlockState newS = event.getNewState();

        List<ChangeSubscriber<?>> subscribers = buf.getSubscribers(ch);
        for (ChangeSubscriber<?> subscriber : subscribers) {
            if (subscriber.observes(pos)) {
                LogCategory.STRUCTURE_MATCH.info(() -> "Adding change at " + pos + " for " + subscriber.getRequester());
                subscriber.addChange(pos, oldS, newS);
                buf.markDirty();
            }
        }

        if (oldS.getBlock() instanceof BlockStructureObserver) {
            LogCategory.STRUCTURE_MATCH.info(() -> "Testing removal for subscriber at " + pos);
            if (((BlockStructureObserver) oldS.getBlock()).removeWithNewState(world, pos, oldS, newS)) {
                LogCategory.STRUCTURE_MATCH.info(() -> "Removing subscriber at " + pos);
                buf.removeSubscriber(pos);
            }
        }
    }

}
