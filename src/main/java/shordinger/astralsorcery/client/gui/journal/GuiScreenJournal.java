/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.gui.journal;

import java.awt.*;
import java.util.*;
import java.util.List;

import net.minecraft.client.Minecraft;
import shordinger.astralsorcery.migration.BufferBuilder;
import com.gtnewhorizons.modularui.api.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import shordinger.astralsorcery.migration.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import shordinger.astralsorcery.client.data.KnowledgeFragmentData;
import shordinger.astralsorcery.client.data.PersistentDataManager;
import shordinger.astralsorcery.client.gui.GuiJournalProgression;
import shordinger.astralsorcery.client.gui.base.GuiWHScreen;
import shordinger.astralsorcery.client.gui.journal.bookmark.BookmarkProvider;
import shordinger.astralsorcery.client.gui.journal.overlay.GuiJournalOverlayKnowledge;
import shordinger.astralsorcery.client.util.TextureHelper;
import shordinger.astralsorcery.client.util.resource.AbstractRenderableTexture;
import shordinger.astralsorcery.client.util.resource.AssetLibrary;
import shordinger.astralsorcery.client.util.resource.AssetLoader;
import shordinger.astralsorcery.client.util.resource.BindableResource;
import shordinger.astralsorcery.common.data.fragment.KnowledgeFragment;
import shordinger.astralsorcery.common.lib.Sounds;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.SoundHelper;
import shordinger.astralsorcery.migration.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiScreenJournal
 * Created by HellFirePvP
 * Date: 15.08.2016 / 12:40
 */
public abstract class GuiScreenJournal extends GuiWHScreen {

