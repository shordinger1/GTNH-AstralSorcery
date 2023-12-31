/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.gui.base;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiTileBase
 * Created by HellFirePvP
 * Date: 02.03.2017 / 16:34
 */
public class GuiTileBase<T extends TileEntity> extends GuiWHScreen {

    private final T te;

    protected GuiTileBase(T te, int guiHeight, int guiWidth) {
        super(guiHeight, guiWidth);
        this.te = te;
    }

    public T getOwningTileEntity() {
        return te;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (te.isInvalid()
            || te.getWorldObj().provider.dimensionId != Minecraft.getMinecraft().theWorld.provider.dimensionId) {
            Minecraft.getMinecraft()
                .displayGuiScreen(null);
        }
    }

}
