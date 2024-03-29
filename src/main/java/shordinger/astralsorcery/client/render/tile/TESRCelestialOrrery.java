/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.render.tile;

import shordinger.astralsorcery.client.util.resource.AssetLibrary;
import shordinger.astralsorcery.client.util.resource.AssetLoader;
import shordinger.astralsorcery.client.util.resource.BindableResource;
import shordinger.astralsorcery.common.tile.TileCelestialOrrery;
import shordinger.wrapper.net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRCelestialOrrery
 * Created by HellFirePvP
 * Date: 15.02.2017 / 22:49
 */
public class TESRCelestialOrrery extends TileEntitySpecialRenderer<TileCelestialOrrery> {

    public static final BindableResource texSmoke = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "smoke");

    @Override
    public void render(TileCelestialOrrery te, double x, double y, double z, float partialTicks, int destroyStage,
                       float alpha) {

    }

}
