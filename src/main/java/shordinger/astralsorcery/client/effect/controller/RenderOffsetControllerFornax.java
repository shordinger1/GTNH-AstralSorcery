/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.effect.controller;

import java.util.Random;

import shordinger.astralsorcery.client.ClientScheduler;
import shordinger.astralsorcery.client.effect.EntityComplexFX;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderOffsetControllerFornax
 * Created by HellFirePvP
 * Date: 20.04.2017 / 21:25
 */
public class RenderOffsetControllerFornax implements EntityComplexFX.RenderOffsetController {

    @Override
    public Vector3 changeRenderPosition(EntityComplexFX fx, Vector3 currentRenderPos, Vector3 currentMotion,
                                        float pTicks) {
        Vector3 perp = currentMotion.clone()
            .perpendicular()
            .normalize()
            .multiply(0.05);
        Random r = new Random(fx.id); // LUL tho...

        int interv = (int) ((r.nextInt() + ClientScheduler.getClientTick()) % 9);
        float part = interv + pTicks;
        float perc = part / 10F;

        float sinPart = MathHelper.sin(perc * ((float) Math.PI) * 2F);
        return currentRenderPos.clone()
            .add(
                perp.rotate(r.nextFloat() * 360F, currentMotion)
                    .multiply(sinPart));
    }

}
