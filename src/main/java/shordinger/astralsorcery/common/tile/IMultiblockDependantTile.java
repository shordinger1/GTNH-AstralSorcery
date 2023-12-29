/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile;

import javax.annotation.Nullable;

import shordinger.astralsorcery.common.structure.array.PatternBlockArray;
import shordinger.astralsorcery.common.util.ILocatable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IMultiblockDependantTile
 * Created by HellFirePvP
 * Date: 31.10.2017 / 15:51
 */
public interface IMultiblockDependantTile extends ILocatable {

    // 'this' tile needs to be centered on that structure
    @Nullable
    public PatternBlockArray getRequiredStructure();

}
