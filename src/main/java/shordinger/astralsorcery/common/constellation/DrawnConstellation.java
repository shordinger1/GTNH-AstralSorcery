/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DrawnConstellation
 * Created by HellFirePvP
 * Date: 01.05.2017 / 14:01
 */
public class DrawnConstellation {

    public static final int CONSTELLATION_DRAW_SIZE = 30;

    public final Point point;
    public final IConstellation constellation;

    public DrawnConstellation(Point point, IConstellation constellation) {
        this.point = point;
        this.constellation = constellation;
    }

}
