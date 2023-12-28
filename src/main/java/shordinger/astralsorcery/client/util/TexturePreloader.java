/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.util;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.util.resource.AssetLibrary;
import shordinger.astralsorcery.client.util.resource.AssetLoader;
import shordinger.astralsorcery.common.data.config.Config;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TexturePreloader
 * Created by HellFirePvP
 * Date: 21.09.2016 / 15:44
 */
public class TexturePreloader {

    public static void doPreloadRoutine() {
        // Needs to happen...
        AstralSorcery.log.info("[AssetLibrary] Preload mandatory textures");
        TexturePreloader.preloadMandatoryTextures();

        if (Config.clientPreloadTextures) {
            long startMs = System.currentTimeMillis();
            AstralSorcery.log.info("[AssetLibrary] Preload textures");
            TexturePreloader.preloadTextures();
            AstralSorcery.log.info("[AssetLibrary] Initializing sprite library");
            SpriteLibrary.init();
            AstralSorcery.log
                .info("[AssetLibrary] Texture Preloading took " + (System.currentTimeMillis() - startMs) + "ms!");
        } else {
            AstralSorcery.log.info("[AssetLibrary] Skipping preloading textures (configured).");
        }
    }

    private static void preloadMandatoryTextures() {
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijblankbook")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijspacebook")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijspaceconstellation")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guicontippaper")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guicontippaper_blank")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijbookmark")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijbookmarkstretched")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiknowledgebookmark")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiknowledgebookmarkstretched")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijarrow")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "underline")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiresbg")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiresbg2")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiresbgcst")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijresoverlay")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "hud_charge_frame")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "hud_charge_charge")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "hud_item_frame")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "hud_item_frame_extender")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "observatoryframe")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "gridsextant")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiscructpreviewicons")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "slotgemcontext")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "connectionperks")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.ENVIRONMENT, "star1")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.ENVIRONMENT, "star2")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.ENVIRONMENT, "connection")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.ENVIRONMENT, "solareclipse")
            .allocateGlId();
    }

    private static void preloadTextures() {
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "cloud1")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "cloud2")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "cloud3")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "cloud4")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "cloud5")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "cloud6")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "cloud7")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "cloud8")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiconpaper")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guialtar1")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guialtar2")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guialtar3")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "griddisc")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "gridatt")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "gridcst")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijstoragebook")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guidrawing")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guidrawing_empty")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "lightbeam")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "burst1")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "burst2")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "burst3")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "ceffect1")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "ceffect2")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "ceffect3")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "lightningpart")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "starlight_store")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "halo1")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "halo2")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "halo7")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "star1")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "star2")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "flarestar")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "flarestatic")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "flaresmall")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "flareperkinactive")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "flareperkactive")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "flareperkactivateable")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "unlock_perk")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "flare1")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "flare2")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "charge")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "vortex1")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "g_explode_blue")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "g_explode_gray")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "g_explode_red")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "g_explode_white")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "potion_cheatdeath")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "potion_bleed")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "moon_full")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "moon_waning1")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "moon_waning2")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "moon_waning3")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "moon_new")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "moon_waxing1")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "moon_waxing2")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "moon_waxing3")
            .allocateGlId();
        AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "smoke")
            .allocateGlId();

        SpriteLibrary.init(); // Loads all spritesheets
    }

}
