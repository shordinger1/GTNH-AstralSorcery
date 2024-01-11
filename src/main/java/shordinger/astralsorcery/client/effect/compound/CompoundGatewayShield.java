/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.effect.compound;

import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.wrapper.net.minecraft.client.renderer.BufferBuilder;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CompoundGatewayShield
 * Created by HellFirePvP
 * Date: 21.04.2017 / 21:57
 */
public class CompoundGatewayShield extends CompoundEffectSphere {

    public CompoundGatewayShield(Vector3 centralPoint, Vector3 southNorthAxis, double sphereRadius, int fractionsSplit, int fractionsCircle) {
        super(centralPoint, southNorthAxis, sphereRadius, fractionsSplit, fractionsCircle);
    }

    @Override
    public void render(BufferBuilder vb, float pTicks) {
        if(EffectHandler.getInstance().renderGateway) {
            super.render(vb, pTicks);
        }
    }
}
