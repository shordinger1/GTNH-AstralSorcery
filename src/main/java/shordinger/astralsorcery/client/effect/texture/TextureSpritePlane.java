/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.effect.texture;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.effect.IComplexEffect;
import shordinger.astralsorcery.client.util.resource.SpriteSheetResource;
import shordinger.astralsorcery.common.util.data.Tuple;
import shordinger.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TextureSpritePlane
 * Created by HellFirePvP
 * Date: 25.09.2016 / 22:52
 */
@SideOnly(Side.CLIENT)
public class TextureSpritePlane extends TexturePlane implements IComplexEffect.PreventRemoval {

    private final SpriteSheetResource spriteSheet;

    public TextureSpritePlane(SpriteSheetResource spriteSheet, Vector3 axis) {
        super(spriteSheet.getResource(), axis);
        this.spriteSheet = spriteSheet;
        this.uLength = spriteSheet.getULength();
        this.vLength = spriteSheet.getVLength();
        this.setMaxAge(spriteSheet.getFrameCount());
    }

    public SpriteSheetResource getSpriteSheet() {
        return spriteSheet;
    }

    @Override
    public void tick() {
        int frame = getAge();
        Tuple<Double, Double> uv = spriteSheet.getUVOffset(frame);

        this.u = uv.key;
        this.v = uv.value;

        super.tick();
    }

}
