package shordinger.wrapper.net.minecraft.client.gui.advancements;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.input.Mouse;

import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.advancements.Advancement;
import shordinger.wrapper.net.minecraft.advancements.AdvancementProgress;
import shordinger.wrapper.net.minecraft.client.gui.GuiScreen;
import shordinger.wrapper.net.minecraft.client.multiplayer.ClientAdvancementManager;
import shordinger.wrapper.net.minecraft.client.network.NetHandlerPlayClient;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.client.renderer.RenderHelper;
import shordinger.wrapper.net.minecraft.client.resources.I18n;
import shordinger.wrapper.net.minecraft.network.play.client.CPacketSeenAdvancements;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiScreenAdvancements extends GuiScreen implements ClientAdvancementManager.IListener {

    private static final ResourceLocation WINDOW = new ResourceLocation("textures/gui/advancements/window.png");
    private static final ResourceLocation TABS = new ResourceLocation("textures/gui/advancements/tabs.png");
    private final ClientAdvancementManager clientAdvancementManager;
    private final Map<Advancement, GuiAdvancementTab> tabs = Maps.<Advancement, GuiAdvancementTab>newLinkedHashMap();
    private GuiAdvancementTab selectedTab;
    private int scrollMouseX;
    private int scrollMouseY;
    private boolean isScrolling;
    private static int tabPage, maxPages;

    public GuiScreenAdvancements(ClientAdvancementManager p_i47383_1_) {
        this.clientAdvancementManager = p_i47383_1_;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        this.tabs.clear();
        this.selectedTab = null;
        this.clientAdvancementManager.setListener(this);

        if (this.selectedTab == null && !this.tabs.isEmpty()) {
            this.clientAdvancementManager.setSelectedTab(
                ((GuiAdvancementTab) this.tabs.values()
                    .iterator()
                    .next()).getAdvancement(),
                true);
        } else {
            this.clientAdvancementManager
                .setSelectedTab(this.selectedTab == null ? null : this.selectedTab.getAdvancement(), true);
        }
        if (this.tabs.size() > AdvancementTabType.MAX_TABS) {
            int guiLeft = (this.width - 252) / 2;
            int guiTop = (this.height - 140) / 2;
            this.buttonList.add(new net.minecraft.client.gui.GuiButton(101, guiLeft, guiTop - 50, 20, 20, "<"));
            this.buttonList
                .add(new net.minecraft.client.gui.GuiButton(102, guiLeft + 252 - 20, guiTop - 50, 20, 20, ">"));
            maxPages = this.tabs.size() / AdvancementTabType.MAX_TABS;
        }
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed() {
        this.clientAdvancementManager.setListener((ClientAdvancementManager.IListener) null);
        NetHandlerPlayClient nethandlerplayclient = this.mc.getConnection();

        if (nethandlerplayclient != null) {
            nethandlerplayclient.sendPacket(CPacketSeenAdvancements.closedScreen());
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            int i = (this.width - 252) / 2;
            int j = (this.height - 140) / 2;

            for (GuiAdvancementTab guiadvancementtab : this.tabs.values()) {
                if (guiadvancementtab.getPage() == tabPage && guiadvancementtab.isMouseOver(i, j, mouseX, mouseY)) {
                    this.clientAdvancementManager.setSelectedTab(guiadvancementtab.getAdvancement(), true);
                    break;
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == this.mc.gameSettings.keyBindAdvancements.getKeyCode()) {
            this.mc.displayGuiScreen((GuiScreen) null);
            this.mc.setIngameFocus();
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int i = (this.width - 252) / 2;
        int j = (this.height - 140) / 2;

        if (Mouse.isButtonDown(0)) {
            if (!this.isScrolling) {
                this.isScrolling = true;
            } else if (this.selectedTab != null) {
                this.selectedTab.scroll(mouseX - this.scrollMouseX, mouseY - this.scrollMouseY);
            }

            this.scrollMouseX = mouseX;
            this.scrollMouseY = mouseY;
        } else {
            this.isScrolling = false;
        }

        this.drawDefaultBackground();
        this.renderInside(mouseX, mouseY, i, j);
        this.renderWindow(i, j);
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (maxPages != 0) {
            String page = String.format("%d / %d", tabPage + 1, maxPages + 1);
            int width = this.fontRenderer.getStringWidth(page);
            GlStateManager.disableLighting();
            this.fontRenderer.drawStringWithShadow(page, i + (252 / 2) - (width / 2), j - 44, -1);
        }
        this.renderToolTips(mouseX, mouseY, i, j);
    }

    private void renderInside(int p_191936_1_, int p_191936_2_, int p_191936_3_, int p_191936_4_) {
        GuiAdvancementTab guiadvancementtab = this.selectedTab;

        if (guiadvancementtab == null) {
            drawRect(p_191936_3_ + 9, p_191936_4_ + 18, p_191936_3_ + 9 + 234, p_191936_4_ + 18 + 113, -16777216);
            String s = I18n.format("advancements.empty");
            int i = this.fontRenderer.getStringWidth(s);
            this.fontRenderer.drawString(
                s,
                p_191936_3_ + 9 + 117 - i / 2,
                p_191936_4_ + 18 + 56 - this.fontRenderer.FONT_HEIGHT / 2,
                -1);
            this.fontRenderer.drawString(
                ":(",
                p_191936_3_ + 9 + 117 - this.fontRenderer.getStringWidth(":(") / 2,
                p_191936_4_ + 18 + 113 - this.fontRenderer.FONT_HEIGHT,
                -1);
        } else {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) (p_191936_3_ + 9), (float) (p_191936_4_ + 18), -400.0F);
            GlStateManager.enableDepth();
            guiadvancementtab.drawContents();
            GlStateManager.popMatrix();
            GlStateManager.depthFunc(515);
            GlStateManager.disableDepth();
        }
    }

    public void renderWindow(int p_191934_1_, int p_191934_2_) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        RenderHelper.disableStandardItemLighting();
        this.mc.getTextureManager()
            .bindTexture(WINDOW);
        this.drawTexturedModalRect(p_191934_1_, p_191934_2_, 0, 0, 252, 140);

        if (this.tabs.size() > 1) {
            this.mc.getTextureManager()
                .bindTexture(TABS);

            for (GuiAdvancementTab guiadvancementtab : this.tabs.values()) {
                if (guiadvancementtab.getPage() == tabPage)
                    guiadvancementtab.drawTab(p_191934_1_, p_191934_2_, guiadvancementtab == this.selectedTab);
            }

            GlStateManager.enableRescaleNormal();
            GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
            RenderHelper.enableGUIStandardItemLighting();

            for (GuiAdvancementTab guiadvancementtab1 : this.tabs.values()) {
                if (guiadvancementtab1.getPage() == tabPage)
                    guiadvancementtab1.drawIcon(p_191934_1_, p_191934_2_, this.itemRender);
            }

            GlStateManager.disableBlend();
        }

        this.fontRenderer.drawString(I18n.format("gui.advancements"), p_191934_1_ + 8, p_191934_2_ + 6, 4210752);
    }

    private void renderToolTips(int p_191937_1_, int p_191937_2_, int p_191937_3_, int p_191937_4_) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (this.selectedTab != null) {
            GlStateManager.pushMatrix();
            GlStateManager.enableDepth();
            GlStateManager.translate((float) (p_191937_3_ + 9), (float) (p_191937_4_ + 18), 400.0F);
            this.selectedTab
                .drawToolTips(p_191937_1_ - p_191937_3_ - 9, p_191937_2_ - p_191937_4_ - 18, p_191937_3_, p_191937_4_);
            GlStateManager.disableDepth();
            GlStateManager.popMatrix();
        }

        if (this.tabs.size() > 1) {
            for (GuiAdvancementTab guiadvancementtab : this.tabs.values()) {
                if (guiadvancementtab.getPage() == tabPage
                    && guiadvancementtab.isMouseOver(p_191937_3_, p_191937_4_, p_191937_1_, p_191937_2_)) {
                    this.drawHoveringText(guiadvancementtab.getTitle(), p_191937_1_, p_191937_2_);
                }
            }
        }
    }

    public void rootAdvancementAdded(Advancement advancementIn) {
        GuiAdvancementTab guiadvancementtab = GuiAdvancementTab.create(this.mc, this, this.tabs.size(), advancementIn);

        if (guiadvancementtab != null) {
            this.tabs.put(advancementIn, guiadvancementtab);
        }
    }

    public void rootAdvancementRemoved(Advancement advancementIn) {
    }

    public void nonRootAdvancementAdded(Advancement advancementIn) {
        GuiAdvancementTab guiadvancementtab = this.getTab(advancementIn);

        if (guiadvancementtab != null) {
            guiadvancementtab.addAdvancement(advancementIn);
        }
    }

    public void nonRootAdvancementRemoved(Advancement advancementIn) {
    }

    public void onUpdateAdvancementProgress(Advancement p_191933_1_, AdvancementProgress p_191933_2_) {
        GuiAdvancement guiadvancement = this.getAdvancementGui(p_191933_1_);

        if (guiadvancement != null) {
            guiadvancement.getAdvancementProgress(p_191933_2_);
        }
    }

    public void setSelectedTab(@Nullable Advancement p_193982_1_) {
        this.selectedTab = this.tabs.get(p_193982_1_);
    }

    public void advancementsCleared() {
        this.tabs.clear();
        this.selectedTab = null;
    }

    @Nullable
    public GuiAdvancement getAdvancementGui(Advancement p_191938_1_) {
        GuiAdvancementTab guiadvancementtab = this.getTab(p_191938_1_);
        return guiadvancementtab == null ? null : guiadvancementtab.getAdvancementGui(p_191938_1_);
    }

    @Nullable
    private GuiAdvancementTab getTab(Advancement p_191935_1_) {
        while (p_191935_1_.getParent() != null) {
            p_191935_1_ = p_191935_1_.getParent();
        }

        return this.tabs.get(p_191935_1_);
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    @Override
    protected void actionPerformed(net.minecraft.client.gui.GuiButton button) throws IOException {
        if (button.id == 101) tabPage = Math.max(tabPage - 1, 0);
        else if (button.id == 102) tabPage = Math.min(tabPage + 1, maxPages);
    }
}
