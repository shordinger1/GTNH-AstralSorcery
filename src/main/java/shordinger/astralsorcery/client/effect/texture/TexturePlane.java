/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.effect.texture;

import shordinger.astralsorcery.client.effect.EntityComplexFX;
import shordinger.astralsorcery.client.effect.IComplexEffect;
import shordinger.astralsorcery.client.util.Blending;
import shordinger.astralsorcery.client.util.RenderingUtils;
import shordinger.astralsorcery.client.util.resource.AbstractRenderableTexture;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TexturePlane
 * Created by HellFirePvP
 * Date: 02.08.2016 / 12:55
 */
public class TexturePlane implements IComplexEffect, IComplexEffect.PreventRemoval {

    protected double u = 0, v = 0, uLength = 1, vLength = 1;

    private float lastRenderDegree = 0F;

    private boolean alphaGradient = false;

    private int counter = 0;
    private boolean remove = false;
    private EntityComplexFX.RefreshFunction refreshFunc = null;

    private Color colorOverlay = Color.WHITE;
    private int ticksPerFullRot = 100;
    private float fixDegree = 0;
    private int maxAge = -1;
    private Vector3 pos = new Vector3(0, 0, 0);
    private Vector3 prevPos = new Vector3(0, 0, 0);
    private float scale = 1F;
    private EntityComplexFX.AlphaFunction alphaFunction = EntityComplexFX.AlphaFunction.CONSTANT;
    private EntityComplexFX.RenderAlphaFunction<TexturePlane> renderAlphaFunction = null;
    private float alphaMultiplier = 1F;
    private EntityComplexFX.ScaleFunction<TexturePlane> scaleFunc;
    private EntityComplexFX.PositionController<TexturePlane> posFunc;
    private boolean flagRemoved = true;

    private final AbstractRenderableTexture texture;
    private final Vector3 axis;

    public TexturePlane(AbstractRenderableTexture texture, Vector3 axis) {
        this.texture = texture;
        this.axis = axis;
    }

    public TexturePlane setPosition(Vector3 pos) {
        this.pos = pos;
        return this;
    }

    public TexturePlane setStaticUVOffset(double u, double v) {
        this.u = u;
        this.v = v;
        return this;
    }

    public TexturePlane setUVLength(double uLength, double vLength) {
        this.uLength = uLength;
        this.vLength = vLength;
        return this;
    }

    public TexturePlane setTicksPerFullRotation(int ticksPerFullRot) {
        this.ticksPerFullRot = ticksPerFullRot;
        return this;
    }

    public TexturePlane setNoRotation(float fixedDregree) {
        this.ticksPerFullRot = -1;
        this.fixDegree = fixedDregree;
        return this;
    }

    public TexturePlane setMaxAge(int ticksMaxAge) {
        this.maxAge = ticksMaxAge;
        return this;
    }

    public TexturePlane setColorOverlay(Color colorOverlay) {
        this.colorOverlay = colorOverlay;
        return this;
    }

    public TexturePlane setScale(float scale) {
        this.scale = scale;
        return this;
    }

    public TexturePlane setPosFunc(EntityComplexFX.PositionController<TexturePlane> posFunc) {
        this.posFunc = posFunc;
        return this;
    }

    public TexturePlane setScaleFunction(EntityComplexFX.ScaleFunction<TexturePlane> function) {
        this.scaleFunc = function;
        return this;
    }

    public TexturePlane setRefreshFunc(EntityComplexFX.RefreshFunction refreshFunc) {
        this.refreshFunc = refreshFunc;
        return this;
    }

    public TexturePlane setRenderAlphaFunction(EntityComplexFX.RenderAlphaFunction<TexturePlane> renderAlphaFunction) {
        this.renderAlphaFunction = renderAlphaFunction;
        return this;
    }

    public TexturePlane setAlphaFunction(EntityComplexFX.AlphaFunction alphaFunction) {
        this.alphaFunction = alphaFunction;
        return this;
    }

    public TexturePlane setAlphaMultiplier(float alphaMultiplier) {
        this.alphaMultiplier = alphaMultiplier;
        return this;
    }

