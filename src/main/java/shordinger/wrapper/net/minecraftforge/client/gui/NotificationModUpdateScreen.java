/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package shordinger.wrapper.net.minecraftforge.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.gui.GuiButton;
import shordinger.wrapper.net.minecraft.client.gui.GuiMainMenu;
import shordinger.wrapper.net.minecraft.client.gui.GuiScreen;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraftforge.common.ForgeModContainer;
import shordinger.wrapper.net.minecraftforge.common.ForgeVersion;
import shordinger.wrapper.net.minecraftforge.common.ForgeVersion.Status;
import shordinger.wrapper.net.minecraftforge.fml.common.Loader;
import shordinger.wrapper.net.minecraftforge.fml.common.ModContainer;

@SideOnly(Side.CLIENT)
public class NotificationModUpdateScreen extends GuiScreen {

    private static final ResourceLocation VERSION_CHECK_ICONS = new ResourceLocation(
        ForgeVersion.MOD_ID,
        "textures/gui/version_check_icons.png");

    private final GuiButton modButton;
    private Status showNotification = null;
    private boolean hasCheckedForUpdates = false;

    public NotificationModUpdateScreen(GuiButton modButton) {
        this.modButton = modButton;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    @Override
    public void initGui() {
        if (!hasCheckedForUpdates) {
            if (modButton != null) {
                for (ModContainer mod : Loader.instance()
                    .getModList()) {
                    Status status = ForgeVersion.getResult(mod).status;
                    if (status == Status.OUTDATED || status == Status.BETA_OUTDATED) {
                        // TODO: Needs better visualization, maybe stacked icons
                        // drawn in a terrace-like pattern?
                        showNotification = Status.OUTDATED;
                    }
                }
            }
            hasCheckedForUpdates = true;
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (showNotification == null || !showNotification.shouldDraw() || ForgeModContainer.disableVersionCheck) {
            return;
        }

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(VERSION_CHECK_ICONS);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.pushMatrix();

        int x = modButton.x;
        int y = modButton.y;
        int w = modButton.width;
        int h = modButton.height;

        drawModalRectWithCustomSizedTexture(
            x + w - (h / 2 + 4),
            y + (h / 2 - 4),
            showNotification.getSheetOffset() * 8,
            (showNotification.isAnimated() && ((System.currentTimeMillis() / 800 & 1) == 1)) ? 8 : 0,
            8,
            8,
            64,
            16);
        GlStateManager.popMatrix();
    }

    public static NotificationModUpdateScreen init(GuiMainMenu guiMainMenu, GuiButton modButton) {
        NotificationModUpdateScreen notificationModUpdateScreen = new NotificationModUpdateScreen(modButton);
        notificationModUpdateScreen.setGuiSize(guiMainMenu.width, guiMainMenu.height);
        notificationModUpdateScreen.initGui();
        return notificationModUpdateScreen;
    }

}
