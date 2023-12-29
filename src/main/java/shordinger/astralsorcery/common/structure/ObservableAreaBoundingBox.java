/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.structure;

import java.util.Collection;

import net.minecraft.util.AxisAlignedBB;

import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.ChunkPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ObservableAreaBoundingBox
 * Created by HellFirePvP
 * Date: 02.12.2018 / 13:20
 */
public class ObservableAreaBoundingBox implements ObservableArea {

    private final AxisAlignedBB boundingBox;

    public ObservableAreaBoundingBox(BlockPos min, BlockPos max) {
        this(new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ()));
    }

    public ObservableAreaBoundingBox(AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox.grow(0.01F);
    }

    @Override
    public Collection<ChunkPos> getAffectedChunks(BlockPos offset) {
        return calculateAffectedChunks(this.boundingBox, offset);
    }

    @Override
    public boolean observes(BlockPos pos) {
        return boundingBox.contains(new BlockPos(pos.getX(), pos.getY(), pos.getZ()));
    }

}
