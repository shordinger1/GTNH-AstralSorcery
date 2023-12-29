/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.core;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralCore
 * Created by HellFirePvP
 * Date: 07.05.2016 / 02:55
 */
@IFMLLoadingPlugin.Name(value = "AstralCore")
@IFMLLoadingPlugin.TransformerExclusions({"shordinger.astralsorcery.core"})
@IFMLLoadingPlugin.SortingIndex(1005)
public class AstralCore implements IFMLLoadingPlugin, IFMLCallHook {

    public static final Logger log = LogManager.getLogger("Astral Core");

    public static Side side;

    public AstralCore() {
        log.info("[AstralCore] Initialized.");
        side = FMLLaunchHandler.side();
    }

    @Override
    public Void call() throws Exception {
        return null;
    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return "shordinger.astralsorcery.core.AstralTransformer";
    }

}
