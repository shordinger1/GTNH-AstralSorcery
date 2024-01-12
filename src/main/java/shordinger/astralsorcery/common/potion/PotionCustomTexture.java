/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.potion;

import java.awt.*;
import java.util.Random;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.util.TextureHelper;
import shordinger.astralsorcery.client.util.resource.BindableResource;
import shordinger.wrapper.net.minecraft.client.renderer.BufferBuilder;
import shordinger.wrapper.net.minecraft.client.renderer.Tessellator;
import shordinger.wrapper.net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import shordinger.wrapper.net.minecraft.potion.Potion;
import shordinger.wrapper.net.minecraft.potion.PotionEffect;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PotionCustomTexture
 * Created by HellFirePvP
 * Date: 13.11.2016 / 01:36
 */
public abstract class PotionCustomTexture extends Potion {

    protected static final Random rand = new Random();

    protected PotionCustomTexture(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
    }

    @Override
    public boolean hasStatusIcon() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public abstract BindableResource getResource();

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
        Tessellator tes = Tessellator.getInstance();
        double wh = 18;
        double offsetX = 6;
        double offsetY = 7;
        Color c = new Color(getLiquidColor());
        float red = ((float) c.getRed()) / 255F;
        float green = ((float) c.getGreen()) / 255F;
        float blue = ((float) c.getBlue()) / 255F;

        getResource().bind();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        vb.pos(x + offsetX, y + offsetY, 0)
            .tex(0, 0)
            .color(red, green, blue, 1F)
            .endVertex();
        vb.pos(x + offsetX, y + offsetY + wh, 0)
            .tex(0, 1)
            .color(red, green, blue, 1F)
            .endVertex();
        vb.pos(x + offsetX + wh, y + offsetY + wh, 0)
            .tex(1, 1)
            .color(red, green, blue, 1F)
            .endVertex();
        vb.pos(x + offsetX + wh, y + offsetY, 0)
            .tex(1, 0)
            .color(red, green, blue, 1F)
            .endVertex();

        tes.draw();
        TextureHelper.refreshTextureBindState();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
        Tessellator tes = Tessellator.getInstance();
        double wh = 18;
        double offsetX = 3;
        double offsetY = 3;
        Color c = new Color(getLiquidColor());
        float red = ((float) c.getRed()) / 255F;
        float green = ((float) c.getGreen()) / 255F;
        float blue = ((float) c.getBlue()) / 255F;

        getResource().bind();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        vb.pos(x + offsetX, y + offsetY, 0)
            .tex(0, 0)
            .color(red, green, blue, alpha)
            .endVertex();
        vb.pos(x + offsetX, y + offsetY + wh, 0)
            .tex(0, 1)
            .color(red, green, blue, alpha)
            .endVertex();
        vb.pos(x + offsetX + wh, y + offsetY + wh, 0)
            .tex(1, 1)
            .color(red, green, blue, alpha)
            .endVertex();
        vb.pos(x + offsetX + wh, y + offsetY, 0)
            .tex(1, 0)
            .color(red, green, blue, alpha)
            .endVertex();

        tes.draw();
        TextureHelper.refreshTextureBindState();
    }
}
