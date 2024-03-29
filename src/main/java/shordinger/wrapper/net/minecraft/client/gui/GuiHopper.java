package shordinger.wrapper.net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.gui.inventory.GuiContainer;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.entity.player.InventoryPlayer;
import shordinger.wrapper.net.minecraft.inventory.ContainerHopper;
import shordinger.wrapper.net.minecraft.inventory.IInventory;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiHopper extends GuiContainer {

    /**
     * The ResourceLocation containing the gui texture for the hopper
     */
    private static final ResourceLocation HOPPER_GUI_TEXTURE = new ResourceLocation(
        "textures/gui/container/hopper.png");
    /**
     * The player inventory currently bound to this GUI instance
     */
    private final IInventory playerInventory;
    /**
     * The hopper inventory bound to this GUI instance
     */
    private final IInventory hopperInventory;

    public GuiHopper(InventoryPlayer playerInv, IInventory hopperInv) {
        super(new ContainerHopper(playerInv, hopperInv, Minecraft.getMinecraft().thePlayer));
        this.playerInventory = playerInv;
        this.hopperInventory = hopperInv;
        this.allowUserInput = false;
        this.ySize = 133;
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
        this.fontRenderer.drawString(
            this.hopperInventory.getDisplayName()
                .getUnformattedText(),
            8,
            6,
            4210752);
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
            .bindTexture(HOPPER_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }
}
