/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.base.patreon.data;

import java.util.List;
import java.util.UUID;

import shordinger.astralsorcery.common.base.patreon.PatreonEffectHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectProvider
 * Created by HellFirePvP
 * Date: 16.02.2019 / 17:38
 */
public interface EffectProvider<T extends PatreonEffectHelper.PatreonEffect> {

    public T buildEffect(UUID uuid, List<String> effectParameters) throws Exception;

}
