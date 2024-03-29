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
import shordinger.wrapper.net.minecraft.client.gui.GuiOptionButton;
import shordinger.wrapper.net.minecraft.client.resources.I18n;
import shordinger.wrapper.net.minecraftforge.fml.common.StartupQuery;

public class GuiConfirmation extends GuiNotification {

    public GuiConfirmation(StartupQuery query) {
        super(query);
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    @Override
    public void initGui() {
        this.buttonList.add(new GuiOptionButton(0, this.width / 2 - 155, this.height - 38, I18n.format("gui.yes")));
        this.buttonList
            .add(new GuiOptionButton(1, this.width / 2 - 155 + 160, this.height - 38, I18n.format("gui.no")));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled && (button.id == 0 || button.id == 1)) {
            FMLClientHandler.instance()
                .showGuiScreen(null);
            query.setResult(button.id == 0);
            query.finish();
        }
    }
}
