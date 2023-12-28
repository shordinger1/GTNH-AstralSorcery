/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.gui;

import java.awt.*;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

import org.lwjgl.opengl.GL11;

import shordinger.astralsorcery.client.gui.base.GuiWHScreen;
import shordinger.astralsorcery.client.util.Blending;
import shordinger.astralsorcery.client.util.MoonPhaseRenderHelper;
import shordinger.astralsorcery.client.util.RenderConstellation;
import shordinger.astralsorcery.client.util.TextureHelper;
import shordinger.astralsorcery.client.util.resource.AssetLibrary;
import shordinger.astralsorcery.client.util.resource.AssetLoader;
import shordinger.astralsorcery.client.util.resource.BindableResource;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.constellation.MoonPhase;
import shordinger.astralsorcery.common.lib.Sounds;
import shordinger.astralsorcery.common.util.SoundHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiKnowledgeFragment
 * Created by HellFirePvP
 * Date: 27.10.2018 / 18:49
 */
public class GuiKnowledgeFragment extends GuiWHScreen {

    private static final BindableResource textureScroll = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "guicontippaper");

    private final IConstellation constellation;
    private List<MoonPhase> phases;

    public GuiKnowledgeFragment(IConstellation c, List<MoonPhase> phases) {
        super(344, 275);
        this.constellation = c;
        this.phases = phases;
    }

    @Override
    public void initGui() {
        super.initGui();

        SoundHelper.playSoundClient(Sounds.bookFlip, 1F, 1F);
    }

    @Override
    public void onGuiClosed() {
        SoundHelper.playSoundClient(Sounds.bookFlip, 1F, 1F);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        drawScroll();

        drawHeader();

        drawConstellation();

        drawPhaseInformation();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void drawHeader() {
        String locName = I18n.format(constellation.getUnlocalizedName())
            .toUpperCase();
        TextureHelper.refreshTextureBindState();
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        double length = fr.getStringWidth(locName) * 1.8;
        double offsetLeft = width / 2 - length / 2;
        int offsetTop = guiTop + 45;
        GlStateManager.pushMatrix();
        GlStateManager.translate(offsetLeft + 2, offsetTop, 0);
        GlStateManager.scale(1.8, 1.8, 1.8);
        fr.drawString(locName, 0, 0, 0xAA4D4D4D, false);
        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1, 1);
        GL11.glColor4f(1, 1, 1, 1);
    }

    private void drawConstellation() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderConstellation.renderConstellationIntoGUI(
            new Color(0.4F, 0.4F, 0.4F, 0.8F),
            constellation,
            width / 2 - 145 / 2,
            guiTop + 84,
            zLevel,
            145,
            145,
            2F,
            new RenderConstellation.BrightnessFunction() {

                @Override
                public float getBrightness() {
                    return 0.5F;
                }
            },
            true,
            false);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void drawPhaseInformation() {
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();
        Blending.DEFAULT.applyStateManager();
        GL11.glColor4f(1, 1, 1, 1);
        int size = 16;
        int offsetX = (width / 2) - (phases.size() * (size + 2)) / 2;
        int offsetY = guiTop + 237;
        for (int i = 0; i < phases.size(); i++) {
            MoonPhase ph = phases.get(i);
            MoonPhaseRenderHelper.getMoonPhaseTexture(ph)
                .bind();
            drawRect(offsetX + (i * (size + 2)), offsetY, size, size);
        }
    }

    private void drawScroll() {
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();
        drawWHRect(textureScroll);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
