/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.effect;

import javax.annotation.Nullable;

import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.util.ILocatable;
import shordinger.astralsorcery.migration.block.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectPositionList
 * Created by HellFirePvP
 * Date: 17.10.2016 / 09:33
 */
public abstract class CEffectPositionList extends CEffectPositionListGen<GenListEntries.SimpleBlockPosEntry> {

    public CEffectPositionList(@Nullable ILocatable origin, IWeakConstellation c, String cfgName, int maxCount,
                               Verifier verifier) {
        super(origin, c, cfgName, maxCount, verifier, GenListEntries.SimpleBlockPosEntry::new);
    }

    public boolean offerNewBlockPos(BlockPos pos) {
        return offerNewElement(new GenListEntries.SimpleBlockPosEntry(pos));
    }

    @Nullable
    public BlockPos getRandomPosition() {
        GenListEntries.SimpleBlockPosEntry entry = getRandomElementByChance(rand);
        if (entry != null) {
            return entry.pos();
        }
        return null;
    }

}
