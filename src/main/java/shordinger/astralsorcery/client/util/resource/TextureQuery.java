/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.util.resource;

import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TextureQuery
 * Created by HellFirePvP
 * Date: 31.03.2017 / 14:13
 */
public class TextureQuery {

    private final AssetLoader.TextureLocation location;
    private final String name;

    private Object resolvedResource;

    public TextureQuery(AssetLoader.TextureLocation location, String name) {
        this.location = location;
        this.name = name;
    }

    @SideOnly(Side.CLIENT)
    public AbstractRenderableTexture resolve() {
        if(resolvedResource == null) {
            resolvedResource = AssetLibrary.loadTexture(location, name);
        }
        return (AbstractRenderableTexture) resolvedResource;
    }

}
