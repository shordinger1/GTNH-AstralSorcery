/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.data.config.ingame.screen;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import com.google.common.collect.Lists;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import shordinger.astralsorcery.Tags;
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
            Tags.MODID,
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
