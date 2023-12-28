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
import shordinger.astralsorcery.common.base.patreon.base.PtEffectTreeBeacon;
import shordinger.astralsorcery.common.base.patreon.data.EffectProvider;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TreeBeaconEffectProvider
 * Created by HellFirePvP
 * Date: 16.02.2019 / 18:42
 */
public class TreeBeaconEffectProvider implements EffectProvider<PtEffectTreeBeacon> {

    @Override
    public PtEffectTreeBeacon buildEffect(UUID uuid, List<String> effectParameters) throws Exception {
        UUID uniqueId = UUID.fromString(effectParameters.get(0));
        PatreonEffectHelper.FlareColor flareColor = PatreonEffectHelper.FlareColor.valueOf(effectParameters.get(1));
        int overlay = Integer.parseInt(effectParameters.get(2));
        int drain = Integer.parseInt(effectParameters.get(3));
        int tree = Integer.parseInt(effectParameters.get(4));
        return new PtEffectTreeBeacon(uniqueId, flareColor).setOverlayColor(overlay)
            .setDrainColor(drain)
            .setTreeColor(tree);
    }

}
