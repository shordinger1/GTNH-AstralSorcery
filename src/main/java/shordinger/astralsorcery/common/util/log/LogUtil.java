/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util.log;

import java.util.HashSet;
import java.util.Set;

import net.minecraftforge.common.config.Configuration;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.data.config.entry.ConfigEntry;
import shordinger.astralsorcery.common.util.Provider;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LogUtil
 * Created by HellFirePvP
 * Date: 06.04.2019 / 13:11
 */
public class LogUtil {

    private static boolean loggingEnabled = false;
    private static final Set<LogCategory> loggedCategories = new HashSet<>();
    private static final String PREFIX = "[DEBUG-%s]: %s";

    public static void info(LogCategory category, Provider<String> msgProvider) {
        if (loggingEnabled && loggedCategories.contains(category)) {
            AstralSorcery.log.info(String.format(PREFIX, category.name(), msgProvider.provide()));
        }
    }

    public static void warn(LogCategory category, Provider<String> msgProvider) {
        if (loggingEnabled && loggedCategories.contains(category)) {
            AstralSorcery.log.warn(String.format(PREFIX, category.name(), msgProvider.provide()));
        }
    }

    public static class CfgEntry extends ConfigEntry {

        public CfgEntry() {
            super(Section.GENERAL, "debug_logging");
        }

        @Override
        public void loadFromConfig(Configuration cfg) {
            loggedCategories.clear();

            for (LogCategory cat : LogCategory.values()) {
                boolean enabled = cfg.getBoolean(
                    cat.name(),
                    getConfigurationSection(),
                    false,
                    "Set to true to enable this logging category. Only do this if you have to debug this section of code! May spam your log HEAVILY!");
                if (enabled) {
                    loggedCategories.add(cat);
                }
            }

            loggingEnabled = !loggedCategories.isEmpty();
        }
    }

}
