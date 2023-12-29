/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.base.patreon.base;

import java.awt.*;
import java.util.UUID;

import javax.annotation.Nullable;

import shordinger.astralsorcery.client.util.resource.TextureQuery;
import shordinger.astralsorcery.common.base.patreon.PatreonEffectHelper;
import shordinger.astralsorcery.common.base.patreon.entity.PartialEntityCrystal;
import shordinger.astralsorcery.common.base.patreon.flare.PatreonPartialEntity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PtEffectFloatingCrystal
 * Created by HellFirePvP
 * Date: 14.07.2018 / 19:59
 */
public class PtEffectFloatingCrystal extends PatreonEffectHelper.PatreonEffect {

    private Color colorTheme;
    private TextureQuery textureQuery;

    public PtEffectFloatingCrystal(UUID uniqueId, Color colorTheme, TextureQuery textureQuery) {
        super(uniqueId, null);
        this.colorTheme = colorTheme;
        this.textureQuery = textureQuery;
    }

    @Override
    public boolean hasPartialEntity() {
        return true;
    }

    @Nullable
    @Override
    public PatreonPartialEntity createEntity(UUID playerUUID) {
        return new PartialEntityCrystal(playerUUID).setColorTheme(this.colorTheme)
            .setQueryTexture(this.textureQuery);
    }

}
