/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.base.patreon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.base.patreon.data.PatreonEffectData;
import shordinger.astralsorcery.common.base.patreon.data.PatreonEffectType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatreonDataManager
 * Created by HellFirePvP
 * Date: 16.02.2019 / 16:56
 */
public class PatreonDataManager {

    private static final String PATREON_EFFECT_URL = "http://hellfiredev.net/patreon.json";
    private static final Gson GSON = new GsonBuilder().create();

    public static void loadPatreonEffects() {
        Thread tr = new Thread(() -> {
            URLConnection conn;
            try {
                conn = new URL(PATREON_EFFECT_URL).openConnection();
            } catch (IOException e) {
                AstralSorcery.log.error("Failed to connect to patreon fileserver! Not loading patreon files...");
                e.printStackTrace();

                PatreonEffectHelper.loadingFinished = true;
                return;
            }

            PatreonEffectData data;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                data = GSON.fromJson(br, PatreonEffectData.class);
            } catch (IOException e) {
                AstralSorcery.log.error("Failed to connect to patreon fileserver! Not loading patreon files...");
                e.printStackTrace();

                PatreonEffectHelper.loadingFinished = true;
                return;
            }

            int skipped = 0;
            for (PatreonEffectData.EffectEntry entry : data.getEffectList()) {
                UUID plUuid;
                PatreonEffectType type;
                try {
                    plUuid = UUID.fromString(entry.getUuid());
                    type = PatreonEffectType.valueOf(entry.getEffectClass());
                } catch (Exception exc) {
                    skipped++;
                    continue;
                }

                try {
                    PatreonEffectHelper.PatreonEffect pe = type.getProvider()
                        .buildEffect(plUuid, entry.getParameters());

                    pe.initialize();
                    PatreonEffectHelper.effectMap.computeIfAbsent(plUuid, uuid -> new ArrayList<>())
                        .add(pe);
                } catch (Exception exc) {
                    skipped++;
                }
            }

            if (skipped > 0) {
                AstralSorcery.log.warn("Skipped " + skipped + " patreon effects during loading due to malformed data!");
            }
            AstralSorcery.log.info("Patreon effect loading finished.");

            // UUID hellfire = UUID.fromString("7f6971c5-fb58-4519-a975-b1b5766e92d1");
            // PatreonEffectHelper.PatreonEffect pe = new PtEffectCorruptedCelestialCrystal(
            // UUID.fromString("7f6971c5-fb58-4519-a975-b1b5766e44d1"),
            // PatreonEffectHelper.FlareColor.FIRE);
            // PatreonEffectHelper.PatreonEffect pe =
            // new PtEffectCrystalFootprint(UUID.fromString("7f6971c5-fb58-4519-a975-b1b5766e92d1"),
            // PatreonEffectHelper.FlareColor.WATER, hellfire,
            // new Color(Integer.parseInt("14287086")));
            // PatreonEffectHelper.PatreonEffect pe =
            // new PtEffectBlockRing(UUID.fromString("7f6971c5-fb58-4519-a975-b1b5766e92d1"),
            // PatreonEffectHelper.FlareColor.WATER,
            // hellfire,
            // 4,
            // 7.5F,
            // 8,
            // 5000,
            // new HashMap<>());
            // pe.initialize();
            // PatreonEffectHelper.effectMap.get(hellfire).add(pe);

            PatreonEffectHelper.loadingFinished = true;
        });
        tr.setName("AstralSorcery Patreon Effect Loader");
        tr.start();
    }

}
