/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.effect.compound;

import shordinger.astralsorcery.migration.BufferBuilder;
import com.gtnewhorizons.modularui.api.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import shordinger.astralsorcery.migration.DefaultVertexFormats;

import org.lwjgl.opengl.GL11;

import shordinger.astralsorcery.client.effect.EntityComplexFX;
import shordinger.astralsorcery.client.util.Blending;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CompoundObjectEffect
 * Created by HellFirePvP
 * Date: 16.02.2017 / 16:34
 */
public abstract class CompoundObjectEffect extends EntityComplexFX {

    @Override
    public final void render(float pTicks) {
        GlStateManager.pushMatrix();
        Tessellator tes = Tessellator.instance;
        BufferBuilder vb = tes.getBuffer();
        getGroup().beginDrawing(vb);
        render(vb, pTicks);
        tes.draw();
        GlStateManager.popMatrix();
    }

    public abstract ObjectGroup getGroup();

    public abstract void render(BufferBuilder vb, float pTicks);

    public enum ObjectGroup {

        SOLID_COLOR_SPHERE;

        public void beginDrawing(BufferBuilder vb) {
            if (this == ObjectGroup.SOLID_COLOR_SPHERE) {
                vb.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
            }
        }

        public void prepareGLContext() {
            if (this == ObjectGroup.SOLID_COLOR_SPHERE) {
                GlStateManager.enableBlend();
                Blending.DEFAULT.apply();
                GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0001F);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask(false);
            }
        }

        public void revertGLContext() {
            if (this == ObjectGroup.SOLID_COLOR_SPHERE) {
                GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
                GlStateManager.depthMask(true);
                GlStateManager.disableBlend();
                GlStateManager.enableTexture2D();
            }
        }

    }

}