    public static final BindableResource textureResBlank = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "guijblankbook");
    public static final BindableResource textureResShell = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "guijspacebook");
    public static final BindableResource textureResShellCst = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "guijspaceconstellation");
    public static final BindableResource textureKnBookmark = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "guiknowledgebookmark");
    public static final BindableResource textureKnBookmarkStr = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "guiknowledgebookmarkstretched");

    protected final int bookmarkIndex;

    protected static List<BookmarkProvider> bookmarks = Lists.newLinkedList();

    protected Map<Rectangle, BookmarkProvider> drawnBookmarks = Maps.newHashMap();
    protected Collection<KnowledgeFragment> fragmentList = null;
    protected Map<Rectangle, KnowledgeFragment> pageFragments = Maps.newHashMap();

    public GuiScreenJournal(int bookmarkIndex) {
        super(270, 420);
        this.bookmarkIndex = bookmarkIndex;
    }

    public static boolean addBookmark(BookmarkProvider bookmarkProvider) {
        int index = bookmarkProvider.getIndex();
        return !MiscUtils.contains(bookmarks, bm -> bm.getIndex() == index) && bookmarks.add(bookmarkProvider);
    }

    private void resolveFragments() {
        KnowledgeFragmentData data = PersistentDataManager.INSTANCE
            .getData(PersistentDataManager.PersistentKey.KNOWLEDGE_FRAGMENTS);
        this.fragmentList = data.getFragmentsFor(this);
    }

    public void drawDefault(BindableResource background, Point mouse) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        zLevel += 100; // To ensure that it over-renders items conflicting with the shell.
        drawWHRect(background);
        drawBookmarks(zLevel, mouse);
        zLevel -= 100;

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private void drawBookmarks(float zLevel, Point mousePoint) {
        if (fragmentList == null) {
            resolveFragments();
        }
        drawnBookmarks.clear();

        GL11.glPushMatrix();
        GL11.glColor4f(1F, 1F, 1F, 1F);

        double bookmarkWidth = 67;
        double bookmarkHeight = 15;
        double bookmarkGap = 18;

        double knBookmarkWidth = 83;

        double offsetX = guiLeft + guiWidth - 17.25;
        double offsetY = guiTop + 20;

        bookmarks.sort(Comparator.comparing(BookmarkProvider::getIndex));

        for (BookmarkProvider bookmarkProvider : bookmarks) {
            if (bookmarkProvider.canSee()) {
                Rectangle r = drawBookmark(
                    offsetX,
                    offsetY,
                    bookmarkWidth,
                    bookmarkHeight,
                    bookmarkWidth + (bookmarkIndex == bookmarkProvider.getIndex() ? 0 : 5),
                    zLevel,
                    bookmarkProvider.getUnlocalizedName(),
                    0xDDDDDDDD,
                    mousePoint,
                    bookmarkProvider.getTextureBookmark(),
                    bookmarkProvider.getTextureBookmarkStretched());
                drawnBookmarks.put(r, bookmarkProvider);
                offsetY += bookmarkGap;
            }
        }

        offsetY += bookmarkGap / 2;

        this.pageFragments.clear();
        for (KnowledgeFragment frag : this.fragmentList) {
            if (frag.isFullyPresent()) {
                offsetY += bookmarkGap;
                Rectangle rctFragment = drawBookmark(
                    offsetX,
                    offsetY,
                    knBookmarkWidth,
                    bookmarkHeight,
                    knBookmarkWidth + (bookmarkIndex == 0 ? 0 : 5),
                    zLevel,
                    frag.getUnlocalizedBookmark(),
                    0xDDDDDDDD,
                    mousePoint,
                    textureKnBookmark,
                    textureKnBookmarkStr);
                this.pageFragments.put(rctFragment, frag);
            }
        }

        GL11.glPopMatrix();
    }

    private Rectangle drawBookmark(double offsetX, double offsetY, double width, double height, double mouseOverWidth,
                                   float zLevel, String title, int titleRGBColor, Point mousePoint, AbstractRenderableTexture texture,
                                   AbstractRenderableTexture textureStretched) {
        TextureHelper.setActiveTextureToAtlasSprite();
        // Reset styles, because MC fontrenderer is STUPID A F
        if (titleRGBColor == Color.WHITE.getRGB()) {
            fontRenderer.drawString("", 0, 0, Color.BLACK.getRGB());
        } else {
            fontRenderer.drawString("", 0, 0, Color.WHITE.getRGB());
        }
        GL11.glPushMatrix();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GlStateManager.color(1F, 1F, 1F, 1F);
        texture.bindTexture();

        Rectangle r = new Rectangle(
            MathHelper.floor(offsetX),
            MathHelper.floor(offsetY),
            MathHelper.floor(width),
            MathHelper.floor(height));
        if (r.contains(mousePoint)) {
            if (mouseOverWidth > width) {
                textureStretched.bindTexture();
            }
            width = mouseOverWidth;
            r = new Rectangle(
                MathHelper.floor(offsetX),
                MathHelper.floor(offsetY),
                MathHelper.floor(width),
                MathHelper.floor(height));
        }

        Tessellator tes = Tessellator.instance;
        BufferBuilder vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX, offsetY + height, zLevel)
            .tex(0, 1)
            .endVertex();
        vb.pos(offsetX + width, offsetY + height, zLevel)
            .tex(1, 1)
            .endVertex();
        vb.pos(offsetX + width, offsetY, zLevel)
            .tex(1, 0)
            .endVertex();
        vb.pos(offsetX, offsetY, zLevel)
            .tex(0, 0)
            .endVertex();
        tes.draw();

        GL11.glPushMatrix();
        GL11.glTranslated(offsetX + 2, offsetY + 4, zLevel + 50);
        GL11.glScaled(0.7, 0.7, 0.7);
        fontRenderer.drawString(I18n.format(title), 0, 0, titleRGBColor);
        GL11.glPopMatrix();

        GlStateManager.color(1F, 1F, 1F, 1F);

        GL11.glPopMatrix();

        return r;
    }

    protected boolean handleBookmarkClick(Point p) {
        return handleJournalNavigationBookmarkClick(p) || handleFragmentClick(p);
    }

    private boolean handleJournalNavigationBookmarkClick(Point p) {
        for (Rectangle bookmarkRectangle : drawnBookmarks.keySet()) {
            BookmarkProvider provider = drawnBookmarks.get(bookmarkRectangle);
            if (bookmarkIndex != provider.getIndex() && bookmarkRectangle.contains(p)) {
                GuiJournalProgression.resetJournal();
                Minecraft.getMinecraft()
                    .displayGuiScreen(provider.getGuiScreen());
                return true;
            }
        }
        return false;
    }

    private boolean handleFragmentClick(Point mouse) {
        for (Rectangle r : this.pageFragments.keySet()) {
            if (r.contains(mouse)) {
                if (this instanceof GuiJournalProgression) {
                    ((GuiJournalProgression) this).expectReinit = true;
                }
                KnowledgeFragment frag = this.pageFragments.get(r);
                Minecraft.getMinecraft()
                    .displayGuiScreen(new GuiJournalOverlayKnowledge(this, frag));
                SoundHelper.playSoundClient(Sounds.bookFlip, 1F, 1F);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
