/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util.data;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.util.EnumFacing;

import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DirectionalLayerBlockDiscoverer
 * Created by HellFirePvP
 * Date: 01.11.2016 / 15:53
 */
public class DirectionalLayerBlockDiscoverer {

    private final BlockPos start;
    private final int rad, stepWidth;

    public DirectionalLayerBlockDiscoverer(BlockPos start, int discoverRadius, int stepWidth) {
        this.start = start;
        this.rad = discoverRadius;
        this.stepWidth = stepWidth;
    }

    public LinkedList<BlockPos> discoverApplicableBlocks() {
        LinkedList<BlockPos> visited = new LinkedList<>();

        int xPos = start.getX();
        int yPos = start.getY();
        int zPos = start.getZ();
        BlockPos currentPos = start;
        tryAdd(start, visited);

        EnumFacing dir = EnumFacing.NORTH;
        while (Math.abs(currentPos.getX() - xPos) <= rad && Math.abs(currentPos.getY() - yPos) <= rad
            && Math.abs(currentPos.getZ() - zPos) <= rad) {
            currentPos = currentPos.offset(dir, stepWidth);
            tryAdd(currentPos, visited);
            EnumFacing tryDirNext = dir.rotateY();
            if (!visited.contains(currentPos.offset(tryDirNext, stepWidth))) {
                dir = tryDirNext;
            }
        }

        return visited;
    }

    private void tryAdd(BlockPos at, List<BlockPos> visited) {
        if (!visited.contains(at)) {
            visited.add(at);
        }
    }

}
