/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.world.retrogen;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.world.ChunkDataEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import shordinger.astralsorcery.common.data.world.WorldCacheManager;
import shordinger.astralsorcery.common.data.world.data.ChunkVersionBuffer;
import shordinger.astralsorcery.migration.ChunkPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ChunkVersionController
 * Created by HellFirePvP
 * Date: 12.04.2017 / 21:10
 */
public class ChunkVersionController {

    private static final String AS_VERSION_KEY = "AS-ChunkGen-Version";
    public static ChunkVersionController instance = new ChunkVersionController();

    private final Map<ChunkPos, Integer> versionBuffer = new ConcurrentHashMap<>();

    private ChunkVersionController() {
    }

    @Nullable
    public Integer getGenerationVersion(ChunkPos pos) {
        return versionBuffer.get(pos);
    }

    public void setGenerationVersion(ChunkPos pos, Integer version) {
        versionBuffer.put(pos, version);
    }

    @SubscribeEvent
    public void onChDataLoad(ChunkDataEvent.Load ev) {
        ChunkPos cp = ev.getChunk()
            .getPos();
        NBTTagCompound tag = ev.getData();
        if (tag.hasKey(AS_VERSION_KEY)) {
            versionBuffer.put(cp, tag.getInteger(AS_VERSION_KEY));
        } else {
            ChunkVersionBuffer buf = WorldCacheManager
                .getOrLoadData(ev.world, WorldCacheManager.SaveKey.CHUNK_VERSIONING);
            Integer savedVersion = buf.getGenerationVersion(cp);
            // Can't grab any data...
            versionBuffer.put(cp, Objects.requireNonNullElse(savedVersion, -1));
        }
    }

    @SubscribeEvent
    public void onChDataSave(ChunkDataEvent.Save ev) {
        ChunkPos cp = ev.getChunk()
            .getPos();
        Integer buf = versionBuffer.get(cp);
        // So at least we don't have to look it up somewhere else later.
        ev.getData()
            .setInteger(AS_VERSION_KEY, Objects.requireNonNullElse(buf, -1));
    }

}
