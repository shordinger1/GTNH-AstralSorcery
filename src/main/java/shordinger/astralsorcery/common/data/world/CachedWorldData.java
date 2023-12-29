/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.data.world;

import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CachedWorldData
 * Created by HellFirePvP
 * Date: 02.08.2016 / 23:21
 */
public abstract class CachedWorldData implements IWorldRelatedData {

    private boolean dirty = false;
    private final WorldCacheManager.SaveKey key;

    public CachedWorldData(WorldCacheManager.SaveKey key) {
        this.key = key;
    }

    public final void markDirty() {
        this.dirty = true;
    }

    public final boolean needsSaving() {
        return this.dirty;
    }

    public final void clearDirtyFlag() {
        this.dirty = false;
    }

    public abstract void updateTick(World world);

    public final WorldCacheManager.SaveKey getSaveKey() {
        return key;
    }

    public void onLoad(World world) {
    }

    /*
     * public final <T extends CachedWorldData> T initializeAndGet(World world) {
     * String id = getSaveKey().getIdentifier();
     * CachedWorldData data = (CachedWorldData) world.getPerWorldStorage().getOrLoadData(getClass(), id);
     * if (data == null) {
     * data = constructNewData();
     * world.getPerWorldStorage().setData(id, data);
     * }
     * return (T) data;
     * }
     */

}
