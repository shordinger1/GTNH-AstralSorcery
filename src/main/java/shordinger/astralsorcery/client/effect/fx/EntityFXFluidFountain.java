/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.effect.fx;

import java.awt.*;
import java.util.Random;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fluids.FluidStack;

import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.effect.EntityComplexFX;
import shordinger.astralsorcery.client.util.RenderingUtils;
import shordinger.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityFXFluidFountain
 * Created by HellFirePvP
 * Date: 01.11.2017 / 16:19
 */
public class EntityFXFluidFountain extends EntityComplexFX {

    private static final Random rand = new Random();
    private TextureAtlasSprite tas;
    private Vector3 offset;
    private FluidStack fluidStack;

    private EntityFXFluidFountain(Vector3 offset, int maxAge, TextureAtlasSprite tas, FluidStack fluid) {
        this.offset = offset;
        this.maxAge = maxAge;
        this.tas = tas;
        this.fluidStack = fluid;
    }

    public static EntityFXFluidFountain spawnAt(Vector3 pos, FluidStack fluid) {
        TextureAtlasSprite tas = RenderingUtils.tryGetFlowingTextureOfFluidStack(fluid);
        EntityFXFluidFountain f = new EntityFXFluidFountain(pos, 60, tas, fluid);
        EffectHandler.getInstance()
            .registerFX(f);
        return f;
    }

    @Override
    public void tick() {
        super.tick();

        EntityFXFloatingCube cube = new EntityFXFloatingCube(tas);
        cube.setPosition(offset);
        cube.setTextureSubSizePercentage(1F / 16F)
            .setMaxAge(40 + rand.nextInt(40));
        cube.setScale(0.1F)
            .tumble();
        Vector3 v = Vector3.positiveYRandom();
        v.setY(v.getY() * 8)
            .normalize()
            .multiply(
                new Vector3(
                    0.01F + rand.nextFloat() * 0.015F,
                    0.09F + rand.nextFloat() * 0.015F,
                    0.01F + rand.nextFloat() * 0.015F));
        cube.setMotion(v);
        cube.setMotionController((c, motion) -> motion.setY(motion.getY() - 0.003F));
        cube.setColorHandler(
            cb -> new Color(
                fluidStack.getFluid()
                    .getColor(fluidStack)));
        EffectHandler.getInstance()
            .registerFX(cube);
    }

    @Override
    public void render(float pTicks) {
    }

}
