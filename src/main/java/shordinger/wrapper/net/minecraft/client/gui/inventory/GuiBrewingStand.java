package shordinger.wrapper.net.minecraft.client.gui.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.entity.player.InventoryPlayer;
import shordinger.wrapper.net.minecraft.inventory.ContainerBrewingStand;
import shordinger.wrapper.net.minecraft.inventory.IInventory;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;

@SideOnly(Side.CLIENT)
public class GuiBrewingStand extends GuiContainer {

    private static final ResourceLocation BREWING_STAND_GUI_TEXTURES = new ResourceLocation(
        "textures/gui/container/brewing_stand.png");
    private static final int[] BUBBLELENGTHS = new int[]{29, 24, 20, 16, 11, 6, 0};
    /**
     * The player inventory bound to this GUI.
     */
    private final InventoryPlayer playerInventory;
    private final IInventory tileBrewingStand;

    public GuiBrewingStand(InventoryPlayer playerInv, IInventory inventoryIn) {
        super(new ContainerBrewingStand(playerInv, inventoryIn));
        this.playerInventory = playerInv;
        this.tileBrewingStand = inventoryIn;
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = this.tileBrewingStand.getDisplayName()
            .getUnformattedText();
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
        this.fontRenderer.drawString(
            this.playerInventory.getDisplayName()
                .getUnformattedText(),
            8,
            this.ySize - 96 + 2,
            4210752);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager()
            .bindTexture(BREWING_STAND_GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        int k = this.tileBrewingStand.getField(1);
        int l = MathHelper.clamp((18 * k + 20 - 1) / 20, 0, 18);

        if (l > 0) {
            this.drawTexturedModalRect(i + 60, j + 44, 176, 29, l, 4);
        }

        int i1 = this.tileBrewingStand.getField(0);

        if (i1 > 0) {
            int j1 = (int) (28.0F * (1.0F - (float) i1 / 400.0F));

            if (j1 > 0) {
                this.drawTexturedModalRect(i + 97, j + 16, 176, 0, 9, j1);
            }

            j1 = BUBBLELENGTHS[i1 / 2 % 7];

            if (j1 > 0) {
                this.drawTexturedModalRect(i + 63, j + 14 + 29 - j1, 185, 29 - j1, 12, j1);
            }
        }
    }
}