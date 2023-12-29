/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

import shordinger.astralsorcery.AstralSorcery;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TextureHelper
 * Created by HellFirePvP
 * Date: 30.09.2016 / 16:31
 */
public class TextureHelper {

    private static final ResourceLocation blackSpaceholder = new ResourceLocation(
        AstralSorcery.MODID,
        "textures/misc/black.png");
    public static ResourceLocation texFontRenderer = new ResourceLocation("textures/font/ascii.png");

    public static ResourceLocation getBlockAtlasTexture() {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    public static void refreshTextureBindState() {
        Minecraft.getMinecraft().renderEngine.bindTexture(blackSpaceholder);
    }

    public static void setActiveTextureToAtlasSprite() {
        Minecraft.getMinecraft().renderEngine.bindTexture(getBlockAtlasTexture());
    }

}
