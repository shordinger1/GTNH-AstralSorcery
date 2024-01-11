/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.structure;

import java.util.Collection;

import shordinger.wrapper.net.minecraft.util.math.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ObservableAreaBoundingBox
 * Created by HellFirePvP
 * Date: 02.12.2018 / 13:20
 */
public class ObservableAreaBoundingBox implements ObservableArea {

    private AxisAlignedBB boundingBox;

    public ObservableAreaBoundingBox(Vec3i min, Vec3i max) {
        this(new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ()));
    }

    public ObservableAreaBoundingBox(AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox.grow(0.01F);
    }

    @Override
    public Collection<ChunkPos> getAffectedChunks(Vec3i offset) {
        return calculateAffectedChunks(this.boundingBox, offset);
    }

    @Override
    public boolean observes(BlockPos pos) {
        return boundingBox.contains(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
    }

}
