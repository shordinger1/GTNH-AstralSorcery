/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.util;

import shordinger.astralsorcery.client.util.resource.AssetLibrary;
import shordinger.astralsorcery.client.util.resource.AssetLoader;
import shordinger.astralsorcery.client.util.resource.BindableResource;
import shordinger.astralsorcery.client.util.resource.SpriteSheetResource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SpriteLibrary
 * Created by HellFirePvP
 * Date: 26.09.2016 / 00:03
 */
public class SpriteLibrary {

    private static final BindableResource texBurst1 = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "ceffect1");
    private static final BindableResource texBurst2 = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "ceffect2");
    private static final BindableResource texBurst3 = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "ceffect3");
    private static final BindableResource texBurstEffect3 = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "burst3");
    private static final BindableResource flareActive = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "flareperkactive");
    private static final BindableResource flareInactive = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "flareperkinactive");
    private static final BindableResource flareActivateable = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "flareperkactivateable");
    private static final BindableResource flareActivate = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "unlock_perk");
    private static final BindableResource texCraftBurst = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "burst1");
    private static final BindableResource texCollectorBurst = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "burst2");
    private static final BindableResource texLightbeam = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "lightbeam");
    private static final BindableResource texHalo1 = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "halo1");
    private static final BindableResource texHalo2 = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "halo2");
    private static final BindableResource texHalo3 = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "halo3");
    private static final BindableResource texHalo4 = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "halo4");
    private static final BindableResource texHalo5 = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "halo5");
    private static final BindableResource texHalo6 = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "halo6");
    private static final BindableResource texHalo7 = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "halo7");
    private static final BindableResource texVortex1 = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "vortex1");
    private static final BindableResource texStar1 = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "star1");
    private static final BindableResource texStar2 = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "star2");
    private static final BindableResource texFlare1 = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "flare1");
    private static final BindableResource texFlare2 = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "flare2");
    private static final BindableResource texFlareDyn = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "patreonflares/gray_mono");
    private static final BindableResource texCharge = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "charge");
    private static final BindableResource texHook = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "hook");
    private static final BindableResource texGemExplodeBlue = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "g_explode_blue");
    private static final BindableResource texGemExplodeGray = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "g_explode_gray");
    private static final BindableResource texGemExplodeRed = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "g_explode_red");
    private static final BindableResource texGemExplodeWhite = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "g_explode_white");
    private static final BindableResource texSpriteStarlight = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "starlight_store");
    private static final BindableResource texLiquidStarlight = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.BLOCKS, "fluid/starlight_still");

    public static final SpriteSheetResource spriteStarlight = texSpriteStarlight.asSpriteSheet(16, 4);
    public static final SpriteSheetResource spriteLightbeam = texLightbeam.asSpriteSheet(4, 16);
    public static final SpriteSheetResource spriteHalo1 = texHalo1.asSpriteSheet(6, 8);
    public static final SpriteSheetResource spriteHalo2 = texHalo2.asSpriteSheet(8, 8);
    public static final SpriteSheetResource spriteVortex1 = texVortex1.asSpriteSheet(5, 8);
    public static final SpriteSheetResource spriteBurstEffect3 = texBurstEffect3.asSpriteSheet(7, 8);
    public static final SpriteSheetResource spriteHalo3 = texHalo3.asSpriteSheet(4, 8);
    public static final SpriteSheetResource spriteHalo4 = texHalo4.asSpriteSheet(4, 8);
    public static final SpriteSheetResource spriteHalo5 = texHalo5.asSpriteSheet(4, 8);
    public static final SpriteSheetResource spriteHalo6 = texHalo6.asSpriteSheet(4, 8);
    public static final SpriteSheetResource spriteGemExplodeBlue = texGemExplodeBlue.asSpriteSheet(5, 8);
    public static final SpriteSheetResource spriteGemExplodeGray = texGemExplodeGray.asSpriteSheet(5, 8);
    public static final SpriteSheetResource spriteGemExplodeRed = texGemExplodeRed.asSpriteSheet(5, 8);
    public static final SpriteSheetResource spriteGemExplodeWhite = texGemExplodeWhite.asSpriteSheet(5, 8);
    public static final SpriteSheetResource spriteStar1 = texStar1.asSpriteSheet(6, 8);
    public static final SpriteSheetResource spriteStar2 = texStar2.asSpriteSheet(6, 8);
    public static final SpriteSheetResource spritePerkInactive = flareInactive.asSpriteSheet(5, 8);
    public static final SpriteSheetResource spritePerkActive = flareActive.asSpriteSheet(5, 8);
    public static final SpriteSheetResource spritePerkActivateable = flareActivateable.asSpriteSheet(5, 8);
    public static final SpriteSheetResource spritePerkActivate = flareActivate.asSpriteSheet(5, 16);
    public static final SpriteSheetResource spritePerkSeal = texHalo7.asSpriteSheet(4, 8);
    public static final SpriteSheetResource spriteCelestialBurst1 = texBurst1.asSpriteSheet(5, 8);
    public static final SpriteSheetResource spriteCelestialBurst2 = texBurst2.asSpriteSheet(5, 8);
    public static final SpriteSheetResource spriteCelestialBurst3 = texBurst3.asSpriteSheet(5, 8);
    public static final SpriteSheetResource spriteCraftBurst = texCraftBurst.asSpriteSheet(6, 8);
    public static final SpriteSheetResource spriteFlare1 = texFlare1.asSpriteSheet(6, 8);
    public static final SpriteSheetResource spriteFlare2 = texFlare2.asSpriteSheet(8, 8);
    public static final SpriteSheetResource spriteDynColorFlare = texFlareDyn.asSpriteSheet(1, 48);
    public static final SpriteSheetResource spriteCharge = texCharge.asSpriteSheet(8, 4);
    public static final SpriteSheetResource spriteHook = texHook.asSpriteSheet(4, 8);
    public static final SpriteSheetResource spriteCollectorBurst = texCollectorBurst.asSpriteSheet(5, 16);
    public static final SpriteSheetResource spriteLiquidStarlight = texLiquidStarlight.asSpriteSheet(64, 1);

    public static void init() {
    }

}
