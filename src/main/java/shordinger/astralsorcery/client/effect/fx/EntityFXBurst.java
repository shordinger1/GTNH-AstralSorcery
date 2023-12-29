/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.effect.fx;

import shordinger.astralsorcery.client.effect.IComplexEffect;
import shordinger.astralsorcery.client.util.SpriteLibrary;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityFXBurst
 * Created by HellFirePvP
 * Date: 17.09.2016 / 23:52
 */
public class EntityFXBurst extends EntityFXFacingSprite implements IComplexEffect.PreventRemoval {

    public EntityFXBurst(double x, double y, double z) {
        super(SpriteLibrary.spriteCollectorBurst, x, y, z);
    }

    public EntityFXBurst(double x, double y, double z, float scale) {
        super(SpriteLibrary.spriteCollectorBurst, x, y, z, scale);
    }

}
