/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.effect.controller.orbital;

import java.awt.*;
import java.util.Random;

import net.minecraft.client.Minecraft;

import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.tile.network.TileCollectorCrystal;
import shordinger.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OrbitalEffectCollector
 * Created by HellFirePvP
 * Date: 04.11.2016 / 01:58
 */
public class OrbitalEffectCollector
    implements OrbitalEffectController.OrbitPersistence, OrbitalEffectController.OrbitPointEffect {

    private static final Random rand = new Random();

    private final Color colorOverride;

    public OrbitalEffectCollector(TileCollectorCrystal tile) {
        this.colorOverride = tile.getConstellation()
            .getConstellationColor();
    }

    @Override
    public boolean canPersist(OrbitalEffectController controller) {
        return false;
    }

    @Override
    public void doPointTickEffect(OrbitalEffectController ctrl, Vector3 pos) {
        if (!Minecraft.isFancyGraphicsEnabled()) return;
        if (rand.nextInt(3) == 0) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(pos.getX(), pos.getY(), pos.getZ());
            p.setMaxAge(15);
            p.setColor(colorOverride);
            p.scale(0.15F)
                .gravity(0.008);
        }
        if (rand.nextInt(3) == 0) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(pos.getX(), pos.getY(), pos.getZ());
            p.motion(
                (rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1));
            p.setMaxAge(25);
            p.scale(0.15F)
                .setColor(colorOverride.brighter());
        }
    }

}
