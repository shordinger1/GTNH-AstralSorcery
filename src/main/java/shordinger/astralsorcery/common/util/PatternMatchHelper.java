/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util;

import net.minecraft.world.World;

import shordinger.astralsorcery.common.data.world.WorldCacheManager;
import shordinger.astralsorcery.common.data.world.data.StructureMatchingBuffer;
import shordinger.astralsorcery.common.structure.array.PatternBlockArray;
import shordinger.astralsorcery.common.structure.change.ChangeSubscriber;
import shordinger.astralsorcery.common.structure.match.StructureMatcherPatternArray;
import shordinger.astralsorcery.migration.block.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatternMatchHelper
 * Created by HellFirePvP
 * Date: 30.12.2018 / 12:47
 */
public class PatternMatchHelper {

    public static ChangeSubscriber<StructureMatcherPatternArray> getOrCreateMatcher(World world, BlockPos pos,
                                                                                    PatternBlockArray pattern) {
        StructureMatchingBuffer buf = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.STRUCTURE_MATCH);
        ChangeSubscriber<?> existingSubscriber = buf.getSubscriber(pos);
        if (existingSubscriber != null) {
            return (ChangeSubscriber<StructureMatcherPatternArray>) existingSubscriber;
        } else {
            return buf.observeAndInitializePattern(world, pos, pattern);
        }
    }

}
