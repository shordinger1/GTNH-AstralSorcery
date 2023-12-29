/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.effect;

import java.awt.*;

import shordinger.astralsorcery.client.effect.fx.EntityFXFacingDepthParticle;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectHelper
 * Created by HellFirePvP
 * Date: 16.10.2016 / 16:25
 */
public class EffectHelper {

    public static EntityFXFacingParticle genericFlareParticle(Vector3 v) {
        return genericFlareParticle(v.getX(), v.getY(), v.getZ());
    }

    public static EntityFXFacingParticle genericFlareParticle(double x, double y, double z) {
        EntityFXFacingParticle p = new EntityFXFacingParticle(x, y, z);
        p.enableAlphaFade(EntityComplexFX.AlphaFunction.PYRAMID)
            .setAlphaMultiplier(0.75F)
            .setColor(new Color(60, 0, 255));
        EffectHandler.getInstance()
            .registerFX(p);
        return p;
    }

    public static EntityFXFacingParticle genericGatewayFlareParticle(double x, double y, double z) {
        EntityFXFacingParticle p = new EntityFXFacingParticle.Gateway(x, y, z);
        p.enableAlphaFade(EntityComplexFX.AlphaFunction.PYRAMID)
            .setAlphaMultiplier(0.75F)
            .setColor(new Color(60, 0, 255));
        EffectHandler.getInstance()
            .registerFX(p);
        return p;
    }

    public static EntityFXFacingDepthParticle genericDepthIgnoringFlareParticle(double x, double y, double z) {
        EntityFXFacingDepthParticle p = new EntityFXFacingDepthParticle(x, y, z);
        p.enableAlphaFade(EntityComplexFX.AlphaFunction.PYRAMID)
            .setAlphaMultiplier(0.75F)
            .setColor(new Color(60, 0, 255));
        EffectHandler.getInstance()
            .registerFX(p);
        return p;
    }

}
