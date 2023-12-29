/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util.log;

import shordinger.astralsorcery.common.util.Provider;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LogCategory
 * Created by HellFirePvP
 * Date: 06.04.2019 / 13:19
 */
public enum LogCategory {

    PERKS,
    STRUCTURE_MATCH,
    TREE_BEACON;

    public void info(Provider<String> message) {
        LogUtil.info(this, message);
    }

    public void warn(Provider<String> message) {
        LogUtil.warn(this, message);
    }

}
