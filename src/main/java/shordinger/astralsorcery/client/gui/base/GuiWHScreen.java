/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.gui.base;

import shordinger.astralsorcery.client.util.resource.AbstractRenderableTexture;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.gui.GuiScreen;
import shordinger.wrapper.net.minecraft.client.renderer.BufferBuilder;
import shordinger.wrapper.net.minecraft.client.renderer.Tessellator;
import shordinger.wrapper.net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiWHScreen
 * Created by HellFirePvP
 * Date: 11.08.2016 / 20:06
 */
public abstract class GuiWHScreen extends GuiScreen {

    protected final int guiHeight;
    protected final int guiWidth;
    protected int guiLeft, guiTop;

    protected boolean closeWithInventoryKey = true;

    protected GuiWHScreen(int guiHeight, int guiWidth) {
        this.guiHeight = guiHeight;
        this.guiWidth = guiWidth;
    }

    @Override
    public void initGui() {
        super.initGui();

        initComponents();
    }

    public int getGuiHeight() {
        return guiHeight;
    }

    public int getGuiLeft() {
        return guiLeft;
    }

    public int getGuiTop() {
        return guiTop;
    }

    public int getGuiWidth() {
        return guiWidth;
    }

    private void initComponents() {
        guiLeft = width / 2 - guiWidth / 2;
        guiTop = height / 2 - guiHeight / 2;
    }

    protected void drawWHRect(AbstractRenderableTexture resource) {
        resource.bindTexture();
        drawRect(guiLeft, guiTop, guiWidth, guiHeight);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if(closeWithInventoryKey && keyCode == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode()) {
            Minecraft.getMinecraft().displayGuiScreen(null);

            if(Minecraft.getMinecraft().currentScreen == null) {
                Minecraft.getMinecraft().setIngameFocus();
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if(mouseButton == 1 && !handleRightClickClose(mouseX, mouseY)) {
            Minecraft.getMinecraft().displayGuiScreen(null);

            if(Minecraft.getMinecraft().currentScreen == null) {
                Minecraft.getMinecraft().setIngameFocus();
            }
        }
    }

    /**
     * @return false if rightclick is not handled any other way and allow for close. true to deny rightclick close and handle otherwise;
     */
    protected boolean handleRightClickClose(int mouseX, int mouseY) {
        return false;
    }

    public Point getCurrentMousePoint() {
        int guiMouseX =          Mouse.getEventX() * width  / mc.displayWidth;
        int guiMouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
        return new Point(guiMouseX, guiMouseY);
    }

    protected void drawTexturedRect(double offsetX, double offsetY, double width, double height, Rectangle.Float uvBounds) {
        drawTexturedRect(offsetX, offsetY, width, height, uvBounds.x, uvBounds.y, uvBounds.width, uvBounds.height);
    }

    protected void drawTexturedRect(double offsetX, double offsetY, double width, double height, float uFrom, float vFrom, float uWidth, float vWidth) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, zLevel).tex(uFrom,          vFrom + vWidth).endVertex();
        vb.pos(offsetX + width, offsetY + height, zLevel).tex(uFrom + uWidth, vFrom + vWidth).endVertex();
        vb.pos(offsetX + width, offsetY,          zLevel).tex(uFrom + uWidth, vFrom)         .endVertex();
        vb.pos(offsetX,         offsetY,          zLevel).tex(uFrom,          vFrom)         .endVertex();
        tes.draw();
    }

    protected void drawTexturedRect(double offsetX, double offsetY, double width, double height, AbstractRenderableTexture tex) {
        Point.Double off = tex.getUVOffset();
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, zLevel).tex(off.x,          off.y + tex.getVWidth()).endVertex();
        vb.pos(offsetX + width, offsetY + height, zLevel).tex(off.x + tex.getUWidth(), off.y + tex.getVWidth()).endVertex();
        vb.pos(offsetX + width, offsetY,          zLevel).tex(off.x + tex.getUWidth(), off.y)         .endVertex();
        vb.pos(offsetX,         offsetY,          zLevel).tex(off.x,          off.y)         .endVertex();
        tes.draw();
    }

    protected void drawTexturedRectAtCurrentPos(double width, double height, float uFrom, float vFrom, float uWidth, float vWidth) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(0,         0 + height, zLevel).tex(uFrom,          vFrom + vWidth).endVertex();
        vb.pos(0 + width, 0 + height, zLevel).tex(uFrom + uWidth, vFrom + vWidth).endVertex();
        vb.pos(0 + width, 0,          zLevel).tex(uFrom + uWidth, vFrom)         .endVertex();
        vb.pos(0,         0,          zLevel).tex(uFrom,          vFrom)         .endVertex();
        tes.draw();
    }

    protected void drawTexturedRectAtCurrentPos(double width, double height) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(0,         0 + height, zLevel).tex(0, 1).endVertex();
        vb.pos(0 + width, 0 + height, zLevel).tex(1, 1).endVertex();
        vb.pos(0 + width, 0,          zLevel).tex(1, 0).endVertex();
        vb.pos(0,         0,          zLevel).tex(0, 0).endVertex();
        tes.draw();
    }

    protected void drawRectDetailed(float offsetX, float offsetY, float width, float height) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, zLevel).tex(0, 1).endVertex();
        vb.pos(offsetX + width, offsetY + height, zLevel).tex(1, 1).endVertex();
        vb.pos(offsetX + width, offsetY,          zLevel).tex(1, 0).endVertex();
        vb.pos(offsetX,         offsetY,          zLevel).tex(0, 0).endVertex();
        tes.draw();
    }

    protected void drawRect(int offsetX, int offsetY, int width, int height) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, zLevel).tex(0, 1).endVertex();
        vb.pos(offsetX + width, offsetY + height, zLevel).tex(1, 1).endVertex();
        vb.pos(offsetX + width, offsetY,          zLevel).tex(1, 0).endVertex();
        vb.pos(offsetX,         offsetY,          zLevel).tex(0, 0).endVertex();
        tes.draw();
    }

}
