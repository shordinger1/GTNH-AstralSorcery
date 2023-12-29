/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import shordinger.astralsorcery.common.util.data.WorldBlockPos;
import shordinger.astralsorcery.common.util.log.LogCategory;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TreeCaptureHelper
 * Created by HellFirePvP
 * Date: 11.11.2016 / 19:58
 */
public class TreeCaptureHelper {

    public static TreeCaptureHelper eventInstance = new TreeCaptureHelper();

    public static List<WorldBlockPos> oneTimeCatches = Lists.newLinkedList();

    private static final List<WeakReference<TreeWatcher>> watchers = Lists.newLinkedList();
    private static final Map<WeakReference<TreeWatcher>, List<WorldBlockPos>> cachedEntries = Maps.newHashMap();

    private TreeCaptureHelper() {
    }

    @SubscribeEvent
    public void onTreeGrowth(SaplingGrowTreeEvent event) {
        LogCategory.TREE_BEACON.info(
            () -> "Captured tree growth at " + new BlockPos(event.x, event.y, event.z)
                + " in dim "
                + event.world.provider.dimensionId);
        WorldBlockPos pos = new WorldBlockPos(event.world, new BlockPos(event.x, event.y, event.z));
        if (oneTimeCatches.contains(pos)) {
            LogCategory.TREE_BEACON.info(() -> "Expected growth at " + pos + " - skipping!");
            oneTimeCatches.remove(pos);
            return;
        }

        if (watchers.isEmpty()) return;
        Iterator<WeakReference<TreeWatcher>> iterator = watchers.iterator();
        while (iterator.hasNext()) {
            WeakReference<TreeWatcher> watch = iterator.next();
            TreeWatcher watcher = watch.get();
            if (watcher == null) {
                LogCategory.TREE_BEACON.info(() -> "A TreeWatcher timed out (no additional information)");
                iterator.remove();
                continue;
            }
            if (watcher.watches(pos)) {
                LogCategory.TREE_BEACON.info(
                    () -> "TreeWatcher at " + watcher.center
                        + " watches "
                        + pos
                        + " - with squared radius: "
                        + watcher.watchRadiusSq
                        + " (real: "
                        + Math.sqrt(watcher.watchRadiusSq)
                        + ")");
                addWatch(watch, pos);
                event.setResult(Event.Result.DENY);
            }
        }
    }

    public static void offerWeakWatcher(TreeWatcher watcher) {
        Iterator<WeakReference<TreeWatcher>> iterator = watchers.iterator();
        while (iterator.hasNext()) {
            WeakReference<TreeWatcher> w = iterator.next();
            TreeWatcher other = w.get();
            if (other == null) {
                iterator.remove();
                continue;
            }
            if (other.equals(watcher)) return;
        }
        LogCategory.TREE_BEACON.info(() -> "New watcher offered and added at " + watcher.center);
        watchers.add(new WeakReference<>(watcher));
    }

    @Nonnull
    public static List<WorldBlockPos> getAndClearCachedEntries(@Nullable TreeWatcher watcher) {
        if (watcher == null) return Lists.newArrayList();
        if (watchers.isEmpty()) return Lists.newArrayList();
        Iterator<WeakReference<TreeWatcher>> iterator = watchers.iterator();
        while (iterator.hasNext()) {
            WeakReference<TreeWatcher> itW = iterator.next();
            TreeWatcher watch = itW.get();
            if (watch == null) {
                LogCategory.TREE_BEACON.info(() -> "A TreeWatcher timed out (no additional information)");
                iterator.remove();
                continue;
            }
            if (watcher.equals(watch)) {
                List<WorldBlockPos> pos = cachedEntries.get(itW);
                cachedEntries.remove(itW);
                LogCategory.TREE_BEACON.info(
                    () -> "Fetched " + (pos == null ? 0 : pos.size())
                        + " cached, captured positions for watcher at "
                        + watcher.center);
                return pos == null ? Lists.newArrayList() : pos;
            }
        }
        return Lists.newArrayList();
    }

    private void addWatch(WeakReference<TreeWatcher> watch, WorldBlockPos pos) {
        List<WorldBlockPos> entries = cachedEntries.computeIfAbsent(watch, k -> Lists.newLinkedList());
        entries.add(pos);

        LogCategory.TREE_BEACON.info(() -> "Captured " + pos + " - TreeWatcher in total watches " + entries.size());

        Iterator<WeakReference<TreeWatcher>> iterator = cachedEntries.keySet()
            .iterator();
        while (iterator.hasNext()) {
            WeakReference<TreeWatcher> wrT = iterator.next();
            if (wrT.get() == null) {
                LogCategory.TREE_BEACON.info(() -> "An empty TreeWatcher was removed from the entry cache");
                iterator.remove();
            }
        }
    }

    public static class TreeWatcher {

        private final int dimId;
        private final BlockPos center;
        private final double watchRadiusSq;

        public TreeWatcher(TileEntity te, double watchRadius) {
            this(te.getWorldObj(), new BlockPos(te), watchRadius);
        }

        public TreeWatcher(World world, BlockPos center, double watchRadius) {
            this(world.provider.dimensionId, center, watchRadius);
        }

        public TreeWatcher(int dimId, BlockPos center, double watchRadius) {
            this.dimId = dimId;
            this.center = center;
            this.watchRadiusSq = watchRadius * watchRadius;
        }

        public boolean watches(WorldBlockPos pos) {
            return pos.getWorld().provider.dimensionId == dimId && center.distanceSq(pos) <= watchRadiusSq;
        }
    }

}
