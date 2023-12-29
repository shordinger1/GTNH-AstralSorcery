/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.base.patreon.entity;

import java.awt.*;
import java.util.UUID;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingSprite;
import shordinger.astralsorcery.client.util.SpriteLibrary;
import shordinger.astralsorcery.client.util.resource.SpriteSheetResource;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.util.Provider;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PartialEntityFlareColor
 * Created by HellFirePvP
 * Date: 04.01.2019 / 20:39
 */
public class PartialEntityFlareColor extends PartialEntityFlare {

    private Provider<Color> colorFunction;

    public PartialEntityFlareColor(UUID ownerUUID, Provider<Color> colorFunction) {
        super(null, ownerUUID);
        this.colorFunction = colorFunction;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected SpriteSheetResource getSprite() {
        return SpriteLibrary.spriteDynColorFlare;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected Color getColor() {
        return rand.nextInt(3) == 0 ? colorFunction.provide()
            : colorFunction.provide()
            .brighter();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void tickInRenderDistance() {
        if (clientSprite != null) {
            EntityFXFacingSprite p = (EntityFXFacingSprite) clientSprite;
            if (!p.isRemoved() && Config.enablePatreonEffects) {
                p.setOverlayColor(colorFunction.provide());
            }
        }

        super.tickInRenderDistance();
    }
}
