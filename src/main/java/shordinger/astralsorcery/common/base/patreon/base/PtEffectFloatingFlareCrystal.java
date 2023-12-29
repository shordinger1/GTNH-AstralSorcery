/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.base.patreon.base;

import java.awt.*;
import java.util.UUID;

import javax.annotation.Nullable;

import shordinger.astralsorcery.client.util.resource.TextureQuery;
import shordinger.astralsorcery.common.base.patreon.PatreonEffectHelper;
import shordinger.astralsorcery.common.base.patreon.entity.PartialEntityFlareCrystal;
import shordinger.astralsorcery.common.base.patreon.flare.PatreonPartialEntity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PtEffectFloatingFlareCrystal
 * Created by HellFirePvP
 * Date: 27.12.2018 / 01:29
 */
public class PtEffectFloatingFlareCrystal extends PatreonEffectHelper.PatreonEffect {

    private Color colorTheme;
    private PatreonEffectHelper.FlareColor flareColor;
    private TextureQuery crystalTexture;

    public PtEffectFloatingFlareCrystal(UUID uniqueId, Color colorTheme, PatreonEffectHelper.FlareColor flareColor,
                                        TextureQuery crystalTexture) {
        super(uniqueId, null);
        this.colorTheme = colorTheme;
        this.flareColor = flareColor;
        this.crystalTexture = crystalTexture;
    }

    @Override
    public boolean hasPartialEntity() {
        return true;
    }

    @Nullable
    @Override
    public PatreonPartialEntity createEntity(UUID playerUUID) {
        return new PartialEntityFlareCrystal(this.flareColor, playerUUID).setQueryTexture(this.crystalTexture)
            .setColorTheme(this.colorTheme);
    }

}
