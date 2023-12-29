/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.util.word;

import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;

import com.google.common.collect.Maps;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RandomWordGenerator
 * Created by HellFirePvP
 * Date: 29.09.2018 / 10:58
 */
public abstract class RandomWordGenerator {

    private static Map<String, RandomWordGenerator> localizedProviders = Maps.newHashMap();
    private static RandomWordGenerator fallback;

    @Nonnull
    public static RandomWordGenerator getGenerator() {
        String lang = Minecraft.getMinecraft().gameSettings.language;
        if (lang == null) {
            return fallback;
        }
        lang = lang.toLowerCase();
        RandomWordGenerator gen;
        if ((gen = localizedProviders.get(lang)) == null) {
            gen = fallback;
        }
        return gen;
    }

    public abstract String generateWord(long seed, int length);

    public static void init() {
        fallback = new WordGeneratorEnglish();

        // Add new/other providers here...
        localizedProviders.put("en_us", fallback);
        localizedProviders.put("zh_cn", new WordGeneratorChinese());
    }
}
