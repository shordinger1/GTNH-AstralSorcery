/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import net.minecraft.world.World;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.gameevent.TickEvent;
import shordinger.astralsorcery.common.auxiliary.tick.ITickHandler;
import shordinger.astralsorcery.migration.block.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ParticleEffectWatcher
 * Created by HellFirePvP
 * Date: 16.11.2018 / 17:56
 */
public class ParticleEffectWatcher implements ITickHandler {

    public static final ParticleEffectWatcher INSTANCE = new ParticleEffectWatcher();

    private final Map<Integer, List<BlockPos>> worldWatch = Maps.newHashMap();

    private ParticleEffectWatcher() {
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        worldWatch.getOrDefault(((World) context[0]).provider.dimensionId, Lists.newArrayList())
            .clear();
    }

    public boolean mayFire(World world, BlockPos pos) {
        int dimId = world.provider.dimensionId;
        worldWatch.putIfAbsent(dimId, Lists.newArrayList());
        List<BlockPos> worldPos = worldWatch.get(dimId);
        if (worldPos.contains(pos)) return false;
        worldPos.add(pos);
        return true;
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
        return "ParticleEffect Limiter";
    }

}