    public TexturePlane setAlphaOverDistance(boolean alphaGradient) {
        this.alphaGradient = alphaGradient;
        return this;
    }

    public float getAlphaDistanceMultiplier(double dstSq) {
        double maxSqDst = Config.maxEffectRenderDistanceSq;
        return 1F - (float) (dstSq / maxSqDst);
    }

    public boolean isRemoved() {
        return flagRemoved;
    }

    public void flagAsRemoved() {
        flagRemoved = true;
    }

    public void clearRemoveFlag() {
        flagRemoved = false;
    }

    public void setDead() {
        remove = true;
    }

    public int getAge() {
        return counter;
    }

    public int getMaxAge() {
        return maxAge;
    }

    @Override
    public int getLayer() {
        return 1;
    }

    @Override
    public RenderTarget getRenderTarget() {
        return RenderTarget.RENDERLOOP;
    }

    @Override
    public void tick() {
        counter++;

        this.prevPos = this.pos.clone();
        if (this.posFunc != null) {
            this.pos = this.posFunc.updatePosition(this, this.pos.clone(), new Vector3());
        }

        if (maxAge >= 0 && counter >= maxAge) {
            if(refreshFunc != null) {
                Entity rView = Minecraft.getMinecraft().getRenderViewEntity();
                if(rView == null) rView = Minecraft.getMinecraft().player;
                if(rView.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) <= Config.maxEffectRenderDistanceSq) {
                    if(refreshFunc.shouldRefresh()) {
                        counter = 0;
                        return;
                    }
                }
            }
            setDead();
        }
    }

    @Override
    public boolean canRemove() {
        return remove;
    }

    @Override
    public void render(float partialTicks) {
        Entity rView = Minecraft.getMinecraft().getRenderViewEntity();
        if(rView == null) rView = Minecraft.getMinecraft().player;
        Vector3 rPos = new Vector3(
                RenderingUtils.interpolate(this.prevPos.getX(), this.pos.getX(), partialTicks),
                RenderingUtils.interpolate(this.prevPos.getY(), this.pos.getY(), partialTicks),
                RenderingUtils.interpolate(this.prevPos.getZ(), this.pos.getZ(), partialTicks));
        double dst = rView.getDistanceSq(rPos.getX(), rPos.getY(), rPos.getZ());
        if(dst > Config.maxEffectRenderDistanceSq) return;

        float alphaMul = alphaFunction.getAlpha(counter, maxAge);
        float alphaGrad = (colorOverlay.getAlpha() / 255F) * alphaMul * this.alphaMultiplier;
        if(alphaGradient) {
            alphaGrad = getAlphaDistanceMultiplier(dst) * alphaMul * this.alphaMultiplier;
        }
        if(renderAlphaFunction != null) {
            alphaGrad = renderAlphaFunction.getRenderAlpha(this, alphaGrad);
        }

        GlStateManager.pushMatrix();
        GlStateManager.color(colorOverlay.getRed() / 255F, colorOverlay.getGreen() / 255F, colorOverlay.getBlue() / 255F, alphaGrad);
        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();
        Vector3 axis = this.axis.clone();
        float deg;
        if(ticksPerFullRot >= 0) {
            float anglePercent = ((float) (counter)) / ((float) ticksPerFullRot);
            deg = anglePercent * 360F;
            deg = RenderingUtils.interpolateRotation(lastRenderDegree, deg, partialTicks);
            this.lastRenderDegree = deg;
        } else {
            deg = fixDegree;
        }

        GlStateManager.disableCull();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0001F);

        currRenderAroundAxis(rPos, partialTicks, Math.toRadians(deg), axis);

        GlStateManager.enableCull();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void currRenderAroundAxis(Vector3 position, float parTicks, double angle, Vector3 axis) {
        float scale = this.scale;
        if(scaleFunc != null) {
            scale = scaleFunc.getScale(this, position.clone(), parTicks, scale);
        }
        texture.bindTexture();
        RenderingUtils.renderAngleRotatedTexturedRect(position, axis, angle, scale, u, v, uLength, vLength, parTicks);
    }

}
