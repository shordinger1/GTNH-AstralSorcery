/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.structure;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.wrapper.net.minecraft.util.math.AxisAlignedBB;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.ChunkPos;
import shordinger.wrapper.net.minecraft.util.math.Vec3i;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ObservableArea
 * Created by HellFirePvP
 * Date: 02.12.2018 / 10:48
 */
public interface ObservableArea {

    public Collection<ChunkPos> getAffectedChunks(Vec3i offset);

    public boolean observes(BlockPos pos);

    default Collection<ChunkPos> calculateAffectedChunks(AxisAlignedBB box, Vec3i offset) {
        return calculateAffectedChunks(
            Vector3.getMin(box)
                .toBlockPos()
                .add(offset),
            Vector3.getMax(box)
                .toBlockPos()
                .add(offset));
    }

    default Collection<ChunkPos> calculateAffectedChunks(Vec3i min, Vec3i max) {
        List<ChunkPos> affected = Lists.newArrayList();
        int maxX = max.getX() >> 4;
        int maxZ = max.getZ() >> 4;
        for (int chX = min.getX() >> 4; chX <= maxX; chX++) {
            for (int chZ = min.getZ() >> 4; chZ <= maxZ; chZ++) {
                affected.add(new ChunkPos(chX, chZ));
            }
        }
        return affected;
    }

}
