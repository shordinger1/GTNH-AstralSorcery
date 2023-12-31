/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.gui.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiInventoryContainerBase
 * Created by HellFirePvP
 * Date: 02.03.2017 / 16:29
 */
public abstract class GuiInventoryContainerBase extends GuiContainer {

    private final TileEntity te;

    public GuiInventoryContainerBase(Container inventorySlotsIn, TileEntity te) {
        super(inventorySlotsIn);
        this.te = te;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    public TileEntity getOwningTileEntity() {
        return te;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (te.isInvalid()) {
            Minecraft.getMinecraft()
                .displayGuiScreen(null);
        }
    }
}
