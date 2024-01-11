/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.gui.journal.overlay;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import shordinger.astralsorcery.client.gui.journal.GuiScreenJournal;
import shordinger.astralsorcery.client.gui.journal.GuiScreenJournalOverlay;
import shordinger.astralsorcery.client.util.Blending;
import shordinger.astralsorcery.client.util.TextureHelper;
import shordinger.astralsorcery.client.util.resource.AssetLibrary;
import shordinger.astralsorcery.client.util.resource.AssetLoader;
import shordinger.astralsorcery.client.util.resource.BindableResource;
import shordinger.astralsorcery.common.data.fragment.KnowledgeFragment;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.gui.FontRenderer;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalOverlayKnowledge
 * Created by HellFirePvP
 * Date: 26.09.2018 / 12:51
 */
public class GuiJournalOverlayKnowledge extends GuiScreenJournalOverlay {

    public static final BindableResource textureKnowledgeOverlay = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "guicontippaper_blank");
    private static final int HEADER_WIDTH = 190;
    private static final int DEFAULT_WIDTH = 175;

    private final KnowledgeFragment knowledgeFragment;
    private final List<String> lines = new LinkedList<>();

    public GuiJournalOverlayKnowledge(GuiScreenJournal origin, KnowledgeFragment display) {
        super(origin);
        this.knowledgeFragment = display;
    }

    @Override
    public void initGui() {
        super.initGui();

        String text = this.knowledgeFragment.getLocalizedPage();
        for (String segment : text.split("<NL>")) {
            lines.addAll(fontRenderer.listFormattedStringToWidth(segment, DEFAULT_WIDTH));
            lines.add("");
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();

        int width = 275;
        int height = 344;

        textureKnowledgeOverlay.bindTexture();
        drawTexturedRect(
            guiLeft + guiWidth / 2 - width / 2,
            guiTop + guiHeight / 2 - height / 2,
            width,
            height,
            textureKnowledgeOverlay);

        drawHeader();
        drawPageText();

        GlStateManager.enableDepth();
        TextureHelper.refreshTextureBindState();
    }

    private void drawPageText() {
        GlStateManager.color(1, 1, 1, 1);
        int offsetY = guiTop + 40;
        int offsetX = guiLeft + guiWidth / 2 - DEFAULT_WIDTH / 2;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            fontRenderer.drawString(line, offsetX, offsetY + (i * 10), 0xEE333333, false);
        }
        GlStateManager.color(1, 1, 1, 1);
    }

    private void drawHeader() {
        String locTitle = this.knowledgeFragment.getLocalizedIndexName();
        TextureHelper.refreshTextureBindState();
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        List<String> split = fr.listFormattedStringToWidth(locTitle, MathHelper.floor(HEADER_WIDTH / 1.4));
        int step = 14;

        int offsetTop = guiTop + 15 - (split.size() * step) / 2;

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, offsetTop, 0);
        for (int i = 0; i < split.size(); i++) {
            String s = split.get(i);

            double offsetLeft = width / 2 - (fr.getStringWidth(s) * 1.4) / 2;
            GlStateManager.pushMatrix();
            GlStateManager.translate(offsetLeft, i * step, 0);
            GlStateManager.scale(1.4, 1.4, 1.4);
            fr.drawString(s, 0, 0, 0xEE333333, false);
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1, 1);
        GL11.glColor4f(1, 1, 1, 1);
    }

    public KnowledgeFragment getKnowledgeFragment() {
        return knowledgeFragment;
    }

}
