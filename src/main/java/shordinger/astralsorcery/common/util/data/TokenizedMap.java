/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util.data;

import java.util.HashMap;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TokenizedMap
 * Created by HellFirePvP
 * Date: 07.11.2016 / 11:24
 */
public class TokenizedMap<K, V extends TokenizedMap.MapToken<?>> extends HashMap<K, V> {

    public static interface MapToken<V> {

        public V getValue();

    }

}
