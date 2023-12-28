/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.base.patreon.data.provider;

import java.util.List;
import java.util.UUID;

import shordinger.astralsorcery.common.base.patreon.PatreonEffectHelper;
import shordinger.astralsorcery.common.base.patreon.data.EffectProvider;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FlareEffectProvider
 * Created by HellFirePvP
 * Date: 16.02.2019 / 17:46
 */
public class FlareEffectProvider implements EffectProvider<PatreonEffectHelper.PatreonEffect> {

    @Override
    public PatreonEffectHelper.PatreonEffect buildEffect(UUID uuid, List<String> effectParameters) throws Exception {
        UUID uniqueId = UUID.fromString(effectParameters.get(0));
        PatreonEffectHelper.FlareColor fc = null;
        if (!"null".equals(effectParameters.get(1))) {
            fc = PatreonEffectHelper.FlareColor.valueOf(effectParameters.get(1));
        }
        return new PatreonEffectHelper.PatreonEffect(uniqueId, fc);
    }

}
