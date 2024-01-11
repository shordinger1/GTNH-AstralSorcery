/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.effect.light;

import java.awt.*;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import shordinger.astralsorcery.client.effect.EntityComplexFX;
import shordinger.astralsorcery.client.effect.IComplexEffect;
import shordinger.astralsorcery.client.util.Blending;
import shordinger.astralsorcery.client.util.SpriteLibrary;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.util.data.Tuple;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.renderer.BufferBuilder;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.client.renderer.Tessellator;
import shordinger.wrapper.net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import shordinger.wrapper.net.minecraft.entity.Entity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectLightbeam
 * Created by HellFirePvP
 * Date: 06.08.2016 / 15:05
 */
public class EffectLightbeam implements IComplexEffect, IComplexEffect.PreventRemoval {

    private final Vector3 from, to, aim, aimPerp;
    private final double fromSize, toSize;
    private int maxAge = 64;
    private int age = 0;
    private EntityComplexFX.AlphaFunction alphaFunction = EntityComplexFX.AlphaFunction.PYRAMID;
    private float alphaMultiplier = 1F;
    private float cR = 1F, cG = 1F, cB = 1F, cA = 1F;
    private double distanceCapSq = Config.maxEffectRenderDistanceSq;

    private boolean flagRemoved = true;

    public EffectLightbeam(Vector3 from, Vector3 to, double fromSize, double toSize) {
        this.from = from;
        this.to = to;
        this.aim = to.clone()
            .subtract(from);
        this.aimPerp = aim.clone()
            .perpendicular()
            .normalize();
        this.fromSize = fromSize;
        this.toSize = toSize;
    }

    public EffectLightbeam(Vector3 from, Vector3 to, double size) {
        this(from, to, size, size);
    }

    public void setMaxAge(int newMax) {
        this.maxAge = newMax;
    }

    public void setDead() {
        age = maxAge;
    }

    public EffectLightbeam setDistanceCapSq(double distanceCapSq) {
        this.distanceCapSq = distanceCapSq;
        return this;
    }

    public EffectLightbeam setColorOverlay(float red, float green, float blue, float alpha) {
        this.cR = red;
        this.cG = green;
        this.cB = blue;
        this.cA = alpha;
        return this;
    }

    public EffectLightbeam setColorOverlay(Color color) {
        this.cR = color.getRed() / 255F;
        this.cG = color.getGreen() / 255F;
        this.cB = color.getBlue() / 255F;
        this.cA = color.getAlpha() / 255F;
        return this;
    }

    public EffectLightbeam setAlphaMultiplier(float alphaMultiplier) {
        this.alphaMultiplier = alphaMultiplier;
        return this;
    }

    public EffectLightbeam setAlphaFunction(@Nonnull EntityComplexFX.AlphaFunction function) {
        this.alphaFunction = function;
        return this;
    }

    @Override
    public boolean canRemove() {
        return age >= maxAge;
    }

    @Override
    public RenderTarget getRenderTarget() {
        return RenderTarget.RENDERLOOP;
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

    @Override
    public void render(float pTicks) {
        Entity rView = Minecraft.getMinecraft()
            .getRenderViewEntity();
        if (rView == null) rView = Minecraft.getMinecraft().player;
        if (rView.getDistanceSq(from.getX(), from.getY(), from.getZ()) > distanceCapSq) return;

        float tr = alphaFunction.getAlpha(age, maxAge);
        tr *= 0.6;
        tr *= alphaMultiplier;

        GlStateManager.pushMatrix();
        removeOldTranslate(rView, pTicks);
        GlStateManager.color(cR * tr, cG * tr, cB * tr, cA * tr);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.001F);
        Blending.PREALPHA.applyStateManager();
        boolean lighting = GL11.glGetBoolean(GL11.GL_LIGHTING);
        if (lighting) {
            GlStateManager.disableLighting();
        }
        SpriteLibrary.spriteLightbeam.getResource()
            .bindTexture();

        renderCurrentTextureAroundAxis(Math.toRadians(0F));
        renderCurrentTextureAroundAxis(Math.toRadians(120F));
        renderCurrentTextureAroundAxis(Math.toRadians(240F));

        if (lighting) {
            GlStateManager.enableLighting();
        }
        Blending.DEFAULT.applyStateManager();
        GlStateManager.enableBlend();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    private void removeOldTranslate(Entity entity, float partialTicks) {
        double x = entity.lastTickPosX + ((entity.posX - entity.lastTickPosX) * partialTicks);
        double y = entity.lastTickPosY + ((entity.posY - entity.lastTickPosY) * partialTicks);
        double z = entity.lastTickPosZ + ((entity.posZ - entity.lastTickPosZ) * partialTicks);
        GlStateManager.translate(-x, -y, -z);
    }

    private void renderCurrentTextureAroundAxis(double angle) {
        Vector3 perp = aimPerp.clone()
            .rotate(angle, aim)
            .normalize();
        Vector3 perpFrom = perp.clone()
            .multiply(fromSize);
        Vector3 perpTo = perp.multiply(toSize);

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buf = tes.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        Tuple<Double, Double> uvOffset = SpriteLibrary.spriteLightbeam.getUVOffset(age);
        double u = uvOffset.key;
        double v = uvOffset.value;
        double uWidth = SpriteLibrary.spriteLightbeam.getULength();
        double vHeight = SpriteLibrary.spriteLightbeam.getVLength();

        Vector3 vec = from.clone()
            .add(
                perpFrom.clone()
                    .multiply(-1));
        buf.pos(vec.getX(), vec.getY(), vec.getZ())
            .tex(u, v + vHeight)
            .endVertex();
        vec = from.clone()
            .add(perpFrom);
        buf.pos(vec.getX(), vec.getY(), vec.getZ())
            .tex(u + uWidth, v + vHeight)
            .endVertex();
        vec = to.clone()
            .add(perpTo);
        buf.pos(vec.getX(), vec.getY(), vec.getZ())
            .tex(u + uWidth, v)
            .endVertex();
        vec = to.clone()
            .add(
                perpTo.clone()
                    .multiply(-1));
        buf.pos(vec.getX(), vec.getY(), vec.getZ())
            .tex(u, v)
            .endVertex();

        tes.draw();
    }

    @Override
    public void tick() {
        age++;
    }

}
