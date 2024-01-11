/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.data.config.ingame;

import shordinger.astralsorcery.common.data.config.ingame.screen.GuiConfigOverview;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.gui.GuiScreen;
import shordinger.wrapper.net.minecraftforge.fml.client.IModGuiFactory;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConfigGuiFactory
 * Created by HellFirePvP
 * Date: 27.06.2018 / 21:08
 */
@SideOnly(Side.CLIENT)
public class ConfigGuiFactory implements IModGuiFactory {

    @Override
    public void initialize(Minecraft minecraftInstance) {}

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new GuiConfigOverview(parentScreen);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

}
