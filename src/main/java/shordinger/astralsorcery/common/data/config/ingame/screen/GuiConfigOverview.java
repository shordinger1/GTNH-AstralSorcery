/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.data.config.ingame.screen;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import com.google.common.collect.Lists;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.data.config.Config;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiConfigOverview
 * Created by HellFirePvP
 * Date: 27.06.2018 / 22:35
 */
public class GuiConfigOverview extends GuiConfig {

    public GuiConfigOverview(GuiScreen parentScreen) {
        super(
            parentScreen,
            buildConfigList(),
            AstralSorcery.MODID,
            false,
            false,
            I18n.format("astralsorcery.config.title.overview"));
    }

    private static List<IConfigElement> buildConfigList() {
        List<IConfigElement> out = Lists.newLinkedList();
        Config.getAvailableConfigurations()
            .forEach((key, value) -> out.add(ConfigHelper.getCategoryElement(key, value)));
        return out;
    }

}
