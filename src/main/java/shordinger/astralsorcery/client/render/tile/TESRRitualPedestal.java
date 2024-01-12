/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.render.tile;

import java.awt.*;

import org.lwjgl.opengl.GL11;

import shordinger.astralsorcery.client.ClientScheduler;
import shordinger.astralsorcery.client.effect.texture.TextureSpritePlane;
import shordinger.astralsorcery.client.util.Blending;
import shordinger.astralsorcery.client.util.RenderConstellation;
import shordinger.astralsorcery.client.util.RenderingUtils;
import shordinger.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import shordinger.astralsorcery.common.item.crystal.ItemTunedCelestialCrystal;
import shordinger.astralsorcery.common.item.crystal.base.ItemTunedCrystalBase;
import shordinger.astralsorcery.common.tile.TileRitualPedestal;
import shordinger.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRRitualPedestal
 * Created by HellFirePvP
 * Date: 28.09.2016 / 20:07
 */
public class TESRRitualPedestal extends TileEntitySpecialRenderer<TileRitualPedestal> {

    @Override
    public void render(TileRitualPedestal te, double x, double y, double z, float partialTicks, int destroyStage,
                       float alpha) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        renderCrystalStack(te, x, y, z);

        if (te.shouldDoAdditionalEffects()) {
            renderEffects(te);

            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_ALPHA_TEST);

            IConstellation c = te.getDisplayConstellation();
            if (c != null) {
                float alphaDaytime = ConstellationSkyHandler.getInstance()
                    .getCurrentDaytimeDistribution(te.getWorld());
                alphaDaytime *= 0.8F;

                int max = 5000;
                int t = (int) (ClientScheduler.getClientTick() % max);
                float halfAge = max / 2F;
                float tr = 1F - (Math.abs(halfAge - t) / halfAge);
                tr *= 2;

                int tick = te.getEffectWorkTick();
                float percRunning = ((float) tick / (float) TileRitualPedestal.MAX_EFFECT_TICK);

                RenderingUtils.removeStandartTranslationFromTESRMatrix(partialTicks);

                float br = 0.6F * (alphaDaytime * percRunning);

                RenderConstellation.renderConstellationIntoWorldFlat(
                    c,
                    c.getConstellationColor(),
                    new Vector3(te).add(0.5, 0.04, 0.5),
                    3 + tr,
                    2,
                    0.1F + br);
            }

            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glPopMatrix();
        }
        GL11.glPopAttrib();
    }

    private void renderEffects(TileRitualPedestal te) {
        int tick = te.getEffectWorkTick();
        float percRunning = ((float) tick / (float) TileRitualPedestal.MAX_EFFECT_TICK);
        if (percRunning > 1E-4) {
            TextureSpritePlane sprite = te.getHaloEffectSprite();
            float alphaMul = ConstellationSkyHandler.getInstance()
                .getCurrentDaytimeDistribution(Minecraft.getMinecraft().theWorld);
            sprite.setAlphaMultiplier(percRunning * alphaMul);
        }
    }

    private void renderCrystalStack(TileRitualPedestal te, double x, double y, double z) {
        ItemStack i = te.getCatalystCache();
        if (!i.isEmpty()) {
            Item it = i.getItem();
            if (it instanceof ItemTunedCrystalBase) {
                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                GL11.glPushMatrix();
                boolean celestial = it instanceof ItemTunedCelestialCrystal;
                Color c = celestial ? BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL.displayColor
                    : BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor;
                long sBase = 1553015L;
                sBase ^= (long) te.getPos()
                    .getX();
                sBase ^= (long) te.getPos()
                    .getY();
                sBase ^= (long) te.getPos()
                    .getZ();
                GL11.glEnable(GL11.GL_BLEND);
                Blending.DEFAULT.apply();
                RenderingUtils.renderLightRayEffects(
                    x + 0.5,
                    y + 1.3,
                    z + 0.5,
                    c,
                    sBase,
                    ClientScheduler.getClientTick(),
                    20,
                    50,
                    25);

                GL11.glTranslated(x + 0.5, y + 1, z + 0.5);
                GL11.glScaled(0.6, 0.6, 0.6);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                TESRCollectorCrystal.renderCrystal(null, celestial, true);

                GL11.glPopMatrix();
                GL11.glPopAttrib();
            }
        }
    }

}
