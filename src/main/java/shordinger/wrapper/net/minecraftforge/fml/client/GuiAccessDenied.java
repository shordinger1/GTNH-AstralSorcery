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

package shordinger.wrapper.net.minecraftforge.fml.client;

import shordinger.wrapper.net.minecraft.client.gui.GuiButton;
import shordinger.wrapper.net.minecraft.client.gui.GuiScreen;
import shordinger.wrapper.net.minecraft.client.multiplayer.ServerData;
import shordinger.wrapper.net.minecraft.client.resources.I18n;

public class GuiAccessDenied extends GuiScreen {

    private GuiScreen parent;
    private ServerData data;

    public GuiAccessDenied(GuiScreen parent, ServerData data) {
        this.parent = parent;
        this.data = data;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(1, this.width / 2 - 75, this.height - 38, I18n.format("gui.done")));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    @Override
    protected void actionPerformed(GuiButton p_73875_1_) {
        if (p_73875_1_.enabled && p_73875_1_.id == 1) {
            FMLClientHandler.instance()
                .showGuiScreen(parent);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        int offset = Math.max(85 - 2 * 10, 10);
        this.drawCenteredString(
            this.fontRenderer,
            "Forge Mod Loader could not connect to this server",
            this.width / 2,
            offset,
            0xFFFFFF);
        offset += 10;
        this.drawCenteredString(
            this.fontRenderer,
            String.format("The server %s has forbidden modded access", data.serverName),
            this.width / 2,
            offset,
            0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
